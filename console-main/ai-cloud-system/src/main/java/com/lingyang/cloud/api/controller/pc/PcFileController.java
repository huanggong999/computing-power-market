package com.lingyang.cloud.api.controller.pc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lingyang.cloud.api.model.dto.PcAddFileDTO;
import com.lingyang.cloud.api.model.dto.PcAddFolderDTO;
import com.lingyang.cloud.api.model.dto.PcMoveOrCopyFileDTO;
import com.lingyang.cloud.api.model.vo.PcFileVO;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.cloud.mapper.SysCustomerBucketMapper;
import com.lingyang.cloud.mapper.SysFileMapper;
import com.lingyang.cloud.model.query.coupon.SysCouponQuery;
import com.lingyang.cloud.model.query.coupon.SysCustomerCouponQuery;
import com.lingyang.cloud.model.query.home.SysFileQuery;
import com.lingyang.cloud.model.vo.FileUploadVO;
import com.lingyang.cloud.model.vo.customer.SysCustomerCouponListVO;
import com.lingyang.cloud.voengine.client.VoEngineApiClient;
import com.lingyang.cloud.voengine.config.VolEngineConfig;
import com.lingyang.common.core.model.FileVO;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.utils.*;
import com.lingyang.common.core.utils.Optional;
import com.volcengine.tos.TOSV2;
import com.volcengine.tos.TOSV2ClientBuilder;
import com.volcengine.tos.model.RequestInfo;
import com.volcengine.tos.model.object.DeleteObjectOutput;
import com.volcengine.tos.model.object.DeleteObjectTaggingInput;
import com.volcengine.tos.model.object.PutObjectInput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "pc端-对象存储文件")
@RestController
@RequestMapping("/pc/ossfile")
public class PcFileController {

    @Autowired
    private SysFileMapper sysFileMapper;

    @Autowired
    private SysCustomerBucketMapper sysCustomerBucketMapper;

    @Resource
    private VolEngineConfig volEngineConfig;

    @GetMapping("/page")
    @Operation(summary = "文件夹和文件列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    public Result<PageResult<SysFile>> getPage(SysFileQuery query) {
        PageQuery<SysFileQuery> pageQuery = PageQuery.build(query);
        SysFileQuery fileQuery = pageQuery.getQuery();
        pageQuery.startPage();
        List<SysFile> files = sysFileMapper.selectList(new LambdaQueryWrapper<SysFile>()
                .eq(SysFile::getBucketId, fileQuery.getBucketId())
                .eq(fileQuery.getFileId() != null, SysFile::getFolderId, fileQuery.getFileId())
                .eq(fileQuery.getFileId() == null, SysFile::getFolderId, 0)
                .like(StringUtils.isNotBlank(fileQuery.getName()), SysFile::getName, fileQuery.getName())
                .orderByAsc(SysFile::getType)

        );

        // 获取上级文件夹
        List<FileVO> fileList = new ArrayList<>();
        if (query.getFileId() != null) {
            SysFile f = sysFileMapper.selectById(query.getFileId());
            while (f.getFolderId() != 0L) {
                FileVO s = new FileVO();
                s.setFileName(f.getName());
                s.setFileId(f.getId());
                s.setBucketId(f.getBucketId());
                fileList.add(s);
                f = sysFileMapper.selectById(f.getFolderId());
            }
            FileVO s = new FileVO();
            s.setFileName(f.getName());
            s.setFileId(f.getId());
            s.setBucketId(f.getBucketId());
            fileList.add(s);
        }

        if (CollectionUtils.isNotEmpty(files)) {
            PageResult<SysFile> result = PageResult.of(files);
            result.setFileList(fileList);
            return Result.success(result);
        } else {
            PageResult<SysFile> data = PageResult.of(List.of());
            data.setFileList(fileList);
            return Result.success(data);
        }

    }


