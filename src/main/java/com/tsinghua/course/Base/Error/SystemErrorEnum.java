package com.tsinghua.course.Base.Error;

/**
 * @描述 系统错误枚举
 **/
public enum SystemErrorEnum implements ExceptionInterface {
    CONTROLLER_NOT_FIND("SysError001", "未找到执行业务的类"),

    METHOD_NOT_FIND("SysError002", "未找到执行业务的函数"),

    PARAMS_ERROR("SysError003", "参数个数或类型错误"),

    PARAMS_TRANSFER_ERROR("SysError004", "参数转换失败"),

    RETURN_PARAMS_ERROR("SysError005", "业务执行结果参数有误"),

    BIZ_TYPE_NOT_EXIST("SysError006", "业务类型不存在")
    ;

    SystemErrorEnum(String code, String msg) {
        errorCode = code;
        errorMsg = msg;
    }

    private String errorCode;
    private String errorMsg;

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMessage() {
        return errorMsg;
    }
}
