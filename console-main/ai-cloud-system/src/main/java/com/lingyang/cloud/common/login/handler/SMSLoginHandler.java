package com.lingyang.cloud.common.login.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.common.login.model.PcLoginUserInfo;
import com.lingyang.cloud.common.login.param.SMSLoginParam;
import com.lingyang.cloud.entity.SysCustomerEntity;
import com.lingyang.cloud.mapper.SysCustomerMapper;
import com.lingyang.common.core.enums.HttpStatus;
import com.lingyang.common.core.model.RequestSource;
import com.lingyang.common.core.security.model.LoginUserInfoDetail;
import com.lingyang.common.core.utils.BeanUtils;
import com.lingyang.common.core.utils.Builder;
import com.lingyang.common.core.utils.Optional;
import com.lingyang.common.core.utils.StringUtils;
import com.lingyang.common.security.access.login.LoginHandler;
import com.lingyang.common.security.exception.LoginException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.lingyang.cloud.common.constant.CommonHandlerConstant.SMS_LOGIN_HANDLER_VALUE;

/**
 * @Description: 验证码登陆处理
 * @Author: 王小龙
 * @Date: 2024/10/17 17:37
 */
@Slf4j
@Service
public class SMSLoginHandler implements LoginHandler<SMSLoginParam> {

    @Resource
    private SysCustomerMapper customerMapper;

    @Override
    public LoginUserInfoDetail login(SMSLoginParam bodyParam) throws LoginException {
        String phone = Optional.of(bodyParam.getPhone())
                .orElseThrow(() -> new LoginException("手机号为空"));
        LambdaQueryWrapper<SysCustomerEntity> customerWrapper = Wrappers.lambdaQuery(SysCustomerEntity.class)
                .eq(SysCustomerEntity::getPhone, phone);
        SysCustomerEntity customer = Optional.of(customerMapper.selectOne(customerWrapper))
//                .orElseGet(() -> {
//                    // 新客户
//                    SysCustomerEntity newCustomer = Builder.of(SysCustomerEntity::new)
//                            .set(SysCustomerEntity::setId, IdUtils.nextId())
//                            .set(SysCustomerEntity::setCustomerName, phone)
//                            .set(SysCustomerEntity::setPhone, phone)
//                            .set(SysCustomerEntity::setStatus, StatusEnum.OK)
//                            .set(SysCustomerEntity::setRegisterTime, DateUtils.getNowDate())
//                            .build();
//                    customerMapper.insert(newCustomer);
//                    return newCustomer;
//                });
                .orElseThrow(() -> new LoginException(HttpStatus.UNREGISTERED));
        if (StringUtils.isNotBlank(bodyParam.getUnionId())){
            Long aLong = customerMapper.selectCount(
                    new LambdaQueryWrapper<SysCustomerEntity>()
                            .eq(SysCustomerEntity::getUnionId, bodyParam.getUnionId())
            );
            if (aLong == 0) {
                customer.setUnionId(bodyParam.getUnionId());
                customer.setOpenId(bodyParam.getOpenId());
                customerMapper.updateById(customer);
            } else {
                log.info("该unionid已经绑定账号：{}", bodyParam.getUnionId());
            }

        }
        return Builder.of(BeanUtils.copyBean(customer, PcLoginUserInfo.class))
                .set(PcLoginUserInfo::setUserId, customer.getId())
                .set(PcLoginUserInfo::setMenuIdList, List.of())
                .set(PcLoginUserInfo::setRoleList, List.of())
                .set(PcLoginUserInfo::setPermissions, List.of())
                .build();
    }

    @Override
    public RequestSource source() {
        return RequestSource.create(SMS_LOGIN_HANDLER_VALUE);
    }

    @Override
    public Class<SMSLoginParam> jsonClass() {
        return SMSLoginParam.class;
    }
}
