package com.tsinghua.course.Base.Enum;

/**
 * @描述 用户类型枚举
 **/
public enum UserType {
    NORMAL("普通用户"),
    ADMIN("管理员")
    ;

    UserType(String name) {
        this.name = name;
    }

    String name;

    public String getName() {
        return name;
    }
}
