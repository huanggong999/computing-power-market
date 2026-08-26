package com.lingyang.cloud.common.login.handler;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.common.login.model.PcLoginUserInfo;
import com.lingyang.cloud.common.login.param.WechatAuthorizedLoginParam;
import com.lingyang.cloud.entity.SysCustomerEntity;
import com.lingyang.cloud.mapper.SysCustomerMapper;
import com.lingyang.common.core.model.RequestSource;
import com.lingyang.common.core.security.model.LoginUserInfoDetail;
import com.lingyang.common.core.utils.BeanUtils;
import com.lingyang.common.core.utils.Builder;
import com.lingyang.common.core.utils.StringUtils;
import com.lingyang.common.security.access.login.LoginHandler;
import com.lingyang.common.security.exception.LoginException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.lingyang.cloud.common.constant.CommonHandlerConstant.WECHAT_LOGIN_HANDLER_VALUE;
import static com.lingyang.common.core.enums.HttpStatus.WECHAT_NOT_REGISTERED;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/3/7 15:09
 */
@Service
@Slf4j
public class WechatAuthorizedLoginHandler implements LoginHandler<WechatAuthorizedLoginParam> {
    @Resource
    private SysCustomerMapper customerMapper;

    @Override
    public LoginUserInfoDetail login(WechatAuthorizedLoginParam bodyParam) throws LoginException, IOException {
        SysCustomerEntity sysCustomerEntity = customerMapper.selectOne(Wrappers.lambdaQuery(SysCustomerEntity.class)
                .eq(SysCustomerEntity::getUnionId, bodyParam.getUnionId()));
        if (sysCustomerEntity == null){
            throw new LoginException(WECHAT_NOT_REGISTERED);
        }
        if (StringUtils.isBlank(sysCustomerEntity.getOpenId())){
            sysCustomerEntity.setOpenId(bodyParam.getOpenId());
            customerMapper.updateById(sysCustomerEntity);
        }
        return Builder.of(BeanUtils.copyBean(sysCustomerEntity, PcLoginUserInfo.class))
                .set(PcLoginUserInfo::setUserId, sysCustomerEntity.getId())
                .set(PcLoginUserInfo::setMenuIdList, List.of())
                .set(PcLoginUserInfo::setRoleList, List.of())
                .set(PcLoginUserInfo::setPermissions, List.of())
                .build();
    }

    @Override
    public RequestSource source() {
        return RequestSource.create(WECHAT_LOGIN_HANDLER_VALUE);
    }

    @Override
    public Class<WechatAuthorizedLoginParam> jsonClass() {
        return WechatAuthorizedLoginParam.class;
    }
}
