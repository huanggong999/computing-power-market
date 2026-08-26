package com.lingyang.cloud.service;

import com.lingyang.cloud.model.dto.FileUploadDTO;
import com.lingyang.cloud.model.vo.FileUploadVO;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/29 17:07
 */
public interface FileService {
    /**
     * 文件上传
     * @param fileUploadDTO 文件对象
     * @return 结果
     */
    FileUploadVO upload(FileUploadDTO fileUploadDTO);

    FileUploadVO uploadInputStream(FileUploadDTO fileUploadDTO);


    FileUploadVO upload(String base64);

    FileUploadVO uploadOcr(FileUploadDTO fileUploadDTO);

    FileUploadVO uploadIdCardOcr(FileUploadDTO fileUploadDTO);
}
