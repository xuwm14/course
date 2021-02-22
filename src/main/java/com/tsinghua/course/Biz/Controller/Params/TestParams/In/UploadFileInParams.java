package com.tsinghua.course.Biz.Controller.Params.TestParams.In;

import com.tsinghua.course.Base.Annotation.BizType;
import com.tsinghua.course.Base.Enum.BizTypeEnum;
import com.tsinghua.course.Biz.Controller.Params.CommonInParams;
import io.netty.handler.codec.http.multipart.FileUpload;

/**
 * @描述 文件上传测试的入参
 **/
@BizType(BizTypeEnum.FILE_UPLOAD_TEST)
public class UploadFileInParams extends CommonInParams {
    // 用户上传的文件
    private FileUpload file;

    public FileUpload getFile() {
        return file;
    }
}
