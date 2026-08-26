package com.lingyang.cloud.api.controller.system;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysNetworkProductEntity;
import com.lingyang.cloud.entity.SysNetworkProductIpEntity;
import com.lingyang.cloud.mapper.SysNetworkProductIpMapper;
import com.lingyang.cloud.mapper.SysNetworkProductMapper;
import com.lingyang.cloud.model.edit.product.ProductIpExcelImportEdit;
import com.lingyang.cloud.model.query.home.SysProductIpQuery;
import com.lingyang.cloud.model.vo.product.SysProductIpExportVO;
import com.lingyang.cloud.service.SysNetworkProductIpService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.BeanUtils;
import com.lingyang.common.core.utils.StringUtils;
import com.lingyang.common.web.excel.utils.ExcelUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/system/network-product/ip")
@Tag(name = "后台系统-产品IP列表")
@Slf4j
public class SysNetworkProductIpController {

    @Autowired
    private SysNetworkProductIpService sysNetworkProductIpService;

    @Autowired
    private SysNetworkProductIpMapper systemNetworkProductIpMapper;

    @Resource
    private SysNetworkProductMapper sysNetworkProductMapper;

    @Operation(summary = "获取分页列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysNetworkProductIpEntity>> page(SysProductIpQuery query) {
        return sysNetworkProductIpService.getPage(PageQuery.build(query));
    }

    @Operation(summary = "添加产品IP列表")
    @PostMapping("/save")
    public Result<Void> save(@RequestBody @Valid SysNetworkProductIpEntity entity) {
        sysNetworkProductIpService.save(entity);
        return Result.result(true);
    }

    @Operation(summary = "修改产品IP列表")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody @Valid SysNetworkProductIpEntity entity) {
        sysNetworkProductIpService.update(entity);
        return Result.result(true);
    }

    @Operation(summary = "删除产品IP")
    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestBody @Parameter(name = "idList", description = "IPid集合", required = true) List<Long> idList) {
        systemNetworkProductIpMapper.deleteBatchIds(idList);
        return Result.success();
    }

    @Operation(summary = "AGI-C产品IP模板下载")
    @GetMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response) {
        String fileName = "AGI-C产品IP器模板.xlsx";
        //编码问题
        try {
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding("utf-8");
            response.setDateHeader("Expires", -1);
            //设置响应头部信息，格式为附件，以及文件名
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build();
            WriteSheet writeSheet = ExcelUtils.writeSelectedSheet(ProductIpExcelImportEdit.class, 0, "AGI-C产品IP模板");
            //此处只导出实体类中的数据所以只new 一个空的list，如果想导出数据库数据需要从数据库中查询数据list
            excelWriter.write(new ArrayList<ProductIpExcelImportEdit>(), writeSheet);
            excelWriter.finish();
        } catch (UnsupportedEncodingException e) {
            log.error("导出Excel编码异常{}", e.getMessage());
        } catch (IOException e) {
            log.error("导出Excel文件异常{}", e.getMessage());
        }
    }

    @Operation(summary = "导入AGI-C产品IP", parameters = {
            @Parameter(name = "productId", description = "产品ID", in = ParameterIn.PATH),
    })
    @PostMapping("/importIp/{productId}")
    public Result<Void> importPersonalEcs(@PathVariable("productId") Long productId, @RequestParam("file") MultipartFile file) {
        try {
                List<ProductIpExcelImportEdit> importList = new ArrayList<>();
                EasyExcel.read(file.getInputStream(), ProductIpExcelImportEdit.class,
                        new PageReadListener<ProductIpExcelImportEdit>(importList::addAll)).sheet(0).doRead();

                // 1. 获取数据库中已有的IP记录（按产品ID分组）
                List<SysNetworkProductIpEntity> existingIps = systemNetworkProductIpMapper.selectList(
                    Wrappers.lambdaQuery(SysNetworkProductIpEntity.class)
                        .eq(SysNetworkProductIpEntity::getProductId, productId)
                );

                // 2. 过滤掉已存在的IP
                List<SysNetworkProductIpEntity> productIpEntityList = new ArrayList<>();
                SysNetworkProductEntity sysNetworkProductEntity = sysNetworkProductMapper.selectById(productId);

                for (ProductIpExcelImportEdit productIpExcelImportEdit : importList) {
                    // 检查是否已存在相同IP和产品ID的记录
                    boolean exists = existingIps.stream()
                        .anyMatch(ip -> ip.getIp().equals(productIpExcelImportEdit.getIp()));

                    if (!exists) {
                        SysNetworkProductIpEntity sysNetworkProductIpEntity = BeanUtils.copyBean(
                            productIpExcelImportEdit, SysNetworkProductIpEntity.class);
                        sysNetworkProductIpEntity.setIp(productIpExcelImportEdit.getIp());
                        sysNetworkProductIpEntity.setPublicIp(productIpExcelImportEdit.getPublicIp());
                        sysNetworkProductIpEntity.setProductId(productId);
                        sysNetworkProductIpEntity.setIpAddress(
                            StringUtils.isEmpty(sysNetworkProductEntity.getCountry()) ?
                            null : sysNetworkProductEntity.getCountry());
                        productIpEntityList.add(sysNetworkProductIpEntity);
                    } else {
                        log.warn("IP {} 已存在，跳过导入", productIpExcelImportEdit.getIp());
                    }
                }

                // 3. 批量插入不存在的记录
                if (!productIpEntityList.isEmpty()) {
                    systemNetworkProductIpMapper.batchInsert(productIpEntityList);
                } else {
                    log.info("没有需要导入的新IP记录");
                }
            } catch (IOException e) {
                return Result.error(e.getMessage());
            }
        return Result.success();
    }

    @Operation(summary = "导出产品IP列表")
    @PostMapping("/export")
    public void export(@RequestBody SysProductIpExportVO vo) {
        sysNetworkProductIpService.export(vo);
    }
}
