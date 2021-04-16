package com.tsinghua.course.Base.Error;

/**
 * @描述 自定义错误类，此错误会被记录在错误日志中
 **/
public class CourseError extends Exception {
    /** 错误编码 */
    private String errorCode;
    /** 错误描述 */
    private String errorMsg;

    public CourseError(ExceptionInterface exceptionInterface) {
        errorCode = exceptionInterface.getErrorCode();
        errorMsg = exceptionInterface.getErrorMessage();
    }

    public CourseError(String code, String msg) {
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
