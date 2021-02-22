package com.tsinghua.course.Biz.Controller.Params.DefaultParams.Out;

import com.tsinghua.course.Base.Error.CourseWarn;
import com.tsinghua.course.Biz.Controller.Params.CommonOutParams;

/**
 * @描述 默认系统内部警告返回参数
 **/
public class SysWarnOutParams extends CommonOutParams {
    // 错误编码
    private String code;
    // 错误描述信息
    private String msg;

    public SysWarnOutParams(CourseWarn warn) {
        code = warn.getErrorCode();
        msg = warn.getErrorMsg();
        success = false;
    }
}
