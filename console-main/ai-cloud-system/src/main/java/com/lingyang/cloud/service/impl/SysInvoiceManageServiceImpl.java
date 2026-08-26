package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.api.model.vo.PcInvoiceManageVO;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.mapper.*;
import com.lingyang.cloud.model.dto.PcInvoiceAmountDetailsDTO;
import com.lingyang.cloud.model.dto.PcInvoiceBillDTO;
import com.lingyang.cloud.model.dto.PcInvoiceBillInfoDTO;
import com.lingyang.cloud.model.dto.excelDto.PcInvoiceExportDto;
import com.lingyang.cloud.model.query.invoice.SysInvoiceQuery;
import com.lingyang.cloud.model.vo.customer.SysCustomerBillOverviewVO;
import com.lingyang.cloud.model.vo.invoice.SysInvoiceManageVO;
import com.lingyang.cloud.service.SysInvoiceManageService;
import com.lingyang.cloud.utils.IdentifierGenerator;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.security.model.LoginUserInfoDetail;
import com.lingyang.common.core.utils.DateUtils;
import com.lingyang.common.web.excel.model.ExcelSheetModel;
import com.lingyang.common.web.excel.utils.ExcelUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * @author 吴思镇
 */
@Service
@Slf4j
public class SysInvoiceManageServiceImpl implements SysInvoiceManageService {

    @Autowired
    private SysInvoiceManageMapper sysInvoiceManageMapper;

    @Autowired
    private SysMessageMapper sysMessageMapper;

    @Autowired
    private SysInvoiceBillMapper sysInvoiceBillMapper;

    @Resource
    private SysInvoiceEmailMapper sysInvoiceEmailMapper;

    @Resource
    private SysInvoiceTitleMapper sysInvoiceTitleMapper;

