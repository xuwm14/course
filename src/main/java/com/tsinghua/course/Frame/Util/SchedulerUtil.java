package com.tsinghua.course.Frame.Util;

import com.tsinghua.course.Base.Constant.KeyConstant;
import com.tsinghua.course.Biz.Controller.Params.CommonInParams;
import com.tsinghua.course.Biz.Handler.TimerJobHandler;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @描述 定时任务组件，使用quartz为系统提供定时任务功能
 **/
@Component
public class SchedulerUtil {

    @Autowired
    private Scheduler scheduler;

    /** 根据id删除定时任务 */
    public void deleteJob(String id) throws SchedulerException {
        scheduler.deleteJob(new JobKey(id));
    }

    /** 增加一次性定时任务，指定延迟后执行 */
    public void addScheduleJob(CommonInParams params, int timeDelay, String id) throws Exception {
        addScheduleJob(params, timeDelay, 2000, 0, id);
    }

    /** 增加一次性定时任务，指定时间执行 */
    public void addScheduleJob(CommonInParams params, Date date, String id) throws Exception {
        addScheduleJob(params, date, 2000, 0, id);
    }

    /** 增加循环类型定时任务，指定延迟后循环执行 */
    public void addScheduleJob(CommonInParams params, int timeDelay, int interval, int nums, String id) throws Exception {
        addScheduleJob(params, new Date(System.currentTimeMillis() + timeDelay), interval, nums, id);
    }

    /** 增加循环类型定时任务，指定时间后循环执行 */
    public void addScheduleJob(CommonInParams params, Date startTime, int interval, int nums, String id) throws Exception {
        JobDetail jobDetail = JobBuilder.newJob(TimerJobHandler.class).withIdentity(id).build();
        jobDetail.getJobDataMap().put(KeyConstant.PARAMS, params);

        /** 设置第一次执行的时间 */
        TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger().startAt(startTime);

        /** 如果需要重复执行，设置重复执行的时间间隔 */
        if (nums > 0) {
            SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(interval).withRepeatCount(nums);
            triggerBuilder.withSchedule(scheduleBuilder);
        } else if (nums == -1) {
            SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(interval).withRepeatCount(10000);
            triggerBuilder.withSchedule(scheduleBuilder);
        }

        /** 开始计划，quartz会自动在指定时间执行任务，我们不需要关注其中的细节，如果要取消定时任务，通过 deleteJob + jobId 来进行 */
        scheduler.scheduleJob(jobDetail, triggerBuilder.build());
        /** 计划执行的具体函数为 CourseJob.execute 函数 */
        scheduler.start();
    }
}
