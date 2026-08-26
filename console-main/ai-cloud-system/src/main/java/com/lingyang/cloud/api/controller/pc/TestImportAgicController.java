package com.lingyang.cloud.api.controller.pc;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.lingyang.cloud.entity.SysNetworkValueEntity;
import com.lingyang.cloud.entity.SysOrderEntity;
import com.lingyang.cloud.entity.SysOrderSourceEntity;
import com.lingyang.cloud.mapper.SysNetworkValueMapper;
import com.lingyang.cloud.mapper.SysOrderMapper;
import com.lingyang.cloud.mapper.SysOrderSourceMapper;
import com.lingyang.cloud.model.edit.product.ProductAgicExcelImportEdit;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.DateUtils;
import com.lingyang.common.core.utils.IdUtils;
import com.lingyang.common.core.utils.RandomUtils;
import com.lingyang.common.web.excel.utils.ExcelUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.lingyang.cloud.enums.order.OrderOnlinePayEnum.REMIT_PAY;
import static com.lingyang.cloud.enums.order.OrderStatusEnum.PAID;
import static com.lingyang.cloud.enums.order.OrderTypeEnum.PRODUCT;
import static com.lingyang.cloud.enums.source.SourceChargeTypeEnum.POSTPAID_BY_HOUR;
import static com.lingyang.cloud.enums.source.SourceChargeTypeEnum.POSTPAID_BY_MONTH;
import static com.lingyang.cloud.enums.source.SourceChargeUnitEnum.DAY;
import static com.lingyang.cloud.enums.source.SourceChargeUnitEnum.MONTH;
import static com.lingyang.cloud.enums.source.SourceTypeEnum.CLOUD_NETWORK;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/8/11 17:17
 */
@RestController
@RequestMapping("/pc/import/agic")
@Slf4j
public class TestImportAgicController {

    @Resource
    private SysNetworkValueMapper sysNetworkValueMapper;

    @Resource
    private SysOrderMapper sysOrderMapper;

    @Resource
    private SysOrderSourceMapper sysOrderSourceMapper;

