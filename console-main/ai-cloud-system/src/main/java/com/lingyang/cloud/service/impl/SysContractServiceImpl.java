package com.lingyang.cloud.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lingyang.cloud.entity.SysContract;
import com.lingyang.cloud.entity.SysCustomerEntity;
import com.lingyang.cloud.entity.SysMessage;
import com.lingyang.cloud.entity.SysOrderEntity;
import com.lingyang.cloud.mapper.SysContractMapper;
import com.lingyang.cloud.mapper.SysCustomerMapper;
import com.lingyang.cloud.mapper.SysMessageMapper;
import com.lingyang.cloud.mapper.SysOrderMapper;
import com.lingyang.cloud.model.dto.OrderContractDTO;
import com.lingyang.cloud.model.query.home.SysContractQuery;
import com.lingyang.cloud.service.SysContractService;
import com.lingyang.cloud.utils.esign.*;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.security.model.LoginUserInfoDetail;
import com.lingyang.common.core.utils.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class SysContractServiceImpl implements SysContractService {

    @Autowired
    private SysContractMapper sysContractMapper;

    @Autowired
    private SysCustomerMapper sysCustomerMapper;

    @Autowired
    private SysOrderMapper sysOrderMapper;

    @Autowired
    private SysMessageMapper sysMessageMapper;

    @Value("${contract.notifyUrl}")
    private String notifyUrl;

    @Override
    public Result<Void> orderContract(OrderContractDTO d) throws EsignDemoException {
        LoginUserInfoDetail userInfo = SecurityContext.getUserInfo();

        List<Long> orderIds = d.getOrderIds();
        if (d.getType().equals(1)) {
            SysContract contract = new SysContract();
            Long userId = userInfo.getUserId();
            SysCustomerEntity customer = sysCustomerMapper.selectById(userId);
            contract.setUserId(userId);
            contract.setContractNo("HT" + System.currentTimeMillis());
            contract.setClientName(d.getClientName());
            contract.setClientContactPerson(d.getClientContactPerson());
            contract.setClientContactPhone(d.getClientContactPhone());
            contract.setClientContactAddress(d.getClientContactAddress());
            String linkOrderNo = "";
            for (Long orderId : orderIds) {
                SysOrderEntity entity = sysOrderMapper.selectById(orderId);
                linkOrderNo += entity.getOrderNo() + ",";
            }
            contract.setLinkOrderNo(linkOrderNo);
            contract.setStatus(2);

            // 生成模版文件
            Gson gson = new Gson();
            EsignHttpResponse createByDocTemplate = TemplateDemo.createByDocTemplate(d);
            log.info("模版文件: {}", createByDocTemplate);
            JsonObject createByDocTemplateObject = gson.fromJson(createByDocTemplate.getBody(), JsonObject.class);
            log.info("模版文件Object: {}", createByDocTemplateObject);
            String fileId = createByDocTemplateObject.getAsJsonObject("data").get("fileId").getAsString();
            String fileDownloadUrl = createByDocTemplateObject.getAsJsonObject("data").get("fileDownloadUrl").getAsString();
            log.info("填充后文件id: {}", fileId);
            log.info("文件下载链接: {}", fileDownloadUrl);

            contract.setFileId(fileId);
            contract.setFileDownloadUrl(fileDownloadUrl);

            EsignHttpResponse file = null;

            // 个人
            if (customer.getType().equals(1)) {
                file = PlatformSignToC.createByFile(contract, notifyUrl);
            } else {
                file = PlatformSignToC.createCompanyByFile(contract, notifyUrl);
            }

            String body = file.getBody();
            log.info("发起签署: {}", body);
            JsonObject js = gson.fromJson(body, JsonObject.class);
            String signFlowId = js.getAsJsonObject("data").get("signFlowId").getAsString();
            contract.setSignFlowId(signFlowId);


            EsignHttpResponse response = SignDemo.signUrl(signFlowId, contract.getClientContactPhone());
            JsonObject signUrlJsonObject = gson.fromJson(response.getBody(), JsonObject.class);
            JsonObject signUrlData = signUrlJsonObject.getAsJsonObject("data");
            String shortUrl = signUrlData.get("shortUrl").getAsString();
            log.info("合同短链: {}", shortUrl);
            contract.setSignUrl(shortUrl);
            contract.setUserType(customer.getType());
            sysContractMapper.insert(contract);
        } else {
            SysContract contract = new SysContract();
            if (d.getId() != null) {
                contract.setId(d.getId());
            }
            Long userId = SecurityContext.getUserInfo().getUserId();
            SysCustomerEntity customer = sysCustomerMapper.selectById(userId);
            contract.setType(d.getType());
            contract.setUserId(userId);
            contract.setContractNo("HT" + System.currentTimeMillis());
            contract.setClientName(d.getClientName());
            contract.setClientContactPerson(d.getClientContactPerson());
            contract.setClientContactPhone(d.getClientContactPhone());
            contract.setClientContactAddress(d.getClientContactAddress());
            contract.setStatus(1);
            contract.setUserType(customer.getType());
            if (d.getId() == null) {
                String linkOrderNo = "";
                if (CollectionUtils.isNotEmpty(orderIds)){
                    for (Long orderId : orderIds) {
                        SysOrderEntity entity = sysOrderMapper.selectById(orderId);
                        linkOrderNo += entity.getOrderNo() + ",";
                    }
                }
                contract.setLinkOrderNo(linkOrderNo);
                sysContractMapper.insert(contract);
            } else {
                sysContractMapper.updateById(contract);
            }

        }

        SysMessage m = new SysMessage();
        m.setMsgType(5);
        m.setText("申请订单合同");
        m.setStatus(1);
        m.setUserId(userInfo.getUserId());

        sysMessageMapper.insert(m);

        return Result.success();
    }

    @Override
    public Result<PageResult<SysContract>> getPage(PageQuery<SysContractQuery> pageQuery) {
        SysContractQuery query = pageQuery.getQuery();
        pageQuery.startPage();
        LambdaQueryWrapper<SysContract> queryWrapper = Wrappers.lambdaQuery(SysContract.class)
                .like(StringUtils.isNotBlank(query.getContractNo()), SysContract::getContractNo, query.getContractNo())
                .like(StringUtils.isNotBlank(query.getLinkOrderNo()), SysContract::getLinkOrderNo, query.getLinkOrderNo())
                .ge(StringUtils.isNotBlank(query.getStartTime()), SysContract::getCreateTime, query.getStartTime())
                .le(StringUtils.isNotBlank(query.getEndTime()), SysContract::getCreateTime, query.getEndTime())
                .eq(SysContract::getUserId, SecurityContext.getUserInfo().getUserId())
                .orderByDesc(SysContract::getCreateTime);
        return Result.success(Optional.of(sysContractMapper.selectList(queryWrapper))
                .flatMap(entities -> {

                    PageResult<SysContract> result = PageResult.of(entities);
                    List<SysContract> list = result.getList();
                    for (SysContract sysContract : list) {
                        SysCustomerEntity sysCustomerEntity = sysCustomerMapper.selectById(sysContract.getUserId());
                        if (sysCustomerEntity != null) {
                            sysContract.setCustomerName(sysCustomerEntity.getCustomerName());
                            sysContract.setCustomerPhone(sysCustomerEntity.getPhone());
                        }

                        if (sysContract.getDownloadTime() == null || (sysContract.getDownloadTime() != null && DateUtils.addMinutes(sysContract.getDownloadTime(), 50).before(new Date()) && sysContract.getStatus() == 3)) {
                            //下载已签署文件及附属材料
                            EsignHttpResponse fileDownloadUrl = null;
                            try {
                                fileDownloadUrl = SignDemo.fileDownloadUrl(sysContract.getSignFlowId());
                                JSONObject jsonObject = JSON.parseObject(fileDownloadUrl.getBody());
                                log.info("下载签署合同： {}", fileDownloadUrl.getBody());
                                sysContract.setFileDownloadUrl(jsonObject.getJSONObject("data").getJSONArray("files").getJSONObject(0).getString("downloadUrl"));
                                sysContract.setDownloadTime(new Date());
                                sysContractMapper.updateById(sysContract);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }



                    }
                    return result;
                })
                .orElseGet(() -> {
                    return PageResult.of(List.of());
                }));
    }

    @Override
    public Result<PageResult<SysContract>> getAdminPage(PageQuery<SysContractQuery> pageQuery) {
        SysContractQuery query = pageQuery.getQuery();
        pageQuery.startPage();
        LambdaQueryWrapper<SysContract> queryWrapper = Wrappers.lambdaQuery(SysContract.class)
                .like(StringUtils.isNotBlank(query.getContractNo()), SysContract::getContractNo, query.getContractNo())
                .like(StringUtils.isNotBlank(query.getLinkOrderNo()), SysContract::getLinkOrderNo, query.getLinkOrderNo())
                .ge(StringUtils.isNotBlank(query.getStartTime()), SysContract::getCreateTime, query.getStartTime())
                .le(StringUtils.isNotBlank(query.getEndTime()), SysContract::getCreateTime, query.getEndTime())
                .orderByDesc(SysContract::getCreateTime);
        return Result.success(Optional.of(sysContractMapper.selectList(queryWrapper))
                .flatMap(entities -> {

                    PageResult<SysContract> result = PageResult.of(entities);
                    List<SysContract> list = result.getList();
                    for (SysContract sysContract : list) {
                        SysCustomerEntity sysCustomerEntity = sysCustomerMapper.selectById(sysContract.getUserId());
                        if (sysCustomerEntity != null) {
                            sysContract.setCustomerName(sysCustomerEntity.getCustomerName());
                            sysContract.setCustomerPhone(sysCustomerEntity.getPhone());
                        }

                        if (sysContract.getDownloadTime() == null || (sysContract.getDownloadTime() != null && DateUtils.addMinutes(sysContract.getDownloadTime(), 50).before(new Date()) && sysContract.getStatus() == 3)) {
                            //下载已签署文件及附属材料
                            EsignHttpResponse fileDownloadUrl = null;
                            try {
                                fileDownloadUrl = SignDemo.fileDownloadUrl(sysContract.getSignFlowId());
                                JSONObject jsonObject = JSON.parseObject(fileDownloadUrl.getBody());
                                log.info("下载签署合同： {}", fileDownloadUrl.getBody());
                                sysContract.setFileDownloadUrl(jsonObject.getJSONObject("data").getJSONArray("files").getJSONObject(0).getString("downloadUrl"));
                                sysContract.setDownloadTime(new Date());
                                sysContractMapper.updateById(sysContract);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }



                    }
                    return result;
                })
                .orElseGet(() -> {
                    return PageResult.of(List.of());
                }));
    }
}
