package com.tsinghua.course.Biz.Controller.Params.DefaultParams.Out;

import com.tsinghua.course.Biz.Controller.Params.CommonOutParams;

/**
 * @描述 默认系统内部错误返回参数
 **/
public class SysErrorOutParams extends CommonOutParams {
    // 错误编码
    private String code;
    // 错误描述信息
    private String msg;

    public SysErrorOutParams() {
        code = "500";
        msg = "服务器内部错误";
        success = false;
    }
}
