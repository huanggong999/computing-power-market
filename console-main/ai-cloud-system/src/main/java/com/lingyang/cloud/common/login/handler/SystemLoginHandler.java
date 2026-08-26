package com.lingyang.cloud.common.login.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.cloud.common.login.model.SysLoginUserInfo;
import com.lingyang.cloud.common.login.model.SysRoleInfo;
import com.lingyang.cloud.common.login.param.UsernamePasswordLoginParam;
import com.lingyang.cloud.entity.SysMenuEntity;
import com.lingyang.cloud.entity.SysRoleMenuEntity;
import com.lingyang.cloud.entity.SysUserEntity;
import com.lingyang.cloud.mapper.SysMenuMapper;
import com.lingyang.cloud.mapper.SysRoleMapper;
import com.lingyang.cloud.mapper.SysRoleMenuMapper;
import com.lingyang.cloud.mapper.SysUserMapper;
import com.lingyang.common.core.model.RequestSource;
import com.lingyang.common.core.security.model.LoginUserInfoDetail;
import com.lingyang.common.core.security.model.Permission;
import com.lingyang.common.core.utils.BeanUtils;
import com.lingyang.common.core.utils.Builder;
import com.lingyang.common.core.utils.Optional;
import com.lingyang.common.security.access.login.LoginHandler;
import com.lingyang.common.security.exception.LoginException;
import com.lingyang.common.security.utils.PasswordUtils;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.lingyang.cloud.common.constant.CommonHandlerConstant.SYSTEM_LOGIN_HANDLER_VALUE;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/17 18:02
 */
@Service
public class SystemLoginHandler implements LoginHandler<UsernamePasswordLoginParam> {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysMenuMapper sysMenuMapper;

    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;
    @Override
    public LoginUserInfoDetail login(UsernamePasswordLoginParam bodyParam) throws LoginException {
        String userName = Optional.of(bodyParam.getUsername())
                .orElseThrow(() -> new LoginException("用户名为空"));
        String password = Optional.of(bodyParam.getPassword())
                .orElseThrow(() -> new LoginException("密码为空"));
        SysUserEntity sysUserEntity = sysUserMapper.selectOne(Wrappers.lambdaQuery(SysUserEntity.class)
                .eq(SysUserEntity::getUserName, userName));
        if (ObjectUtils.isEmpty(sysUserEntity) || !PasswordUtils.matchesPassword(password, sysUserEntity.getPassword())) {
            throw new LoginException("用户名或密码错误");
        }
        if (!sysUserEntity.getStatus().equals(StatusEnum.OK)) {
            throw new LoginException("账户异常，无法登陆");
        }
        AtomicReference<List<Permission>> permissionList = new AtomicReference<>(null);
        AtomicReference<List<Long>> menuIdList = new AtomicReference<>(null);
        AtomicReference<SysRoleInfo> roleInfo = new AtomicReference<>(null);
        Optional.of(sysUserEntity.getRoleId())
                .flatMap(roleId -> sysRoleMapper.selectById(roleId))
                .flatMap(role -> BeanUtils.copyBean(role, SysRoleInfo.class))
                .ifPresent(sysRoleInfo -> {
                    roleInfo.set(sysRoleInfo);
                    LambdaQueryWrapper<SysRoleMenuEntity> roleMenuWrapper = Wrappers.lambdaQuery(SysRoleMenuEntity.class)
                            .eq(SysRoleMenuEntity::getRoleId, sysRoleInfo.getId())
                            .select(SysRoleMenuEntity::getMenuId);
                    Optional.of(sysRoleMenuMapper.selectList(roleMenuWrapper))
                            .flatMap(sysRoleMenu -> sysMenuMapper.selectList(Wrappers.lambdaQuery(SysMenuEntity.class)
                                    .in(SysMenuEntity::getId, sysRoleMenu.stream().map(SysRoleMenuEntity::getMenuId).toList())))
                            .ifPresent(menuList -> {
                                List<Long> menuIds = new ArrayList<>(menuList.size());
                                List<Permission> permissions = new ArrayList<>(menuList.size());
                                menuList.forEach(menu -> {
                                    Optional.of(menu.getPerms())
                                            .ifPresent(per -> permissions.add(new Permission(per)));
                                    menuIds.add(menu.getId());
                                });
                                permissionList.set(permissions);
                                menuIdList.set(menuIds);
                            });
                });
        // 封装登陆数据
        return Builder.of(BeanUtils.copyBean(sysUserEntity, SysLoginUserInfo.class))
                .set(SysLoginUserInfo::setUsername, sysUserEntity.getUserName())
                .set(SysLoginUserInfo::setUserId, sysUserEntity.getId())
                .set(SysLoginUserInfo::setMenuIdList, menuIdList.get())
                .set(SysLoginUserInfo::setRoleList, roleInfo.get() != null ? List.of(roleInfo.get()) : List.of())
                .set(SysLoginUserInfo::setPermissions, permissionList.get())
                .build();
    }

    @Override
    public RequestSource source() {
        return RequestSource.create(SYSTEM_LOGIN_HANDLER_VALUE);
    }

    @Override
    public Class<UsernamePasswordLoginParam> jsonClass() {
        return UsernamePasswordLoginParam.class;
    }
}
