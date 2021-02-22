package com.tsinghua.course.Base.Model;

import com.tsinghua.course.Base.Enum.UserType;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

/**
 * @描述 对应mongodb中的User集合，mongodb是非关系型数据库，可以存储的对象类型很丰富，使用起来方便很多
 **/
@Document("User")
public class User {
    /** 子对象文档 */
    public static class SubObj {
        /** 存储的时间 */
        String time;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
    // mongodb唯一id
    String id;
    // 用户名
    String username;
    // 密码
    String password;
    // 用户类型
    UserType userType;
    // 测试数组
    String[] testArr;
    // 测试对象
    Map<String, String> testObj;
    // 另一个测试对象，和 Map<String, String> 方式存储的格式是一样的，但是直观很多
    SubObj subObj;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String[] getTestArr() {
        return testArr;
    }

    public void setTestArr(String[] testArr) {
        this.testArr = testArr;
    }

    public Map<String, String> getTestObj() {
        return testObj;
    }

    public void setTestObj(Map<String, String> testObj) {
        this.testObj = testObj;
    }

    public SubObj getSubObj() {
        return subObj;
    }

    public void setSubObj(SubObj subObj) {
        this.subObj = subObj;
    }
}
