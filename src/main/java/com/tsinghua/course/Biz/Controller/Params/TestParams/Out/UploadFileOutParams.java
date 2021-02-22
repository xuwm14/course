package com.tsinghua.course.Biz.Controller.Params.TestParams.Out;

import com.tsinghua.course.Biz.Controller.Params.CommonOutParams;

/**
 * @描述 文件上传测试的出参
 **/
public class UploadFileOutParams extends CommonOutParams {
    // 上传文件的大小
    private Long fileSize;
    // 上传的文件名称
    private String filename;

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
