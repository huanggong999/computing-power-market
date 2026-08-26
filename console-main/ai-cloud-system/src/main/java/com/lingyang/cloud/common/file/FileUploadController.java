package com.lingyang.cloud.common.file;

import com.lingyang.cloud.model.dto.FileUploadDTO;
import com.lingyang.cloud.model.vo.FileUploadVO;
import com.lingyang.cloud.service.FileService;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.log.annotation.Log;
import com.lingyang.common.log.enums.BusinessType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/29 17:05
 */
@RestController
@Tag(name = "公共接口-文件上传")
@RequestMapping("/file")
public class FileUploadController {
    @Resource
    private FileService fileService;

    @Log(value = "上传文件", businessType = BusinessType.IMPORT, isSaveRequestData = false)
    @Operation(summary = "文件上传")
    @PostMapping("/upload")
    public Result<FileUploadVO> uploadFile(@Parameter(description = "文件对象")
                                           @RequestParam("file") MultipartFile file,
                                           @Parameter(description = "上传路径前缀", in = ParameterIn.QUERY)
                                           String filePrefix) {
        return Result.success(fileService.upload(new FileUploadDTO(file, filePrefix)));
    }

    @Log(value = "上传文件ocr", businessType = BusinessType.IMPORT, isSaveRequestData = false)
    @Operation(summary = "文件上传")
    @PostMapping("/uploadocr")
    public Result<FileUploadVO> uploadocr(@Parameter(description = "文件对象")
                                           @RequestParam("file") MultipartFile file,
                                           @Parameter(description = "上传路径前缀", in = ParameterIn.QUERY)
                                           String filePrefix) {
        return Result.success(fileService.uploadOcr(new FileUploadDTO(file, filePrefix)));
    }

    @Log(value = "上传身份证文件ocr", businessType = BusinessType.IMPORT, isSaveRequestData = false)
    @Operation(summary = "身份证文件上传")
    @PostMapping("/uploadIdCardOcr")
    public Result<FileUploadVO> uploadIdCardOcr(@Parameter(description = "文件对象")
                                          @RequestParam("file") MultipartFile file,
                                          @Parameter(description = "上传路径前缀", in = ParameterIn.QUERY)
                                          String filePrefix) {
        return Result.success(fileService.uploadIdCardOcr(new FileUploadDTO(file, filePrefix)));
    }

}
