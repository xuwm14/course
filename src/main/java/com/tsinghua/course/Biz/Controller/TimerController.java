package com.tsinghua.course.Biz.Controller;

import com.tsinghua.course.Base.Annotation.BizType;
import com.tsinghua.course.Base.Enum.BizTypeEnum;
import com.tsinghua.course.Biz.Controller.Params.TestParams.In.LogTestInParams;
import com.tsinghua.course.Frame.Util.LogUtil;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @描述 定时任务控制器，用于执行定时任务
 **/
@Component
public class TimerController {

    /** 定时任务的具体执行载体 */
    @BizType(BizTypeEnum.LOG_TEST)
    public void logTest(LogTestInParams inParams) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LogUtil.INFO("定时任务启动时间：" + dateFormat.format(inParams.getStartTime()) + "；定时任务执行时间：" + dateFormat.format(new Date()));
    }
}
