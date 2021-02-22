package com.tsinghua.course.Biz.Controller;

import com.tsinghua.course.Base.Annotation.NeedLogin;
import com.tsinghua.course.Base.Annotation.BizType;
import com.tsinghua.course.Base.Constant.NameConstant;
import com.tsinghua.course.Base.Enum.BizTypeEnum;
import com.tsinghua.course.Base.Enum.UserType;
import com.tsinghua.course.Biz.Controller.Params.CommonInParams;
import com.tsinghua.course.Biz.Controller.Params.CommonOutParams;
import com.tsinghua.course.Biz.Controller.Params.TestParams.In.LogTestInParams;
import com.tsinghua.course.Biz.Controller.Params.TestParams.Out.DownloadFileOutParams;
import com.tsinghua.course.Biz.Controller.Params.TestParams.Out.MultiRetOutParams;
import com.tsinghua.course.Biz.Controller.Params.TestParams.In.UploadFileInParams;
import com.tsinghua.course.Biz.Controller.Params.TestParams.Out.UploadFileOutParams;
import com.tsinghua.course.Biz.Processor.TestProcessor;
import com.tsinghua.course.Frame.Util.RedisUtil;
import com.tsinghua.course.Frame.Util.SchedulerUtil;
import io.netty.handler.codec.http.multipart.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @描述 测试控制器，用于执行测试业务，书写正式代码时可以删除
 **/
@Component
public class TestController {
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    SchedulerUtil schedulerUtil;

    @Autowired
    TestProcessor testProcessor;

    /** 登录后才可以访问此接口 */
    @NeedLogin
    @BizType(BizTypeEnum.LOGIN_TEST)
    public CommonOutParams loginTest(CommonInParams inParams) {
        return new CommonOutParams(true);
    }

    /** 管理员才可以访问此接口 */
    @NeedLogin(value = { UserType.ADMIN })
    @BizType(BizTypeEnum.ADMIN_TEST)
    public CommonOutParams adminTest(CommonInParams inParams) {
        return new CommonOutParams(true);
    }

    /** 测试是否可以访问redis缓存 */
    @BizType(BizTypeEnum.REDIS_TEST)
    public CommonOutParams redisTest(CommonInParams inParams) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        redisUtil.setKey("test", dateFormat.format(new Date()));
        return new CommonOutParams(true);
    }

    /** 测试定时器功能是否正常 */
    @BizType(BizTypeEnum.TIMER_TEST)
    public CommonOutParams timerTest(CommonInParams inParams) throws Exception {
        /** 设置定时任务的参数 */
        LogTestInParams testInParams = new LogTestInParams();
        testInParams.setStartTime(new Date());
        testInParams.setUsername(inParams.getUsername());
        testInParams.setBizType(BizTypeEnum.LOG_TEST);

        /** 五秒后执行定时任务，定时任务id为testJob */
        schedulerUtil.deleteJob(NameConstant.TEST_JOB); // 先删除已存在的定时任务，否则会报错
        schedulerUtil.addScheduleJob(testInParams, 5000, NameConstant.TEST_JOB);
        return new CommonOutParams(true);
    }

    /** 测试内部报错是否正常 */
    @BizType(BizTypeEnum.ERROR_TEST)
    public CommonOutParams errorTest(CommonInParams inParams) {
        /**
         * 此处会报数组越界错误，如果客户端收到 服务器内部错误 的报错，说明功能正常
         * 可以在error.log日志中查看报错的具体信息，方便定位错误
         * 以后遇到服务器内部错误，不需要调试，可以直接通过日志来追踪根源
         */
        int[] testArr = new int[2];
        testArr[3] = 1;
        return new CommonOutParams(true);
    }

    /** 测试文件上传是否正常 */
    @BizType(BizTypeEnum.FILE_UPLOAD_TEST)
    public CommonOutParams fileTest(UploadFileInParams inParams) throws Exception {
        UploadFileOutParams outParams = new UploadFileOutParams();
        FileUpload file = inParams.getFile();
        if (file == null)
            outParams.setSuccess(false);
        else {
            outParams.setSuccess(true);
            outParams.setFilename(file.getFilename());
            outParams.setFileSize(file.getFile().length());
        }
        return outParams;
    }

    /** 获取文件下载的路径 */
    @NeedLogin
    @BizType(BizTypeEnum.FILE_DOWNLOAD_TEST)
    public CommonOutParams fileDownTest(CommonInParams inParams) throws Exception {
        DownloadFileOutParams outParams = new DownloadFileOutParams();
        outParams.setUrl("http://8.140.133.34:7998/man.png");
        return outParams;
    }

    /** 测试多个返回值，dispatch会自动封装多个返回值 */
    @BizType(BizTypeEnum.MULTI_RETURN_TEST)
    public List<CommonOutParams> multiReturnTest(CommonInParams inParams) {
        List<CommonOutParams> retParams = new ArrayList<>();
        /** 可以重复增加返回参数 */
        for (int i = 0; i < 3; ++i) {
            MultiRetOutParams outParams = new MultiRetOutParams();
            outParams.setNum(i);
            outParams.setSuccess(true);
            retParams.add(outParams);
        }
        UploadFileOutParams uploadFileOutParams = new UploadFileOutParams();
        uploadFileOutParams.setSuccess(false);
        retParams.add(uploadFileOutParams);
        return retParams;
    }

    /** mongodb数据库功能测试 */
    @BizType(BizTypeEnum.MONGODB_TEST)
    public CommonOutParams mongodbTest(CommonInParams inParams) {
        /** 执行完操作后可以在mongodb中查看执行的结果 */
        testProcessor.addTimeStr();
        testProcessor.setTimeAttr();
        return new CommonOutParams(true);
    }
}
