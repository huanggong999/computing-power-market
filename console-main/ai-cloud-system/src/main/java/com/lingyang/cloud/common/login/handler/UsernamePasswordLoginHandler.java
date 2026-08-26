package com.lingyang.cloud.common.login.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.common.login.model.PcLoginUserInfo;
import com.lingyang.cloud.common.login.param.UsernamePasswordLoginParam;
import com.lingyang.cloud.entity.SysCustomerEntity;
import com.lingyang.cloud.mapper.SysCustomerMapper;
import com.lingyang.common.core.model.RequestSource;
import com.lingyang.common.core.security.model.LoginUserInfoDetail;
import com.lingyang.common.core.utils.BeanUtils;
import com.lingyang.common.core.utils.Builder;
import com.lingyang.common.core.utils.Optional;
import com.lingyang.common.core.utils.StringUtils;
import com.lingyang.common.security.access.login.LoginHandler;
import com.lingyang.common.security.exception.LoginException;
import com.lingyang.common.security.utils.PasswordUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.lingyang.cloud.common.constant.CommonHandlerConstant.USERNAME_PASSWORD_LOGIN_HANDLER_VALUE;

/**
 * @Description: 用户名密码登陆
 * @Author: 王小龙
 * @Date: 2024/10/17 17:51
 */
@Slf4j
@Service
public class UsernamePasswordLoginHandler implements LoginHandler<UsernamePasswordLoginParam> {

    @Resource
    private SysCustomerMapper customerMapper;

    @Override
    public LoginUserInfoDetail login(UsernamePasswordLoginParam bodyParam) throws LoginException {
        if (bodyParam.getId() == null) {
            String userName = Optional.of(bodyParam.getUsername())
                    .orElseThrow(() -> new LoginException("用户名为空"));
            String password = Optional.of(bodyParam.getPassword())
                    .orElseThrow(() -> new LoginException("密码为空"));
            SysCustomerEntity customer = customerMapper.selectOne(Wrappers.lambdaQuery(SysCustomerEntity.class)
                    .eq(SysCustomerEntity::getPhone, userName));
            if (customer == null || !PasswordUtils.matchesPassword(password, customer.getPassword())) {
                throw new LoginException("用户名或密码错误");
            }
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
        } else {
            SysCustomerEntity customer = customerMapper.selectOne(Wrappers.lambdaQuery(SysCustomerEntity.class)
                    .eq(SysCustomerEntity::getId, bodyParam.getId()));
            if (customer == null) {
                throw new LoginException("用户名或密码错误");
            }
            return Builder.of(BeanUtils.copyBean(customer, PcLoginUserInfo.class))
                    .set(PcLoginUserInfo::setUserId, customer.getId())
                    .set(PcLoginUserInfo::setMenuIdList, List.of())
                    .set(PcLoginUserInfo::setRoleList, List.of())
                    .set(PcLoginUserInfo::setPermissions, List.of())
                    .build();
        }

    }

    @Override
    public RequestSource source() {
        return RequestSource.create(USERNAME_PASSWORD_LOGIN_HANDLER_VALUE);
    }

    @Override
    public Class<UsernamePasswordLoginParam> jsonClass() {
        return UsernamePasswordLoginParam.class;
    }
}
