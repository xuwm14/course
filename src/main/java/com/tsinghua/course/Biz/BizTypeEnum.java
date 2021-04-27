package com.tsinghua.course.Biz;

import com.tsinghua.course.Biz.Controller.TestController;
import com.tsinghua.course.Biz.Controller.TimerController;
import com.tsinghua.course.Biz.Controller.UserController;

import io.netty.handler.codec.http.HttpMethod;

/**
 * @描述 业务类型枚举，所有的业务类型都需要枚举在此类中
 **/
public enum BizTypeEnum {
    /** 以下为用户业务类型 */
    USER_LOGIN(UserController.class, "/user/login", "用户登录", HttpMethod.POST),

    /** 定时任务业务测试 */
    LOG_TEST(TimerController.class, null, "定时日志测试", HttpMethod.GET),

    /** 测试业务，在书写正式代码时可以删除，在书写正式代码前先运行测试业务，如果测试业务无问题说明各模块正常 */
    LOGIN_TEST(TestController.class, "/test/loginPermission", "登录控制测试", HttpMethod.GET),
    ADMIN_TEST(TestController.class, "/test/adminPermission", "管理员权限控制测试", HttpMethod.GET),
    REDIS_TEST(TestController.class, "/test/redis", "redis缓存测试", HttpMethod.GET),
    TIMER_TEST(TestController.class, "/test/timer", "定时器测试", HttpMethod.GET),
    ERROR_TEST(TestController.class, "/test/error", "内部报错测试", HttpMethod.GET),
    FILE_UPLOAD_TEST(TestController.class, "/test/upload", "文件上传测试", HttpMethod.GET),
    FILE_DOWNLOAD_TEST(TestController.class, "/test/url", "获取文件下载的路径", HttpMethod.GET),
    MULTI_RETURN_TEST(TestController.class, "/test/multiParams", "返回多个参数的测试", HttpMethod.GET),
    MONGODB_TEST(TestController.class, "/test/mongodb", "mongodb数据库功能测试", HttpMethod.GET);

    BizTypeEnum(Class<?> controller, String httpPath, String description, HttpMethod method) {
        this.controller = controller;
        this.description = description;
        this.httpPath = httpPath;
        this.method = method;
    }

    /** 执行业务具体的类 */
    Class<?> controller;
    /** 业务对应的http请求路径 */
    String httpPath;
    /** 业务描述 */
    String description;

    HttpMethod method;

    public Class<?> getControllerClass() {
        return controller;
    }

    public String getDescription() {
        return description;
    }

    public String getHttpPath() {
        return httpPath;
    }

    public HttpMethod getHttpMethod(){
        return method;
    }
}
