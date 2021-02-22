package com.tsinghua.course.Frame.Aspect;

import com.tsinghua.course.Base.Annotation.NeedLogin;
import com.tsinghua.course.Base.Enum.UserType;
import com.tsinghua.course.Base.Error.CourseWarn;
import com.tsinghua.course.Base.Error.UserWarnEnum;
import com.tsinghua.course.Base.Model.User;
import com.tsinghua.course.Frame.Util.ThreadUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * @描述 登录切面，用户需要登录才可以访问对应内容
 **/
@Aspect
@Component
public class LogInAspect {
    /** 权限控制器，所有带 com.tsinghua.course.Base.Annotation.NeedLogin 注解的函数在执行前都会进入此切面 */
    @Around("@annotation(com.tsinghua.course.Base.Annotation.NeedLogin)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        /** 获取用户 */
        User user = ThreadUtil.getUser();
        if (user == null)
            throw new CourseWarn(UserWarnEnum.NEED_LOGIN);
        /** 获取能够访问的用户类型 */
        MethodSignature signature = (MethodSignature)pjp.getSignature();
        NeedLogin needLogin = signature.getMethod().getAnnotation(NeedLogin.class);
        UserType[] userTypes = needLogin.value();

        /** 用户类型长度为0说明不限制用户权限 */
        if (userTypes.length != 0) {
            boolean canAccess = false;
            for (UserType userType:userTypes)
                if (userType.equals(user.getUserType())) {
                    canAccess = true;
                    break;
                }
            if (!canAccess)
                throw new CourseWarn(UserWarnEnum.PERMISSION_DENIED);
        }
        /** 执行业务内部逻辑 */
        return pjp.proceed();
    }
}