    @Operation(summary = "新建文件夹")
    @PostMapping("/addFolder")
    public Result<String> addFolder(@RequestBody PcAddFolderDTO dto) {
        if (dto.getFileId() != null) {
            SysFile ps = sysFileMapper.selectById(dto.getFileId());
            if (ps == null) {
                return Result.error("文件夹不存在");
            }
            if (ps.getType().equals(2)) {
                return Result.error("id不是文件夹");
            }

            Long aLong = sysFileMapper.selectCount(
                    new LambdaQueryWrapper<SysFile>()
                            .eq(SysFile::getBucketId, dto.getBucketId())
                            .eq(SysFile::getFolderId, dto.getFileId())
                            .eq(SysFile::getType, 1)
                            .eq(SysFile::getName, dto.getName())
            );

            if (aLong != 0) {
                return Result.error("文件夹名称存在");
            }

            SysFile folder = new SysFile();
            folder.setFolderId(dto.getFileId());
            folder.setType(1);
            folder.setName(dto.getName());
            folder.setBucketId(dto.getBucketId());
            folder.setUserId(SecurityContext.getUserInfo().getUserId());
            folder.setSize(BigDecimal.ZERO);
            folder.setPath(ps.getPath() + "/" + dto.getName());
            sysFileMapper.insert(folder);
        } else {

            Long aLong = sysFileMapper.selectCount(
                    new LambdaQueryWrapper<SysFile>()
                            .eq(SysFile::getBucketId, dto.getBucketId())
                            .eq(SysFile::getFolderId, 0)
                            .eq(SysFile::getType, 1)
                            .eq(SysFile::getName, dto.getName())
            );

            if (aLong != 0) {
                return Result.error("文件夹名称存在");
            }

            SysFile folder = new SysFile();
            folder.setFolderId(0L);
            folder.setType(1);
            folder.setName(dto.getName());
            folder.setBucketId(dto.getBucketId());
            folder.setUserId(SecurityContext.getUserInfo().getUserId());
            folder.setSize(BigDecimal.ZERO);
            folder.setPath(dto.getName());
            sysFileMapper.insert(folder);
        }

        return Result.success();
    }

    @Operation(summary = "上传文件")
    @PostMapping("/addFile")
    public Result<String> addFile(@RequestParam("file") MultipartFile file,
                                  @RequestParam("bucketId") Long bucketId,
                                  @RequestParam(value = "fileId", required = false) Long fileId) {
        log.info("bnuck {}, {} {}", bucketId, fileId, file.getSize());
        SysCustomerBucketEntity customerBucketEntity = sysCustomerBucketMapper.selectById(bucketId);
        if (customerBucketEntity == null) {
            return Result.error("存储桶不存在");
        }
        // 没有文件夹
        if (fileId == null) {
            TOSV2 tosv2 = new TOSV2ClientBuilder()
                    .build(volEngineConfig.getRegions(),
                            volEngineConfig.getTos().getEndpoint(),
                            volEngineConfig.getAccessKey(),
                            volEngineConfig.getAccessSecret());
            try (InputStream inputStream = file.getInputStream()) {
                String fileName = getFileName(file);
                PutObjectInput putObjectInput = new PutObjectInput().setBucket(customerBucketEntity.getName())
                        .setKey(getPath(fileName, ""))
                        .setContent(inputStream);
                tosv2.putObject(putObjectInput);
                FileUploadVO result = new FileUploadVO();
                result.setName(file.getOriginalFilename());
                result.setUrl(getHostUrl(fileName, volEngineConfig.getRegions(),customerBucketEntity.getName(),
                        volEngineConfig.getTos().getEndpoint()));

                SysFile f = new SysFile();
                f.setType(2);
                f.setUserId(SecurityContext.getUserInfo().getUserId());
                f.setBucketId(bucketId);
                f.setName(fileName);
                f.setPath(result.getUrl());
                f.setFolderId(0L);
                f.setSize(BigDecimal.valueOf(file.getSize()).divide(BigDecimal.valueOf(1024)).divide(BigDecimal.valueOf(1024L)));
                sysFileMapper.insert(f);
                return Result.success();
            } catch (Exception e) {
                log.error("火山云上传文件失败：", e);
                Result.throwsError("文件上传失败");
                return Result.error("文件上传失败");
            }
        } else {
            // 携带文件夹
            SysFile sysFile = sysFileMapper.selectById(fileId);

            TOSV2 tosv2 = new TOSV2ClientBuilder()
                    .build(volEngineConfig.getRegions(),
                            volEngineConfig.getTos().getEndpoint(),
                            volEngineConfig.getAccessKey(),
                            volEngineConfig.getAccessSecret());


            try (InputStream inputStream = file.getInputStream()) {
                String fileName = getFileName(file);
                PutObjectInput putObjectInput = new PutObjectInput().setBucket(customerBucketEntity.getName())
                        .setKey(getPath(fileName, sysFile.getPath()))
                        .setContent(inputStream);
                tosv2.putObject(putObjectInput);
                FileUploadVO result = new FileUploadVO();
                result.setName(file.getOriginalFilename());
                result.setUrl(getHostUrl(getPath(fileName, sysFile.getPath()), volEngineConfig.getRegions(),customerBucketEntity.getName(),
                        volEngineConfig.getTos().getEndpoint()));

                SysFile f = new SysFile();
                f.setType(2);
                f.setUserId(SecurityContext.getUserInfo().getUserId());
                f.setBucketId(bucketId);
                f.setName(fileName);
                f.setPath(result.getUrl());
                f.setFolderId(sysFile.getId());
                f.setSize(BigDecimal.valueOf(file.getSize()).divide(BigDecimal.valueOf(1024)).divide(BigDecimal.valueOf(1024L)));
                sysFileMapper.insert(f);


                sysFile.setSize(f.getSize().add(sysFile.getSize()));
                sysFileMapper.updateById(sysFile);

                return Result.success();
            } catch (Exception e) {
                log.error("火山云上传文件失败：", e);
                Result.throwsError("文件上传失败");
                return Result.error("文件上传失败");
            }
        }
    }

