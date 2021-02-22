package com.tsinghua.course.Biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tsinghua.course.Base.Annotation.BizType;
import com.tsinghua.course.Base.Constant.NameConstant;
import com.tsinghua.course.Base.Enum.BizTypeEnum;
import com.tsinghua.course.Base.Error.CourseError;
import com.tsinghua.course.Base.Error.CourseWarn;
import com.tsinghua.course.Base.Error.SystemErrorEnum;
import com.tsinghua.course.Base.Model.User;
import com.tsinghua.course.Biz.Controller.Params.CommonInParams;
import com.tsinghua.course.Biz.Controller.Params.CommonOutParams;
import com.tsinghua.course.Biz.Controller.Params.DefaultParams.Out.SysErrorOutParams;
import com.tsinghua.course.Biz.Controller.Params.DefaultParams.Out.SysWarnOutParams;
import com.tsinghua.course.Biz.Processor.UserProcessor;
import com.tsinghua.course.Frame.Util.LogUtil;
import com.tsinghua.course.Frame.Util.ThreadUtil;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;
import java.util.Set;

/**
 * @描述 事件分发器，根据输入的参数来决定使用哪个类和函数来执行逻辑
 **/
@Component
public class Dispatcher {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private UserProcessor userProcessor;

    /**
     * 事件分发器
     * 所有的业务在经过 Handler 处理后转入 Controller 内部执行时都需要经过事件分发器
     * 目前用到分发器的有：①处理web的http请求；②处理socket长连接中客户端发送的请求；③处理定时任务中执行的请求；
     * 由分发器根据业务的参数，决定哪个业务逻辑由哪个类的哪个函数执行
     * 这只是一个最简单的分发器，可以根据业务逻辑的复杂程度定制不同复杂度的分发器
     **/
    public String dispatch(CommonInParams params) {
        String username = params.getUsername();
        BizTypeEnum bitType = params.getBizType();

        try {
            /** 获取执行业务的类 */
            Class<?> exeCls = bitType.getControllerClass();
            if (exeCls == null)
                throw new CourseError(SystemErrorEnum.CONTROLLER_NOT_FIND);
            /** 获取类中执行业务的函数 */
            Method exeMethod = getMethodByOptType(exeCls, bitType);
            if (exeMethod == null)
                throw new CourseError(SystemErrorEnum.METHOD_NOT_FIND);

            /** 参数正常与否的判断 */
            Class<?>[] methodParams = exeMethod.getParameterTypes();
            if (methodParams.length != 1 || !CommonInParams.class.isAssignableFrom(methodParams[0]))
                throw new CourseError(SystemErrorEnum.PARAMS_ERROR);

            /** 获取用户并存在线程中，可以让以后的操作不需要重复获取用户 */
            if (username != null && bitType != BizTypeEnum.USER_LOGIN) {
                User user = userProcessor.getUserByUsername(username);
                ThreadUtil.setUser(user);
            }

            /** 执行业务，返回结果 */
            Object exeBean = applicationContext.getBean(exeCls);
            Object rlt = exeMethod.invoke(exeBean, params);
            if (rlt == null) /** 定时任务可以不返回参数，因为不需要传参数给客户端 */
                return new JSONObject().toString();
            else if (rlt instanceof List) { /** 如果返回多个参数要封装一下 */
                JSONArray retArr = new JSONArray();
                List<CommonOutParams> rlts = (List<CommonOutParams>)rlt;
                for (CommonOutParams obj : rlts)
                    retArr.add(obj.toJsonObject());
                return retArr.toString();
            } else if (rlt instanceof CommonOutParams) /** 否则直接返回参数 */
                return rlt.toString();
            else /** 不允许其它类型的参数返回值 */
                throw new CourseError(SystemErrorEnum.RETURN_PARAMS_ERROR);
        } catch (Exception e) {
            Throwable realError = e;
            boolean isWarning = false;

            /** 获取报错的类型，判断是警告还是真实错误 */
            if (e instanceof InvocationTargetException) {
                /** InvocationTargetException 代表业务内部逻辑执行有错误 */
                realError = ((InvocationTargetException)e).getTargetException();
                /** UndeclaredThrowableException 代表内部还有错误，获取更内层的错误 */
                if (realError instanceof UndeclaredThrowableException)
                    realError = ((UndeclaredThrowableException)realError).getUndeclaredThrowable();
            }
            if (realError instanceof CourseWarn) {
                isWarning = true;
            }
            if (isWarning) {
                CourseWarn courseWarn = (CourseWarn)realError;
                /** 记录警告日志，并返回警告的参数 */
                LogUtil.WARN(username, bitType, params, courseWarn);
                return new SysWarnOutParams(courseWarn).toString();
            } else {
                /** 记录错误日志并返回服务器内部错误 */
                LogUtil.ERROR(username, bitType, params, realError);
                return new SysErrorOutParams().toString();
            }
        }
    }

    /** 根据类名和操作类型获取执行业务的具体函数 */
    private Method getMethodByOptType(Class<?> cls, BizTypeEnum bitType) {
        Method method = null;
        Method[] methods = cls.getDeclaredMethods();
        for (Method m : methods) {
            /** 根据注解获取执行函数 */
            BizType cType = m.getAnnotation(BizType.class);
            if (cType != null && cType.value().equals(bitType)) {
                method = m;
                break;
            }
        }
        return method;
    }

    /** 根据操作类型获取入参类 */
    public Class<CommonInParams> getParamByBizType(BizTypeEnum bitType) {
        Reflections reflections = new Reflections(NameConstant.PACKAGE_NAME + ".Biz.Controller.Params");
        /** 根据注解获取入参类 */
        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(BizType.class);
        for (Class<?> cls:classSet) {
            if (!CommonInParams.class.isAssignableFrom(cls))
                continue;
            Class<CommonInParams> paramCls = (Class<CommonInParams>)cls;
            BizType bizType1 = paramCls.getAnnotation(BizType.class);
            if (bizType1.value().equals(bitType))
                return paramCls;
        }
        /** 没有找到则认为使用默认入参类 */
        return CommonInParams.class;
    }
}
