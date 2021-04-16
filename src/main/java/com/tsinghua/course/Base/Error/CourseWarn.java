package com.tsinghua.course.Base.Error;

/**
 * @描述 自定义警告类，此错误会被记录在警告日志中
 **/
public class CourseWarn extends Exception {
    /** 错误编码 */
    private String errorCode;
    /** 错误描述 */
    private String errorMsg;

    public CourseWarn(ExceptionInterface exceptionInterface) {
        errorCode = exceptionInterface.getErrorCode();
        errorMsg = exceptionInterface.getErrorMessage();
    }

    public CourseWarn(String code, String msg) {
        errorCode = code;
        errorMsg = msg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
