package com.lingyang.cloud.api.controller.system;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.lingyang.cloud.entity.SysEcsEntity;
import com.lingyang.cloud.enums.source.EcsTypeEnum;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import com.lingyang.cloud.mapper.SysEcsMapper;
import com.lingyang.cloud.model.edit.ecs.EcsExcelImportEdit;
import com.lingyang.cloud.model.edit.ecs.EcsExcelUploadEdit;
import com.lingyang.cloud.model.query.esc.PcEcsQuery;
import com.lingyang.cloud.service.SysEcsService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.BeanUtils;
import com.lingyang.common.web.excel.utils.ExcelUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/18 16:48
 */
@Slf4j
@Tag(name = "后台系统-云服务器")
@RestController
@RequestMapping("/system/ecs")
public class SysEcsController {

    @Resource
    private SysEcsService sysEcsService;

    @Autowired
    private SysEcsMapper sysEcsMapper;


    @Operation(summary = "新增ecs服务器")
    @PostMapping("/addEcs")
    public Result<Void> addEcs(@RequestBody SysEcsEntity sysEcsEntity) {
        sysEcsMapper.insert(sysEcsEntity);
        return Result.success();
    }

    @Operation(summary = "修改ecs服务器")
    @PostMapping("/updateEcs")
    public Result<Void> updateEcs(@RequestBody SysEcsEntity sysEcsEntity) {
        if (sysEcsEntity.getId() == null) {
            return Result.error("缺少id");
        }
        sysEcsMapper.updateById(sysEcsEntity);
        return Result.success();
    }

    @Operation(summary = "删除ecs服务器")
    @GetMapping("/delete")
    public Result<Void> delete(@RequestParam(value = "id") Long id) {
        sysEcsMapper.deleteById(id);
        return Result.success();
    }


    @Operation(summary = "获取列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/getPage")
    public Result<PageResult<SysEcsEntity>> getEcsList(PcEcsQuery query) {
        return Result.success(sysEcsService.getEcsPage(PageQuery.build(query)));
    }



    @Operation(summary = "同步ecs服务器", parameters = {
            @Parameter(name = "ecsType", description = "服务器类型", in = ParameterIn.PATH),
            @Parameter(name = "file", description = "文件，通过火山价格计算器下载获取: https://www.volcengine.com/pricing?product=ECS&tab=1", in = ParameterIn.QUERY)
    })
    @PostMapping("/syncEcs/{ecsType}")
    public Result<Void> syncEcs(@PathVariable("ecsType") EcsTypeEnum ecsType,  @RequestParam("file") MultipartFile file) {
        try {
            // sysEcsService.clearEcs();
            List<EcsExcelUploadEdit> importList = new ArrayList<>();
            EasyExcel.read(file.getInputStream(), EcsExcelUploadEdit.class,
                    new PageReadListener<EcsExcelUploadEdit>(importList::addAll)).sheet(0).doRead();
            // log.info("111: {}", JSON.toJSONString(importList));

            List<SysEcsEntity> es = new ArrayList<>();
            for (EcsExcelUploadEdit ecsExcelUploadEdit : importList) {
                SysEcsEntity sysEcsEntity = BeanUtils.copyBean(ecsExcelUploadEdit, SysEcsEntity.class);
                sysEcsEntity.setRegionsZones(SourceRegionsEnum.getByName(ecsExcelUploadEdit.getRegions()));
                sysEcsEntity.setEcsType(ecsType);
                sysEcsEntity.setProductType(1);
                es.add(sysEcsEntity);
            }
            sysEcsMapper.batchInsert(es);
            // ExcelUtils.readExcel(file.getInputStream(), 0, EcsExcelUploadEdit.class, new EcsExcelUploadListener(ecsType));
        } catch (IOException e) {
            return Result.error(e.getMessage());
        }
        return Result.success();
    }

    @Operation(summary = "自建服务器模板下载")
    @GetMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response) {
        String fileName = "自建服务器模板.xlsx";
        //编码问题
        try {
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding("utf-8");
            response.setDateHeader("Expires", -1);
            //设置响应头部信息，格式为附件，以及文件名
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build();
            WriteSheet writeSheet = ExcelUtils.writeSelectedSheet(EcsExcelImportEdit.class, 0, "自建服务器模板");
            //此处只导出实体类中的数据所以只new 一个空的list，如果想导出数据库数据需要从数据库中查询数据list
            excelWriter.write(new ArrayList<EcsExcelImportEdit>(), writeSheet);
            excelWriter.finish();
        } catch (UnsupportedEncodingException e) {
            log.error("导出Excel编码异常{}", e.getMessage());
        } catch (IOException e) {
            log.error("导出Excel文件异常{}", e.getMessage());
        }
    }

    @Operation(summary = "导入自建服务器", parameters = {
            @Parameter(name = "ecsType", description = "服务器类型", in = ParameterIn.PATH),
    })
    @PostMapping("/importPersonalEcs/{ecsType}")
    public Result<Void> importPersonalEcs(@PathVariable("ecsType") EcsTypeEnum ecsType,  @RequestParam("file") MultipartFile file) {
        try {
            List<EcsExcelImportEdit> importList = new ArrayList<>();
            EasyExcel.read(file.getInputStream(), EcsExcelImportEdit.class,
                    new PageReadListener<EcsExcelImportEdit>(importList::addAll)).sheet(0).doRead();
            List<SysEcsEntity> es = new ArrayList<>();
            for (EcsExcelImportEdit ecsExcelUploadEdit : importList) {
                SysEcsEntity sysEcsEntity = BeanUtils.copyBean(ecsExcelUploadEdit, SysEcsEntity.class);
                sysEcsEntity.setRegionsZones(SourceRegionsEnum.getByName(ecsExcelUploadEdit.getRegions()));
                sysEcsEntity.setEcsType(ecsType);
                sysEcsEntity.setProductType(2);
                es.add(sysEcsEntity);
            }
            sysEcsMapper.batchInsert(es);
        } catch (IOException e) {
            return Result.error(e.getMessage());
        }
        return Result.success();
    }
}
