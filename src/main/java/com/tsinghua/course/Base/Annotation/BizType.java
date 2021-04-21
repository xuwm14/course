package com.tsinghua.course.Base.Annotation;

import com.tsinghua.course.Biz.BizTypeEnum;

import java.lang.annotation.*;

/**
 * @描述 业务类型注解，注解在类名上为指定业务的入参类，注解在方法上为指定业务的执行函数
 **/
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BizType {
    BizTypeEnum value();
}
