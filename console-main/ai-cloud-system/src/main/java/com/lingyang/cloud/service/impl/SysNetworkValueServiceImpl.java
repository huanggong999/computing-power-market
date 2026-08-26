package com.lingyang.cloud.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.common.constant.CacheQueueConstant;
import com.lingyang.cloud.config.WeiXinConfig;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.handler.OnlinePayHandler;
import com.lingyang.cloud.mapper.*;
import com.lingyang.cloud.model.dto.SysNetworkProductValueDTO;
import com.lingyang.cloud.model.query.home.SysNetworkValueQuery;
import com.lingyang.cloud.model.vo.feilian.ResetPasswordVO;
import com.lingyang.cloud.model.vo.feilian.UpdateUserStatusVO;
import com.lingyang.cloud.model.vo.product.SysOpenProductUserPwdVO;
import com.lingyang.cloud.model.vo.product.SysProductIpEnoughVO;
import com.lingyang.cloud.service.*;
import com.lingyang.cloud.utils.FeiLianUtils;
import com.lingyang.common.cache.CacheQueueService;
import com.lingyang.common.cache.queue.QueueMessageBody;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.utils.Optional;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.lingyang.cloud.common.constant.CacheQueueConstant.CUSTOMER_NEW_DAY_BILL_PRODUCT_QUEUE_TYPE;
import static com.lingyang.cloud.enums.order.OrderTypeEnum.PRODUCT;

@Service
@Slf4j
public class SysNetworkValueServiceImpl implements SysNetworkValueService {

    @Autowired
    private SysNetworkValueMapper sysNetworkValueMapper;

    @Autowired
    private SysUserMapper systemUserMapper;

    @Autowired
    private SysCustomerMapper sysCustomerMapper;

    @Autowired
    private SysNetworkProductMapper sysNetworkProductMapper;

    @Autowired
    private SysOrderMapper sysOrderMapper;

    @Resource
    private FeiLianUtils flashLianUtils;

    @Resource
    private SysNetworkProductIpMapper sysNetworkProductIpMapper;

    @Resource
    private CacheQueueService cacheQueueService;

    @Resource
    private SysOrderService orderService;

    @Autowired
    private WeiXinConfig weiXinConfig;
    @Resource
    private SysCustomerVoucherService sysCustomerVoucherService;
    @Resource
    private SysCustomerCreditLineService sysCustomerCreditLineService;
    @Resource
    private SysCustomerCouponService sysCustomerCouponService;
    @Resource
    private SysCustomerBillService sysCustomerBillService;
    @Resource
    private OnlinePayHandler onlinePayHandler;
    @Resource
    private SysCustomerCouponMapper sysCustomerCouponMapper;
    @Resource
    private SysCustomerService sysCustomerService;
    @Resource
    private SysOrderSourceMapper sysOrderSourceMapper;
    @Resource
    private SysActiveRecordMapper sysActiveRecordMapper;
    @Resource
    private SysActiveCouponMapper sysActiveCouponMapper;
    @Resource
    private SysActiveCenterMapper sysActiveCenterMapper;
    @Resource
    private SysMessageMapper sysMessageMapper;


    @Override
    public Result<PageResult<SysNetworkValueEntity>> getPage(PageQuery<SysNetworkValueQuery> pageQuery) {
        SysNetworkValueQuery query = pageQuery.getQuery();
        pageQuery.startPage();
        List<SysNetworkValueEntity> value = sysNetworkValueMapper.getPage(query);
        if (CollectionUtils.isNotEmpty(value)) {
            for (SysNetworkValueEntity sysNetworkValueEntity : value) {
                SysNetworkValueEntity sysNetworkValueEntity1 = sysNetworkValueMapper.selectById(sysNetworkValueEntity.getId());
                if (sysNetworkValueEntity1 != null) {
                    sysNetworkValueEntity.setUserpwdList(sysNetworkValueEntity1.getUserpwdList());
                }
            }
        }
        return Result.success(Optional.of(value)
                .flatMap(entities -> {
                    PageResult<SysNetworkValueEntity> result = PageResult.of(entities);
                    return result;
                })
                .orElseGet(() -> {
                    return PageResult.of(List.of());
                }));
    }

