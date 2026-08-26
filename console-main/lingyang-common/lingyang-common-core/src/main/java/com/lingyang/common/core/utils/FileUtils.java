package com.lingyang.common.core.utils;

import cn.hutool.core.io.FileUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件处理工具类
 */
public class FileUtils extends FileUtil {


    /**
     * 获取文件名称
     * @param file 文件
     * @return 文件名称
     */
    public static String extractFileNameNotDate(MultipartFile file) {
        if (file == null ) {
            return "";
        }
        String extension = getExtension(file);
        return  IdUtils.simpleUUID() + "." + extension;
    }


    /**
     * 获取文件名称
     * @param file 文件
     * @return 文件名称
     */
    public static String extractFileName(MultipartFile file) {
        if (file == null ) {
            return "";
        }
        String extension = getExtension(file);
        return DateUtils.datePath() + "/" + IdUtils.simpleUUID() + "." + extension;
    }

    /**
     * 获取文件名的后缀
     *
     * @param file 表单文件
     * @return 后缀名
     */
    public static String getExtension(MultipartFile file) {
        String extension = getExtension(file.getOriginalFilename());
        if (StringUtils.isEmpty(extension)) {
            extension = MimeTypeUtils.getExtension(file.getContentType());
        }
        return extension;
    }

    /**
     * 获取文件名的后缀
     *
     * @param fileName 文件名称
     * @return 后缀名
     */
    public static String getExtension(String fileName) {
        return FilenameUtils.getExtension(fileName);
    }
}
