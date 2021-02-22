package com.tsinghua.course.Biz.Controller.Params;

/**
 * @描述 通用出参，所有的出参都需要继承自此类
 **/
public class CommonOutParams extends CommonParams {
    /** 业务执行成功与否 */
    protected boolean success;
    /** 业务执行的具体时间，系统会在返回前自动填充 */
    protected Long time;

    public CommonOutParams() {
        this.success = true;
    }

    public CommonOutParams(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    protected void beforeTransfer() {
        if (time == null)
            time = System.currentTimeMillis();
    }
}
