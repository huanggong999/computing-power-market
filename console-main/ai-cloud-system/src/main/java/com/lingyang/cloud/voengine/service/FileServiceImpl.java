package com.lingyang.cloud.voengine.service;

import com.alibaba.fastjson.JSONObject;
import com.lingyang.cloud.model.dto.FileUploadDTO;
import com.lingyang.cloud.model.vo.FileUploadVO;
import com.lingyang.cloud.service.FileService;
import com.lingyang.cloud.voengine.client.VoEngineApiClient;
import com.lingyang.cloud.voengine.config.VolEngineConfig;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.DateUtils;
import com.lingyang.common.core.utils.IdUtils;
import com.volcengine.service.visual.IVisualService;
import com.volcengine.service.visual.impl.VisualServiceImpl;
import com.volcengine.tos.TOSV2;
import com.volcengine.tos.model.object.PutObjectInput;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Base64;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/29 17:08
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {
    @Resource
    private VolEngineConfig volEngineConfig;

    @Override
    public FileUploadVO upload(FileUploadDTO fileUploadDTO) {
        TOSV2 tosv2 = VoEngineApiClient.getTosv2();
        VolEngineConfig.Tos tosConfig = volEngineConfig.getTos();
        try (InputStream inputStream = fileUploadDTO.getMultipartFile().getInputStream()) {
            String fileName = fileUploadDTO.getFileName();
            PutObjectInput putObjectInput = new PutObjectInput().setBucket(tosConfig.getBucketName())
                    .setKey(fileUploadDTO.getPath(fileName))
                    .setContent(inputStream);
            tosv2.putObject(putObjectInput);
            FileUploadVO result = new FileUploadVO();
            result.setName(fileName);
            result.setUrl(tosConfig.getHostUrl(fileName));
            return result;
        } catch (Exception e) {
            log.error("火山云上传文件失败：", e);
            Result.throwsError("文件上传失败");
            return new FileUploadVO();
        }
    }

    @Override
    public FileUploadVO uploadInputStream(FileUploadDTO fileUploadDTO) {
        TOSV2 tosv2 = VoEngineApiClient.getTosv2();
        VolEngineConfig.Tos tosConfig = volEngineConfig.getTos();
        try (InputStream inputStream = fileUploadDTO.getInputStream()) {
            String fileName = DateUtils.datePath() + "/" + IdUtils.simpleUUID() + ".png";
            PutObjectInput putObjectInput = new PutObjectInput().setBucket(tosConfig.getBucketName())
                    .setKey(fileUploadDTO.getPath(fileName))
                    .setContent(inputStream);
            tosv2.putObject(putObjectInput);
            FileUploadVO result = new FileUploadVO();
            result.setName(fileName);
            result.setUrl(tosConfig.getHostUrl(fileName));
            return result;
        } catch (Exception e) {
            log.error("火山云上传文件失败：", e);
            Result.throwsError("文件上传失败");
            return new FileUploadVO();
        }
    }

    @Override
    public FileUploadVO upload(String base64) {
        return null;
    }

    @Override
    public FileUploadVO uploadOcr(FileUploadDTO fileUploadDTO) {
        TOSV2 tosv2 = VoEngineApiClient.getTosv2();
        VolEngineConfig.Tos tosConfig = volEngineConfig.getTos();
        try (InputStream inputStream = fileUploadDTO.getMultipartFile().getInputStream()) {
            String fileName = fileUploadDTO.getFileName();
            PutObjectInput putObjectInput = new PutObjectInput().setBucket(tosConfig.getBucketName())
                    .setKey(fileUploadDTO.getPath(fileName))
                    .setContent(inputStream);
            tosv2.putObject(putObjectInput);
            FileUploadVO result = new FileUploadVO();
            result.setName(fileName);
            result.setUrl(tosConfig.getHostUrl(fileName));

            IVisualService visualService = VisualServiceImpl.getInstance();

            visualService.setAccessKey(volEngineConfig.getAccessKey());
            visualService.setSecretKey(volEngineConfig.getAccessSecret());

            // 步骤1: 将MultipartFile转换为字节数组
            byte[] fileContent = fileUploadDTO.getMultipartFile().getBytes();

            // 步骤2: 将字节数组转换为Base64编码的字符串
            String base64 = Base64.getEncoder().encodeToString(fileContent);


            JSONObject request = new JSONObject();
            request.put("image_base64", base64);

            String a = "OcrClueLicense";

            try {
                String jsonStri = visualService.ocrApi(a, request);
                result.setOcrzs(JSONObject.parseObject(jsonStri));
                System.out.println(jsonStri);
            } catch (Exception e) {
                e.printStackTrace();
            }


            return result;
        } catch (Exception e) {
            log.error("火山云上传文件失败：", e);
            Result.throwsError("文件上传失败");
            return new FileUploadVO();
        }
    }

    @Override
    public FileUploadVO uploadIdCardOcr(FileUploadDTO fileUploadDTO) {
        TOSV2 tosv2 = VoEngineApiClient.getTosv2();
        VolEngineConfig.Tos tosConfig = volEngineConfig.getTos();
        try (InputStream inputStream = fileUploadDTO.getMultipartFile().getInputStream()) {
            String fileName = fileUploadDTO.getFileName();
            PutObjectInput putObjectInput = new PutObjectInput().setBucket(tosConfig.getBucketName())
                    .setKey(fileUploadDTO.getPath(fileName))
                    .setContent(inputStream);
            tosv2.putObject(putObjectInput);
            FileUploadVO result = new FileUploadVO();
            result.setName(fileName);
            result.setUrl(tosConfig.getHostUrl(fileName));

            IVisualService visualService = VisualServiceImpl.getInstance();

            visualService.setAccessKey(volEngineConfig.getAccessKey());
            visualService.setSecretKey(volEngineConfig.getAccessSecret());

            // 步骤1: 将MultipartFile转换为字节数组
            byte[] fileContent = fileUploadDTO.getMultipartFile().getBytes();

            // 步骤2: 将字节数组转换为Base64编码的字符串
            String base64 = Base64.getEncoder().encodeToString(fileContent);


            JSONObject request = new JSONObject();
            request.put("image_base64", base64);

            String a = "IDCard";

            try {
                String jsonStri = visualService.ocrApi(a, request);
                result.setOcrzs(JSONObject.parseObject(jsonStri));
                System.out.println(jsonStri);
            } catch (Exception e) {
                e.printStackTrace();
            }


            return result;
        } catch (Exception e) {
            log.error("火山云上传文件失败：", e);
            Result.throwsError("文件上传失败");
            return new FileUploadVO();
        }
    }
}
