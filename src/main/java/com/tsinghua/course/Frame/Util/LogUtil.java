package com.tsinghua.course.Frame.Util;

import com.tsinghua.course.Base.Constant.GlobalConstant;
import com.tsinghua.course.Base.Constant.NameConstant;
import com.tsinghua.course.Biz.BizTypeEnum;
import com.tsinghua.course.Base.Error.CourseError;
import com.tsinghua.course.Base.Error.CourseWarn;
import com.tsinghua.course.Biz.Controller.Params.CommonInParams;
import com.tsinghua.course.CourseApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @描述 日志组件，负责记录日志，日志功能对追踪用户操作，定位服务器异常有重大作用，良好的日志框架是优秀服务器必备之功能。
 *       一个良好的日志框架可以让服务器的运维事半功倍。
 **/
public class LogUtil {
    private static Logger allLogger = LoggerFactory.getLogger(CourseApplication.class);
    private static Logger timerLogger = LoggerFactory.getLogger(NameConstant.TIMER_LOG);

    /** 记录错误日志 */
    public static void ERROR(String username, BizTypeEnum bizType, Object args, Throwable error) {
        StringBuilder sb = new StringBuilder();
        sb.append(username)
                .append(GlobalConstant.LOG_SPLIT)
                .append(bizType)
                .append(GlobalConstant.LOG_SPLIT)
                .append(args.toString())
                .append(GlobalConstant.LOG_SPLIT);
        if (error == null)
            sb.append("null");
        else if (error instanceof CourseError) /** 自定义错误记录错误码 */
            sb.append(((CourseError)error).getErrorCode());
        else    /** 非自定义错误记录具体的内容 */
            sb.append(error.toString());
        sb.append(GlobalConstant.LOG_SPLIT)
                .append(ParseUtil.getErrorStackInfo(error));
        allLogger.error(sb.toString());
    }

    /** 记录警告日志 */
    public static void WARN(String username, BizTypeEnum bizType, Object args, CourseWarn state) {
        String sb = username + GlobalConstant.LOG_SPLIT +
                bizType + GlobalConstant.LOG_SPLIT +
                args.toString() + GlobalConstant.LOG_SPLIT +
                (state == null ? "null" : state.getErrorCode()) + GlobalConstant.LOG_SPLIT +
                ParseUtil.getWarnStackInfo(state);
        allLogger.warn(sb);
    }

    /** 记录信息日志 */
    public static void INFO(Object... objects) {
        StringBuilder sb = new StringBuilder();
        for (Object obj:objects)
            sb.append(obj.toString()).append(GlobalConstant.LOG_SPLIT);
        allLogger.info(sb.toString());
    }

    /** 定时任务的日志 */
    public static void TIMER(String username, BizTypeEnum bizType, CommonInParams params) {
        String sb = username + GlobalConstant.LOG_SPLIT +
                bizType + GlobalConstant.LOG_SPLIT +
                params.toString();
        timerLogger.info(sb);
    }
}