    public static void main(String[] args) {
        TOSV2 tosv2 = new TOSV2ClientBuilder()
                .build("cn-beijing",
                        "tos-cn-beijing.volces.com",
                        System.getenv().getOrDefault("VOLC_TOS_ACCESS_KEY", ""),
                        System.getenv().getOrDefault("VOLC_TOS_ACCESS_SECRET", ""));

        tosv2.copyObject("ai-cloud-system","https://ai-cloud-system.tos-cn-beijing.volces.com/2024/11/29/29138645cdc4436f8a598da0c2eabba8.xlsx",

                "https://ai-cloud-system.tos-cn-beijing.volces.com/2025");

    }


    @Operation(summary = "移动到")
    @PostMapping("/move")
    public Result<String> move(@RequestBody PcMoveOrCopyFileDTO dto) {
        for (Long fileId : dto.getFileIdList()) {
            SysFile tar = sysFileMapper.selectById(fileId);
            if (tar.getType().equals(1)) {
                return Result.error("文件夹不能复制");
            }
        }
        copy(dto);

        for (Long aLong : dto.getFileIdList()) {
            delete(aLong);
        }

        return Result.success();
    }


    @Operation(summary = "复制到")
    @PostMapping("/copy")
    public Result<String> copy(@RequestBody PcMoveOrCopyFileDTO dto) {

        for (Long fileId : dto.getFileIdList()) {
            SysFile tar = sysFileMapper.selectById(fileId);
            if (tar.getType().equals(1)) {
                return Result.error("文件夹不能复制");
            }
        }

        SysFile tar = sysFileMapper.selectById(dto.getFileId());
        SysCustomerBucketEntity bucketEntity = sysCustomerBucketMapper.selectById(tar.getBucketId());
        TOSV2 tosv2 = new TOSV2ClientBuilder()
                .build(volEngineConfig.getRegions(),
                        volEngineConfig.getTos().getEndpoint(),
                        volEngineConfig.getAccessKey(),
                        volEngineConfig.getAccessSecret());
        if (dto.getFileId() == null) {
            List<Long> fileIdList = dto.getFileIdList();
            fileIdList.forEach(fileId -> {
                SysFile sysFile = sysFileMapper.selectById(fileId);
//                log.info("指定复制命令: {}，{}，{}", bucketEntity.getName(), sysFile.getPath(), tar.getPath());
//                tosv2.copyObject(bucketEntity.getName(), sysFile.getPath(), tar.getPath());

                SysFile copy = new SysFile();
                copy.setType(2);
                copy.setUserId(SecurityContext.getUserInfo().getUserId());
                copy.setBucketId(bucketEntity.getId());
                copy.setName(sysFile.getName());
                copy.setPath(tar.getPath());
//                copy.setPath(getHostUrl(tar.getName(), volEngineConfig.getRegions(),bucketEntity.getName(),
//                        volEngineConfig.getTos().getEndpoint()));
                copy.setFolderId(0L);
                copy.setSize(sysFile.getSize());
                sysFileMapper.insert(copy);

                tar.setSize(tar.getSize().add(sysFile.getSize()));
                sysFileMapper.updateById(tar);

            });
        } else {

            List<Long> fileIdList = dto.getFileIdList();
            fileIdList.forEach(fileId -> {
                SysFile sysFile = sysFileMapper.selectById(fileId);
//                log.info("指定复制命令: {}，{}，{}", bucketEntity.getName(), sysFile.getPath(), tar.getPath());
//                tosv2.copyObject(bucketEntity.getName(), sysFile.getPath(), tar.getPath());

                SysFile copy = new SysFile();
                copy.setType(2);
                copy.setUserId(SecurityContext.getUserInfo().getUserId());
                copy.setBucketId(bucketEntity.getId());
                copy.setName(sysFile.getName());
                copy.setPath(tar.getPath());
//                copy.setPath(getHostUrl(getPath(tar.getName(), tar.getPath()), volEngineConfig.getRegions(), bucketEntity.getName(),
//                        volEngineConfig.getTos().getEndpoint() ));
                copy.setFolderId(tar.getId());
                copy.setSize(sysFile.getSize());
                sysFileMapper.insert(copy);

                tar.setSize(tar.getSize().add(sysFile.getSize()));
                sysFileMapper.updateById(tar);
            });
        }
        return Result.success();
    }

