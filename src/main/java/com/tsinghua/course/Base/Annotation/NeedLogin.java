package com.tsinghua.course.Base.Annotation;

import com.tsinghua.course.Base.Enum.UserType;

import java.lang.annotation.*;

/**
 * @描述 登录注解，用户需要登录才可以访问对应内容
 **/
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NeedLogin {
    /** 需要对应的用户权限才可以访问该接口 */
    UserType[] value() default {};
}
