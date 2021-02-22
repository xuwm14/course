package com.tsinghua.course.Biz.Controller.Params.TestParams.Out;

import com.tsinghua.course.Biz.Controller.Params.CommonOutParams;

/**
 * @描述 文件下载测试的出参
 **/
public class DownloadFileOutParams extends CommonOutParams {
    // 文件的url
    private String url;

    public void setUrl(String url) {
        this.url = url;
    }
}
