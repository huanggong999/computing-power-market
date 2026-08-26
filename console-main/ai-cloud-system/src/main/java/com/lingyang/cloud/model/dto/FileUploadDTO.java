package com.lingyang.cloud.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lingyang.common.core.utils.FileUtils;
import com.lingyang.common.core.utils.IdUtils;
import com.lingyang.common.core.utils.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Locale;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/4/9 10:49
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1385992813061330197L;

    @Schema(description = "文件对象")
    private MultipartFile multipartFile;

    @Schema(description = "路径前缀")
    private String pathPrefix;


    private InputStream inputStream;

    public FileUploadDTO(MultipartFile multipartFile, String pathPrefix)  {
        this.multipartFile = multipartFile;
        this.pathPrefix = pathPrefix;
    }


    @JsonIgnore
    public String getFileName() {
        String fileName = FileUtils.extractFileName(multipartFile);
        if (StringUtils.isEmpty(fileName)) {
            fileName = IdUtils.simpleUUID();
        }
        return fileName;
    }

    @JsonIgnore
    public String getPath(String fileName){
        if (StringUtils.isNotEmpty(pathPrefix)) {
            if (!pathPrefix.endsWith("/") && !fileName.startsWith("/")) {
                fileName = "/" + fileName;
            }
            fileName = pathPrefix + fileName;
        }
        return fileName.toLowerCase(Locale.ROOT);
    }
}