    @Operation(summary = "删除")
    @GetMapping("/delete")
    public Result<String> delete(@RequestParam Long id) {
        SysFile file = sysFileMapper.selectById(id);
        if (!file.getFolderId().equals(0L)) {
            SysFile file1 = sysFileMapper.selectById(file.getFolderId());
            while(file1.getFolderId() != 0) {
                file1.setSize(file1.getSize().subtract(file.getSize()));
                sysFileMapper.updateById(file1);
                file1 = sysFileMapper.selectById(file1.getFolderId());
            }
            file1.setSize(file1.getSize().subtract(file.getSize()));
            sysFileMapper.updateById(file1);
        }
        // 删除文件夹
        if (file.getType().equals(1)) {
            try {
                SysCustomerBucketEntity customerBucketEntity = sysCustomerBucketMapper.selectById(file.getBucketId());
                TOSV2 tosv2 = new TOSV2ClientBuilder()
                        .build(volEngineConfig.getRegions(),
                                volEngineConfig.getTos().getEndpoint(),
                                volEngineConfig.getAccessKey(),
                                volEngineConfig.getAccessSecret());

                DeleteObjectTaggingInput d = new DeleteObjectTaggingInput();
                d.setBucket(customerBucketEntity.getName());
                d.setKey(file.getPath());
                tosv2.deleteObjectTagging(d);
            } catch (Exception e) {
                log.info("删除操作", e);
            }
        } else {
            try {
                SysCustomerBucketEntity customerBucketEntity = sysCustomerBucketMapper.selectById(file.getBucketId());
                TOSV2 tosv2 = new TOSV2ClientBuilder()
                        .build(volEngineConfig.getRegions(),
                                volEngineConfig.getTos().getEndpoint(),
                                volEngineConfig.getAccessKey(),
                                volEngineConfig.getAccessSecret());

                DeleteObjectTaggingInput d = new DeleteObjectTaggingInput();
                d.setBucket(customerBucketEntity.getName());
                d.setKey(file.getPath());
                tosv2.deleteObjectTagging(d);
            } catch (Exception e) {
                log.info("删除操作", e);
            }

        }
        sysFileMapper.deleteById(id);
        return Result.success();
    }


    @Operation(summary = "获取所有文件夹")
    @GetMapping("/getAll")
    public Result<List<PcFileVO>> getAll(@RequestParam Long id) {
        List<SysFile> files = sysFileMapper.selectList(new LambdaQueryWrapper<SysFile>()
                .eq(SysFile::getBucketId, id)
                .eq(SysFile::getType, 1)
                .orderByAsc(SysFile::getFolderId)
        );
        List<PcFileVO> ls = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(files)) {
//            List<SysFile> files1 = files.stream().filter(s -> {
//                return s.getFolderId().equals(0L);
//            }).collect(Collectors.toList());
            for (SysFile sysFile : files) {
                PcFileVO v = new PcFileVO();
                v.setId(sysFile.getId());
                v.setName(sysFile.getPath());
                ls.add(v);
            }
        }
        return Result.success(ls);
    }

    public static String getFileName(MultipartFile multipartFile) {
        String fileName = FileUtils.extractFileNameNotDate(multipartFile);
        if (StringUtils.isEmpty(fileName)) {
            fileName = IdUtils.simpleUUID();
        }
        return fileName;
    }

    public static String getPath(String fileName, String pathPrefix) {
        if (StringUtils.isNotEmpty(pathPrefix)) {
            if (!pathPrefix.endsWith("/") && !fileName.startsWith("/")) {
                fileName = "/" + fileName;
            }
            fileName = pathPrefix + fileName;
        }
        return fileName.toLowerCase(Locale.ROOT);
    }


    /**
     * 获取访问域名
     * @return 访问域名
     */
    public static String getHostUrl(String fileName, String region, String bucketName, String endpoint) {
        if (StringUtils.isEmpty(region) || StringUtils.isEmpty(bucketName)) {
            return null;
        }
        if (StringUtils.isEmpty(fileName)) {
            fileName = IdUtils.simpleUUID();
        }
        if (!fileName.startsWith("/")) {
            fileName = "/" + fileName;
        }
        //https://ai-cloud-system.tos-cn-beijing.volces.com/2024/11/29/ea57a1d3d79042b6ac107fc6079d5b33.png
        //https://ai-cloud-system.tos-cn-beijing.volces.com/2024/11/29/374cd0fc51054e9e9bd29cf59b1265c9.png
        return "https://" + bucketName + "." + endpoint + fileName;
    }




}
