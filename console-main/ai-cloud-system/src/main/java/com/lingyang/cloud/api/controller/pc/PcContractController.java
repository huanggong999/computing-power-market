package com.lingyang.cloud.api.controller.pc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lingyang.cloud.entity.SysContract;
import com.lingyang.cloud.entity.SysCreditContract;
import com.lingyang.cloud.entity.SysCustomerBillEntity;
import com.lingyang.cloud.entity.SysCustomerCreditLineEntity;
import com.lingyang.cloud.enums.order.OrderStatusEnum;
import com.lingyang.cloud.enums.source.SourceTypeEnum;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.cloud.mapper.SysContractMapper;
import com.lingyang.cloud.mapper.SysCreditContractMapper;
import com.lingyang.cloud.mapper.SysCustomerBillMapper;
import com.lingyang.cloud.model.dto.OrderContractDTO;
import com.lingyang.cloud.model.query.home.SysContractQuery;
import com.lingyang.cloud.model.vo.OrderContractCompanyInfoVO;
import com.lingyang.cloud.service.SysContractService;
import com.lingyang.cloud.service.SysCustomerCreditLineService;
import com.lingyang.cloud.utils.esign.EsignDemoException;
import com.lingyang.cloud.utils.esign.EsignHttpResponse;
import com.lingyang.cloud.utils.esign.SignDemo;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.DateUtils;
import com.lingyang.common.core.utils.IdUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Slf4j
@Tag(name = "pc端-合同")
@RestController
@RequestMapping("/pc/contract")
public class PcContractController {

    @Autowired
    private SysContractService sysContractService;

    @Autowired
    private SysContractMapper sysContractMapper;

    @Autowired
    private SysCreditContractMapper sysCreditContractMapper;

    @Autowired
    private SysCustomerCreditLineService sysCustomerCreditLineService;

    @Autowired
    private SysCustomerBillMapper sysCustomerBillMapper;