    @Override
    public Result<List<PcInvoiceBillDTO>> getInvoiceData(SysInvoiceQuery query) {
        Long userId = SecurityContext.getUserInfo().getUserId();
        log.info("获取发票数据的客户id：{}", userId);
        query.setCustomerId(userId);
        List<PcInvoiceBillDTO> list = new ArrayList<>();
        List<SysCustomerBillOverviewVO> bills;
        Integer type = query.getType();
        if (type == 1){
            //账期查询发票
            bills = sysInvoiceManageMapper.getDateList(query);
            if (ObjectUtils.isNotEmpty(bills)) {
                bills.forEach(b -> {
                    Date lastDayOfMonth = DateUtils.getLastDayOfMonth(DateUtils.toDate(b.getBill(), "yyyy-MM"));
                    if (b.getArrearsAmount().compareTo(BigDecimal.ZERO) == 0 || b.getBillDate().equals(DateUtils.parseDateToStr("yyyy-MM-dd", lastDayOfMonth))) {
                        PcInvoiceBillDTO pcInvoiceBillDTO = new PcInvoiceBillDTO();
                        pcInvoiceBillDTO.setBill(b.getBill());
                        pcInvoiceBillDTO.setAmount(b.getPayPrice());
                        pcInvoiceBillDTO.setBillIds(b.getBillIds());
                        pcInvoiceBillDTO.setTag(b.getTag());
                        List<SysCustomerBillOverviewVO> data = sysInvoiceManageMapper.getTotalAmount(query);
                        Optional<SysCustomerBillOverviewVO> first = data.stream().filter(d -> d.getBill().equals(b.getBill())).findFirst();
                        first.ifPresent(sysCustomerBillOverviewVO -> pcInvoiceBillDTO.setTotalAmount(sysCustomerBillOverviewVO.getPayPrice()));
                        list.add(pcInvoiceBillDTO);
                    }
                });
            }
        }else if (type == 2){
            //时间查询发票
            bills = sysInvoiceManageMapper.getTimeList(query);
            if (ObjectUtils.isNotEmpty(bills)) {
                bills.forEach(b -> {
                    if (b.getArrearsAmount().compareTo(BigDecimal.ZERO) == 0) {
                        PcInvoiceBillDTO pcInvoiceBillDTO = new PcInvoiceBillDTO();
                        pcInvoiceBillDTO.setBillIds(String.valueOf(b.getBillId()));
                        pcInvoiceBillDTO.setBill(b.getBill());
                        pcInvoiceBillDTO.setAmount(b.getPayPrice());
                        pcInvoiceBillDTO.setTotalAmount(b.getPayPrice());
                        pcInvoiceBillDTO.setTag(b.getTag());
                        String bill = b.getBill();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
                        YearMonth yearMonth = YearMonth.parse(bill, formatter);

                        // 获取该月的第一天和最后一天
                        LocalDate firstDayOfMonth = yearMonth.atDay(1);
                        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

                        // 设置时间为 00:00:00 和 23:59:59
                        ZonedDateTime startDate = firstDayOfMonth.atStartOfDay(ZoneId.systemDefault());
                        ZonedDateTime endDate = lastDayOfMonth.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());

                        // 转换为 java.util.Date 类型
                        Date startDateDate = Date.from(startDate.toInstant());
                        Date endDateDate = Date.from(endDate.toInstant());
                        pcInvoiceBillDTO.setStartDate(startDateDate);
                        pcInvoiceBillDTO.setEndDate(endDateDate);
                        pcInvoiceBillDTO.setProductName(b.getSourceType().getDesc());
                        list.add(pcInvoiceBillDTO);
                    }
                });
            }
        }else if (type == 3){
            //提前开票数据
            bills = sysInvoiceManageMapper.getAdvanceInvoice(query);
            if (ObjectUtils.isNotEmpty(bills)) {
                bills.forEach(b -> {
                    PcInvoiceBillDTO pcInvoiceBillDTO = new PcInvoiceBillDTO();
                    pcInvoiceBillDTO.setBillIds(String.valueOf(b.getBillId()));
                    pcInvoiceBillDTO.setBill(b.getBill());
                    pcInvoiceBillDTO.setAmount(b.getCreditLineAmount());
                    pcInvoiceBillDTO.setTotalAmount(b.getCreditLineAmount());
                    pcInvoiceBillDTO.setTag(b.getTag());
                    pcInvoiceBillDTO.setProductName(b.getSourceType().getDesc());
                    list.add(pcInvoiceBillDTO);

                });
            }
        }
        return Result.success(list);
    }

    @Override
    public Result<List<PcInvoiceBillInfoDTO>> getApplyInfo(SysInvoiceQuery query) {
        Long userId = SecurityContext.getUserInfo().getUserId();
        query.setCustomerId(userId);
        List<PcInvoiceBillInfoDTO> list = sysInvoiceManageMapper.getApplyInfo(query);
        return Result.success(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void apply(PcInvoiceManageVO vo) {
        try {
            LoginUserInfoDetail userInfo = SecurityContext.getUserInfo();
            Long userId = userInfo.getUserId();
            SysInvoiceManageEntity entity = new SysInvoiceManageEntity();
            BeanUtils.copyProperties(vo, entity);
            entity.setApplicationTime(new Date());
            String number = IdentifierGenerator.generateIdentifier();
            entity.setInvoiceNumber(number);
            entity.setCustomerId(userId);
            sysInvoiceManageMapper.insert(entity);
            List<Long> billIds = vo.getBillIds();
            if (ObjectUtils.isNotEmpty(billIds)){
                billIds.forEach(billId -> {
                    SysInvoiceBillEntity bill = new SysInvoiceBillEntity();
                    bill.setCustomerBillId(billId);
                    bill.setInvoiceManageId(entity.getId());
                    sysInvoiceBillMapper.insert(bill);
                });
            }
            SysMessage m = new SysMessage();
            m.setMsgType(4);
            m.setText(userInfo.getUsername() + "-申请发票");
            m.setStatus(1);
            m.setUserId(userInfo.getUserId());

            sysMessageMapper.insert(m);
        }catch (Exception e){
            log.info("申请开发票失败:{}",e.getMessage());
            throw new RuntimeException("申请开发票失败");
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelInvoice(Integer id) {
        try {
            List<Long> ids = sysInvoiceBillMapper.selectList(Wrappers.lambdaQuery(SysInvoiceBillEntity.class)
                            .eq(SysInvoiceBillEntity::getInvoiceManageId, id))
                    .stream().map(SysInvoiceBillEntity::getId).toList();
            sysInvoiceBillMapper.deleteBatchIds(ids);
            sysInvoiceManageMapper.deleteById(id);
        }catch (Exception e){
            log.info("取消发票失败:{}",e.getMessage());
            throw new RuntimeException("取消发票失败");
        }
    }

    @Override
    public Result<PcInvoiceAmountDetailsDTO> getTotalAmount(Long userId) {
        SysInvoiceQuery query = new SysInvoiceQuery();
        query.setCustomerId(userId);
        PcInvoiceAmountDetailsDTO entity = new PcInvoiceAmountDetailsDTO();
        entity.setAmount(BigDecimal.ZERO);
        entity.setCannotAmount(BigDecimal.ZERO);
        entity.setAlreadyAmount(BigDecimal.ZERO);

        List<SysCustomerBillOverviewVO> list = sysInvoiceManageMapper.getInvoiceList(query);
        if (ObjectUtils.isNotEmpty(list)) {
            // 获取可开发票金额
            BigDecimal amount = list.stream()
                .filter(s -> s.getArrearsAmount() != null && s.getPayPrice() != null)
                .filter(s -> s.getArrearsAmount().compareTo(BigDecimal.ZERO) == 0)
                .map(SysCustomerBillOverviewVO::getPayPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            entity.setAmount(amount);

            // 获取暂不可开发票金额
            BigDecimal cannotAmount = list.stream()
                .filter(s -> s.getArrearsAmount() != null && s.getPayPrice() != null)
                .filter(s -> s.getArrearsAmount().compareTo(BigDecimal.ZERO) > 0)
                .map(SysCustomerBillOverviewVO::getPayPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            entity.setCannotAmount(cannotAmount);
        }

        // 获取已开发票金额
        List<SysInvoiceManageEntity> sysInvoiceManageEntities = sysInvoiceManageMapper.selectList(
            Wrappers.lambdaQuery(SysInvoiceManageEntity.class)
                .eq(SysInvoiceManageEntity::getCustomerId, userId)
                .eq(SysInvoiceManageEntity::getStatus, 2)
                .eq(SysInvoiceManageEntity::getDelFlag, 0)
        );
        if (ObjectUtils.isNotEmpty(sysInvoiceManageEntities)) {
            BigDecimal alreadyAmount = sysInvoiceManageEntities.stream()
                .map(SysInvoiceManageEntity::getInvoicePrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            entity.setAlreadyAmount(alreadyAmount);
        }

        // 开票总额
        entity.setTotalAmount(entity.getAmount().add(entity.getAlreadyAmount()).add(entity.getCannotAmount()));
        return Result.success(entity);

    }

    @Override
    public void exportDetails(List<Long> bills) {

        List<PcInvoiceExportDto> invoiceData = sysInvoiceManageMapper.getDetails(bills);
        if (ObjectUtils.isNotEmpty(invoiceData)){
            invoiceData.forEach(invoice -> {
                String bill = invoice.getBill();
                LocalDate firstDayOfMonth = LocalDate.parse(bill + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth());

                String date = firstDayOfMonth.atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "~"
                        + lastDayOfMonth.atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                invoice.setDate(date);
            });
        }
        // 创建 ExcelSheetModel
        ExcelSheetModel<PcInvoiceExportDto> sheetModel = new ExcelSheetModel<>("详情", invoiceData);

        // 创建包含 sheetModel 的列表
        List<ExcelSheetModel<PcInvoiceExportDto>> sheetModels = new ArrayList<>();
        sheetModels.add(sheetModel);

        // 调用 exportExcel 方法
        ExcelUtils.exportExcel("invoice_details.xlsx", sheetModels, PcInvoiceExportDto.class);
    }

    @Override
    public Result<List<PcInvoiceBillInfoDTO>> list(SysInvoiceQuery query) {
        List<PcInvoiceBillInfoDTO> list = sysInvoiceManageMapper.getApplyInfo(query);
        return Result.success(list);
    }

    @Override
    public Result<Void> billingOperation(SysInvoiceManageVO vo) {
        Integer type = vo.getType();
        SysInvoiceManageEntity entity = new SysInvoiceManageEntity();
        entity.setId(vo.getId());
        if (type == 1){
            entity.setStatus(3);
            entity.setRemark(vo.getRemark());
        }else {
            entity.setStatus(2);
            entity.setProof(vo.getProof());
        }
        sysInvoiceManageMapper.updateById(entity);
        return Result.success();
    }

    @Override
    public Result<Boolean> hasTitleEmail(Long userId) {
        // 更高效的写法（仅检查是否存在）
        boolean hasEmail = sysInvoiceEmailMapper.exists(Wrappers.lambdaQuery(SysInvoiceEmailEntity.class)
                .eq(SysInvoiceEmailEntity::getCustomerId, userId));

        boolean hasTitle = sysInvoiceTitleMapper.exists(Wrappers.lambdaQuery(SysInvoiceTitleEntity.class)
                .eq(SysInvoiceTitleEntity::getCustomerId, userId));

        return Result.success(hasEmail && hasTitle);
    }


}
