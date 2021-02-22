package com.tsinghua.course.Base.Error;

/** 错误信息接口，用于给抛出的自定义错误提供编码和描述 */
public interface ExceptionInterface {
    String getErrorCode();

    String getErrorMessage();
}