    @GetMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response) {
        String fileName = "AGI-C产品模板.xlsx";
        //编码问题
        try {
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding("utf-8");
            response.setDateHeader("Expires", -1);
            //设置响应头部信息，格式为附件，以及文件名
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build();
            WriteSheet writeSheet = ExcelUtils.writeSelectedSheet(ProductAgicExcelImportEdit.class, 0, "AGI-C产品IP模板");
            //此处只导出实体类中的数据所以只new 一个空的list，如果想导出数据库数据需要从数据库中查询数据list
            excelWriter.write(new ArrayList<ProductAgicExcelImportEdit>(), writeSheet);
            excelWriter.finish();
        } catch (UnsupportedEncodingException e) {
            log.error("导出Excel编码异常{}", e.getMessage());
        } catch (IOException e) {
            log.error("导出Excel文件异常{}", e.getMessage());
        }
    }


    @PostMapping("/importAgic")
    public Result<Void> importPersonalEcs(@RequestParam("file") MultipartFile file) {
        try {
            List<ProductAgicExcelImportEdit> importList = new ArrayList<>();
            EasyExcel.read(file.getInputStream(), ProductAgicExcelImportEdit.class,
                    new PageReadListener<ProductAgicExcelImportEdit>(importList::addAll)).sheet(0).doRead();
            log.info("导入数据：{}", importList);
            List<SysOrderEntity> sysOrderList =  new ArrayList<>();
            List<SysOrderSourceEntity> sysOrderSourceEntityList =  new ArrayList<>();
            for (ProductAgicExcelImportEdit productAgicExcelImportEdit : importList) {

                SysNetworkValueEntity sysNetworkValueEntity = new SysNetworkValueEntity();
                sysNetworkValueEntity.setProductId(Long.parseLong(productAgicExcelImportEdit.getProductId()));
                sysNetworkValueEntity.setUserId(Long.parseLong(productAgicExcelImportEdit.getCustomerId()));
                sysNetworkValueEntity.setProductName(productAgicExcelImportEdit.getProductName());
                sysNetworkValueEntity.setCreateTime(new Date());
                sysNetworkValueEntity.setCreateById(Long.parseLong(productAgicExcelImportEdit.getCustomerId()));
                sysNetworkValueEntity.setFormType(2);
                sysNetworkValueEntity.setBandwidth(productAgicExcelImportEdit.getBandwidth());
                sysNetworkValueEntity.setNetworkDay(1);
                sysNetworkValueEntity.setNetworkCount(productAgicExcelImportEdit.getNetworkCount());
                sysNetworkValueEntity.setIpCount(productAgicExcelImportEdit.getIpCount());
                sysNetworkValueEntity.setActualStatus(productAgicExcelImportEdit.getActualStatus());
                sysNetworkValueEntity.setActualAgiOpenTime(productAgicExcelImportEdit.getActualAgiOpenTime());
                sysNetworkValueEntity.setActualAgiExpireTime(productAgicExcelImportEdit.getActualAgiExpireTime());
                sysNetworkValueEntity.setPayStatus(1);
                sysNetworkValueEntity.setPayAmount(BigDecimal.valueOf(productAgicExcelImportEdit.getPayAmount()));
                if (productAgicExcelImportEdit.getChargeType() == 1){
                    sysNetworkValueEntity.setChargeType(POSTPAID_BY_HOUR);
                }else {
                    sysNetworkValueEntity.setChargeType(POSTPAID_BY_MONTH);
                }
                sysNetworkValueEntity.setDuration(productAgicExcelImportEdit.getDuration());
                if (productAgicExcelImportEdit.getDurationUnit() ==5){
                    sysNetworkValueEntity.setDurationUnit(DAY);
                }else {
                    sysNetworkValueEntity.setDurationUnit(MONTH);
                }
                sysNetworkValueEntity.setRemark("导入订单");
                sysNetworkValueEntity.setMobile(productAgicExcelImportEdit.getMobile());
                sysNetworkValueEntity.setAgiCustomerName(productAgicExcelImportEdit.getAgiCustomerName());
                sysNetworkValueEntity.setIsAutoRenew(0);
                sysNetworkValueEntity.setDepartmentId(productAgicExcelImportEdit.getDepartmentId());
                sysNetworkValueMapper.insert(sysNetworkValueEntity);

                String orderNo = "Order" + DateUtils.getDate(DateUtils.YYYYMMDDHHMMSS) + RandomUtils.getNumberRandom(6);
                Long orderId = IdUtils.nextId();
                SysOrderEntity sysOrderEntity = new SysOrderEntity();
                sysOrderEntity.setId(orderId);
                sysOrderEntity.setOrderNo(orderNo);
                sysOrderEntity.setOrderType(PRODUCT);
                sysOrderEntity.setOriginalPrice(BigDecimal.valueOf(productAgicExcelImportEdit.getPayAmount()));
                sysOrderEntity.setPremiumPrice(BigDecimal.ZERO);
                sysOrderEntity.setUserDiscountAmount(BigDecimal.ZERO);
                sysOrderEntity.setCouponAmount(BigDecimal.ZERO);
                sysOrderEntity.setCreditLineAmount(BigDecimal.ZERO);
                sysOrderEntity.setFinalPayAmount(BigDecimal.valueOf(productAgicExcelImportEdit.getPayAmount()));
                sysOrderEntity.setBalancePayAmount(BigDecimal.ZERO);
                sysOrderEntity.setOnlinePayAmount(BigDecimal.ZERO);
                sysOrderEntity.setOnlinePayType(REMIT_PAY);
                sysOrderEntity.setPayTime(productAgicExcelImportEdit.getActualAgiOpenTime());
                sysOrderEntity.setOrderStatus(PAID);
                sysOrderEntity.setCreateById(Long.parseLong(productAgicExcelImportEdit.getCustomerId()));
                sysOrderEntity.setCreateTime(new Date());
                sysOrderEntity.setNetworkProductId(Long.parseLong(productAgicExcelImportEdit.getProductId()));
                sysOrderEntity.setActualAgiOpenTime(productAgicExcelImportEdit.getActualAgiOpenTime());
                sysOrderEntity.setActualAgiExpireTime(productAgicExcelImportEdit.getActualAgiExpireTime());
                sysOrderEntity.setNetworkValueId(sysNetworkValueEntity.getId());
                sysOrderEntity.setBandwidth(productAgicExcelImportEdit.getBandwidth());
                sysOrderEntity.setNetworkDay(1);
                sysOrderEntity.setNetworkCount(productAgicExcelImportEdit.getNetworkCount());
                sysOrderEntity.setActualStatus(productAgicExcelImportEdit.getActualStatus());
                sysOrderEntity.setSettlementStatus(1);
                sysOrderList.add(sysOrderEntity);


                SysOrderSourceEntity sysOrderSourceEntity = new SysOrderSourceEntity();
                sysOrderSourceEntity.setOrderId(orderId);
                sysOrderSourceEntity.setOrderNo(orderNo);
                sysOrderSourceEntity.setSourceType(CLOUD_NETWORK);
                if (productAgicExcelImportEdit.getChargeType() == 1){
                    sysOrderSourceEntity.setChargeType(POSTPAID_BY_HOUR);
                }else {
                    sysOrderSourceEntity.setChargeType(POSTPAID_BY_MONTH);
                }
                sysOrderSourceEntity.setDuration(productAgicExcelImportEdit.getDuration());
                if (productAgicExcelImportEdit.getDurationUnit() ==5){
                    sysOrderSourceEntity.setDurationUnit(DAY);
                }else {
                    sysOrderSourceEntity.setDurationUnit(MONTH);
                }
                sysOrderSourceEntity.setNumber(1);
                sysOrderSourceEntity.setUnitPrice(BigDecimal.valueOf(productAgicExcelImportEdit.getPayAmount()));
                sysOrderSourceEntity.setVoucherDiscountAmount(BigDecimal.ZERO);
                sysOrderSourceEntity.setFinalUnitPrice(BigDecimal.valueOf(productAgicExcelImportEdit.getPayAmount()));
                sysOrderSourceEntity.setCreateById(Long.parseLong(productAgicExcelImportEdit.getCustomerId()));
                sysOrderSourceEntity.setCreateTime(new Date());
                sysOrderSourceEntityList.add(sysOrderSourceEntity);
            }

            sysOrderMapper.batchInsert(sysOrderList);
            sysOrderSourceMapper.batchInsert(sysOrderSourceEntityList);

        } catch (IOException e) {
            return Result.error(e.getMessage());
        }
        return Result.success();
    }
}