    @Operation(summary = "订单合同分页", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysContract>> page(SysContractQuery query) {
        return sysContractService.getPage(PageQuery.build(query));
    }

    @Operation(summary = "生成订单合同")
    @PostMapping("/generate-order-contract")
    public Result<Void> orderContract(@RequestBody OrderContractDTO d) throws EsignDemoException {
        return  sysContractService.orderContract(d);
    }


    @Operation(summary = "乙方信息")
    @GetMapping("/company-info")
    public Result<OrderContractCompanyInfoVO> companyInfo() {
        OrderContractCompanyInfoVO vo = new OrderContractCompanyInfoVO();
        vo.setClientName(P1);
        vo.setClientContactPerson(P3);
        vo.setClientContactPhone(P4);
        vo.setClientContactAddress(P2);
        return Result.success(vo);
    }

    @Operation(summary = "上传纸质合同")
    @PostMapping("/upload-contract")
    public Result<Void> uploadContract(@RequestBody OrderContractDTO d) {
        if (d.getId() != null) {
            SysContract contract = new SysContract();
            contract.setId(d.getId());
            contract.setStatus(2);
            contract.setSignUploadImg(d.getSignUploadImg());
            sysContractMapper.updateById(contract);
        }
        return Result.success();
    }


    @Operation(summary = "回调信息")
    @PostMapping("/notify")
    public void notifyA(@RequestBody Map map) throws EsignDemoException {
        log.info("合同回调信息: {}", JSON.toJSONString(map, true));
        log.info("合同回调信息11111: {}", JSON.toJSONString(map.keySet()));
        log.info("合同回调信息 action: {}", map.get("action").toString());
        String action = map.get("action").toString();

        if (action.equals("SIGN_FLOW_COMPLETE")) {
            String signFlowId = map.get("signFlowId").toString();
            SysCreditContract creditContract = sysCreditContractMapper.selectOne(
                    new LambdaQueryWrapper<SysCreditContract>()
                            .eq(SysCreditContract::getSignFlowId, signFlowId)
            );
            if (creditContract != null) {
                if (map.get("signFlowStatus").toString().equals("3")) {
                    creditContract.setStatus(7);
                    sysCreditContractMapper.updateById(creditContract);
                }
                if (map.get("signFlowStatus").toString().equals("5")) {
                    creditContract.setStatus(4);
                    sysCreditContractMapper.updateById(creditContract);
                }
            } else {
                SysContract selectOne = sysContractMapper.selectOne(
                        new LambdaQueryWrapper<SysContract>()
                                .eq(SysContract::getSignFlowId, signFlowId)
                );
                if (selectOne != null) {
                    if (map.get("signFlowStatus").toString().equals("3")) {
                        selectOne.setStatus(7);
                        sysContractMapper.updateById(selectOne);
                    }
                    if (map.get("signFlowStatus").toString().equals("5")) {
                        selectOne.setStatus(4);
                        sysContractMapper.updateById(selectOne);
                    }
                }
            }
        }

        if (action.equals("SIGN_MISSON_COMPLETE")) {
            String signFlowId = map.get("signFlowId").toString();
            SysContract selectOne = sysContractMapper.selectOne(
                    new LambdaQueryWrapper<SysContract>()
                            .eq(SysContract::getSignFlowId, signFlowId)
            );
            if (selectOne != null) {
                if (map.get("signResult").toString().equals("2")) {
                    selectOne.setCompleteTime(new Date());
                    selectOne.setStatus(3);
                    //下载已签署文件及附属材料
                    EsignHttpResponse fileDownloadUrl = SignDemo.fileDownloadUrl(signFlowId);
                    JSONObject jsonObject = JSON.parseObject(fileDownloadUrl.getBody());
                    log.info("下载签署合同： {}", fileDownloadUrl.getBody());
                    selectOne.setFileDownloadUrl(jsonObject.getJSONObject("data").getJSONArray("files").getJSONObject(0).getString("downloadUrl"));
                    selectOne.setDownloadTime(new Date());
                    sysContractMapper.updateById(selectOne);
                }
            } else {
                log.info("未找到订单合同，查询授信额合同");
                SysCreditContract creditContract = sysCreditContractMapper.selectOne(
                        new LambdaQueryWrapper<SysCreditContract>()
                                .eq(SysCreditContract::getSignFlowId, signFlowId)
                );
                if (creditContract != null) {
                    if (map.get("signResult").toString().equals("2")) {
                        creditContract.setCompleteTime(new Date());
                        creditContract.setStatus(3);
                        //下载已签署文件及附属材料
                        EsignHttpResponse fileDownloadUrl = SignDemo.fileDownloadUrl(signFlowId);
                        JSONObject jsonObject = JSON.parseObject(fileDownloadUrl.getBody());
                        log.info("下载签署合同： {}", fileDownloadUrl.getBody());
                        creditContract.setFileDownloadUrl(jsonObject.getJSONObject("data").getJSONArray("files").getJSONObject(0).getString("downloadUrl"));
                        creditContract.setDownloadTime(new Date());
                        sysCreditContractMapper.updateById(creditContract);


                        // 发放授信额
                        SysCustomerCreditLineEntity entity = new SysCustomerCreditLineEntity();
                        entity.setCustomerId(creditContract.getUserId());
                        entity.setTotalAmount(creditContract.getAmount());
//                        entity.setUseAmount();
                        entity.setUseAmount(BigDecimal.ZERO);
                        entity.setStatus(StatusEnum.OK);
                        entity.setUseTimeStart(entity.getUseTimeStart() == null ? DateUtils.getNowDate() : entity.getUseTimeStart());
                        entity.setUseTimeEnd(DateUtils.addYears(entity.getUseTimeStart(), 1));
                        sysCustomerCreditLineService.addCustomerCreditLine(entity);
                        // 增加账单记录
                        SysCustomerBillEntity billEntity = new SysCustomerBillEntity();
                        billEntity.setPayPrice(creditContract.getAmount());
                        billEntity.setCustomerId(creditContract.getUserId());
                        billEntity.setBill(DateUtils.parseDateToStr("yyyy-MM", new Date()));
                        billEntity.setBillNo(IdUtils.simpleUUID());
                        billEntity.setBillDate(new Date());
                        billEntity.setSourceType(SourceTypeEnum.CREDIT_AMOUNT_RECHARGE);
                        billEntity.setBillType("充值-使用");
                        billEntity.setSettleType("充值");
                        billEntity.setArrearsAmount(BigDecimal.ZERO);
                        billEntity.setCreditLineAmount(creditContract.getAmount());
                        billEntity.setPayStatus(OrderStatusEnum.PAID);
                        billEntity.setPayTime(new Date());
                        billEntity.setBillStartTime(new Date());
                        billEntity.setBillEndTime(new Date());
                        sysCustomerBillMapper.insert(billEntity);

                    }
                }
            }

        }
    }




    public static final String notifyUrl = "http://115.190.35.186:9100/api/pc/contract/";







    public static final String P1 = "逸云数智科技（深圳）有限公司 ";
    public static final String P2 = "深圳市福田区福田街道彩田路3069号星河世纪A栋3602";
    public static final String P3 = "黄玉华";
    public static final String P4 = "13828802018";


    /**
     * 待填充的模板ID
     */
//    public static final String docTemplateId = "b039123eeed24115b507b9176f371e03";
    public static final String docTemplateId = "3a6e29590f5c4590b6d79ca96e476016";

    /**
     * 授信额模板id
     */
    public static final String docTemplateIdCredit = "0425bd038a2f4b1f8650c5a67487ef82";

    /**
     * 甲方名称
     */
    public static final String clientName = "p1";

    /**
     * 甲方联系人
     */
    public static final String clientContactPerson = "p2";

    /**
     * 甲方联系电话
     */
    public static final String clientContactPhone = "p3";


    /**
     * 甲方联系地址
     */
    public static final String clientContactAddress = "p4";



    /**
     * 时间5
     */
    public static final String time5 = "p5";

    /**
     * 时间6
     */
    public static final String time6 = "p6";





}