    @Override
    public Result<PageResult<SysNetworkValueEntity>> getPcPage(PageQuery<SysNetworkValueQuery> pageQuery) {
        SysNetworkValueQuery query = pageQuery.getQuery();
        pageQuery.startPage();
        LambdaQueryWrapper<SysNetworkValueEntity> queryWrapper = Wrappers.lambdaQuery(SysNetworkValueEntity.class)
                .like(StringUtils.isNotBlank(query.getFormName()), SysNetworkValueEntity::getFormName, query.getFormName())
                .like(StringUtils.isNotBlank(query.getProductName()), SysNetworkValueEntity::getProductName, query.getProductName())
                .eq(SysNetworkValueEntity::getUserId, SecurityContext.getUserInfo().getUserId())
                .eq(SysNetworkValueEntity::getFormType, 2)
                .orderByDesc(SysNetworkValueEntity::getCreateTime);
        List<SysNetworkValueEntity> value = sysNetworkValueMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(value)) {
            for (SysNetworkValueEntity SysNetworkValueEntity : value) {
                SysCustomerEntity sysCustomerEntity = sysCustomerMapper.selectById(SysNetworkValueEntity.getUserId());
                if (sysCustomerEntity != null) {
                    SysNetworkValueEntity.setUserName(sysCustomerEntity.getCustomerName());
                    SysNetworkValueEntity.setPhone(sysCustomerEntity.getPhone());
                }
                SysNetworkProductEntity product = sysNetworkProductMapper.selectById(SysNetworkValueEntity.getProductId());
                if (product != null) {
                    SysNetworkValueEntity.setProductImg(product.getImage().toString());
//                    SysNetworkValueEntity.setPayAmount(product.getPayPrice());
                    SysNetworkValueEntity.setReferPrice(product.getReferPrice());
                    SysNetworkValueEntity.setCrossedPrice(product.getCrossedPrice());
                    SysNetworkValueEntity.setIsBandwidthDisplay(product.getIsBandwidthDisplay());
                    SysNetworkValueEntity.setIsIpCountDisplay(product.getIsIpCountDisplay());
                }
            }
        }
        return Result.success(Optional.of(value)
                .flatMap(entities -> {
                    PageResult<SysNetworkValueEntity> result = PageResult.of(entities);
                    return result;
                })
                .orElseGet(() -> {
                    return PageResult.of(List.of());
                }));
    }

    @Override
    public SysNetworkProductValueDTO getProductDetail(Long id) {
        SysNetworkValueEntity sysNetworkValueEntity = sysNetworkValueMapper.selectById(id);
        SysOrderEntity sysOrderEntity = sysOrderMapper.selectOne(Wrappers.lambdaQuery(SysOrderEntity.class)
                .eq(SysOrderEntity::getNetworkValueId, id)
                .eq(SysOrderEntity::getOrderType,PRODUCT));

        SysNetworkProductEntity sysNetworkProductEntity = sysNetworkProductMapper.selectOne(Wrappers.lambdaQuery(SysNetworkProductEntity.class)
                .eq(SysNetworkProductEntity::getId, sysNetworkValueEntity.getProductId()));

        SysNetworkProductValueDTO sysNetworkProductValueDTO = BeanUtil.copyProperties(sysNetworkValueEntity, SysNetworkProductValueDTO.class);
        sysNetworkProductValueDTO.setIsAccountIpBinding(sysNetworkProductEntity.getIsAccountIpBinding());
        sysNetworkProductValueDTO.setIsIpDisplay(sysNetworkProductEntity.getIsIpDisplay());
        sysNetworkProductValueDTO.setIsBandwidthDisplay(sysNetworkProductEntity.getIsBandwidthDisplay());
        if (sysOrderEntity != null) {
            sysNetworkProductValueDTO.setOrderNo(sysOrderEntity.getOrderNo());
            sysNetworkProductValueDTO.setPayTime(sysOrderEntity.getPayTime());
        }
        JSONArray userpwdList = sysNetworkValueEntity.getUserpwdList();
        if (userpwdList != null){
            List<SysOpenProductUserPwdVO> userPwdList = JSON.parseArray(userpwdList.toJSONString(), SysOpenProductUserPwdVO.class);
            sysNetworkProductValueDTO.setUserPwdList(userPwdList);
        }
        String email = sysNetworkValueEntity.getEmail();
        if (StringUtils.isNotBlank(email)){
            List<String> emailList =  Arrays.stream(email.split(",")).toList();
            sysNetworkProductValueDTO.setEmails(emailList);
        }
        return sysNetworkProductValueDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> stop(Long valueId) {
        SysNetworkValueEntity sysNetworkValueEntity = sysNetworkValueMapper.selectById(valueId);
        if (sysNetworkValueEntity != null && sysNetworkValueEntity.getActualStatus() == 2) {
            sysNetworkValueEntity.setActualStatus(4);
            sysNetworkValueMapper.updateById(sysNetworkValueEntity);

            //修改用户状态
            List<SysOpenProductUserPwdVO> userPwdList = JSON.parseArray(sysNetworkValueEntity.getUserpwdList().toJSONString(), SysOpenProductUserPwdVO.class);
            for (SysOpenProductUserPwdVO sysOpenProductUserPwdVO : userPwdList) {
                String userId = flashLianUtils.getUserInfo(sysOpenProductUserPwdVO.getEmail()).get("id").toString();
                UpdateUserStatusVO vo = new UpdateUserStatusVO();
                vo.setId(userId);
                vo.setStatus("disable");
                flashLianUtils.updateUserStatus(vo);
            }
        }else {
            return Result.error("只有正在运行中的产品才可以停线");
        }
        return Result.success();
    }

    @Override
    public Result<Boolean> start(Long valueId) {
        log.info("执行上线操作");
        SysNetworkValueEntity sysNetworkValueEntity = sysNetworkValueMapper.selectById(valueId);
        if (sysNetworkValueEntity != null && sysNetworkValueEntity.getActualStatus() == 4) {
            sysNetworkValueEntity.setActualStatus(2);
            sysNetworkValueMapper.updateById(sysNetworkValueEntity);

            //修改用户状态
            List<SysOpenProductUserPwdVO> userPwdList = JSON.parseArray(sysNetworkValueEntity.getUserpwdList().toJSONString(), SysOpenProductUserPwdVO.class);
            for (SysOpenProductUserPwdVO sysOpenProductUserPwdVO : userPwdList) {
                String userId = flashLianUtils.getUserInfo(sysOpenProductUserPwdVO.getEmail()).get("id").toString();

                UpdateUserStatusVO vo = new UpdateUserStatusVO();
                vo.setId(userId);
                vo.setStatus("enable");
                flashLianUtils.updateUserStatus(vo);
            }
        }else {
            return Result.error("只有已停线的产品才可以上线");
        }
        return Result.success();
    }

    @Override
    public Result<Boolean> cancel(Long valueId) {
        log.info("执行撤线操作");
        SysNetworkValueEntity sysNetworkValueEntity = sysNetworkValueMapper.selectById(valueId);
        sysNetworkValueEntity.setActualStatus(5);
        sysNetworkValueMapper.updateById(sysNetworkValueEntity);

        //修改用户状态
        List<SysOpenProductUserPwdVO> userPwdList = JSON.parseArray(sysNetworkValueEntity.getUserpwdList().toJSONString(), SysOpenProductUserPwdVO.class);
        for (SysOpenProductUserPwdVO sysOpenProductUserPwdVO : userPwdList) {
            String userId = flashLianUtils.getUserInfo(sysOpenProductUserPwdVO.getEmail()).get("id").toString();

            UpdateUserStatusVO vo = new UpdateUserStatusVO();
            vo.setId(userId);
            vo.setStatus("offline");
            flashLianUtils.updateUserStatus(vo);

            sysNetworkProductIpMapper.update(null,new LambdaUpdateWrapper<SysNetworkProductIpEntity>()
                    .set(SysNetworkProductIpEntity::getStatus, 2)
                    .set(SysNetworkProductIpEntity::getUpdateTime, new Date())
                    .eq(SysNetworkProductIpEntity::getIp, sysOpenProductUserPwdVO.getIp()));

            for (Object queue : cacheQueueService.getAllQueue()) {
                if (queue instanceof QueueMessageBody messageBody) {
                    if (messageBody.getMessageType().equals(CUSTOMER_NEW_DAY_BILL_PRODUCT_QUEUE_TYPE) && messageBody.getBody().equals(valueId.toString())) {
                        cacheQueueService.remove(messageBody);
                        log.info("产品{}已撤线", valueId);
                    }
                }
            }

            //如果客户撤线，从撤线时间开始先改为等待中，2个月后再改为未使用。
            cacheQueueService.addDelayQueue(CacheQueueConstant.AGI_C_IP_QUEUE_TYPE, sysOpenProductUserPwdVO.getIp(), 60 * 60 * 24 * 30 * 2);
        }
        return Result.success();
    }

    @Override
    public Result<Boolean> resetPassword(String email) {
        String userId = flashLianUtils.getUserInfo(email).get("id").toString();
        ResetPasswordVO vo2 = new ResetPasswordVO();
        vo2.setId(userId);
        vo2.setCustomPassword("00000000");
        flashLianUtils.resetPassword(vo2);
        SysNetworkValueEntity sysNetworkValueEntity = sysNetworkValueMapper.selectOne(Wrappers.lambdaQuery(SysNetworkValueEntity.class)
                .like(SysNetworkValueEntity::getEmail, email)
                .eq(SysNetworkValueEntity::getActualStatus, 2));
        List<SysOpenProductUserPwdVO> userPwdList = JSON.parseArray(sysNetworkValueEntity.getUserpwdList().toJSONString(), SysOpenProductUserPwdVO.class);
        userPwdList.stream().filter(vo -> vo.getEmail().equals(email)).findFirst().ifPresent(vo -> {
            vo.setPwd("00000000");
            sysNetworkValueEntity.setUserpwdList(JSON.parseArray(JSON.toJSONString(userPwdList)));
        });
        sysNetworkValueMapper.updateById(sysNetworkValueEntity);
        return Result.success();
    }

    @Override
    public Result<Boolean> ipEnough(SysProductIpEnoughVO vo) {
        List<SysNetworkProductIpEntity> sysNetworkProductIpEntities = sysNetworkProductIpMapper.selectList(Wrappers.lambdaQuery(SysNetworkProductIpEntity.class)
                .eq(SysNetworkProductIpEntity::getProductId, vo.getProductId())
                .eq(SysNetworkProductIpEntity::getStatus, 0));
        boolean flag = sysNetworkProductIpEntities.size() >= vo.getIpNumber();
        if (!flag) {
            SysNetworkProductEntity sysNetworkProductEntity = sysNetworkProductMapper.selectById(vo.getProductId());
            SysMessage m = new SysMessage();
            m.setMsgType(6);
            m.setText("AGI-C的【" + sysNetworkProductEntity.getName() + "】产品IP地址不足3个，请及时处理！");
            m.setStatus(1);
            m.setUserId(SecurityContext.getUserInfo().getUserId());
            sysMessageMapper.insert(m);
        }
        return Result.success(flag);

    }
}
