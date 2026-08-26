package com.lingyang.cloud.api.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysRoleEntity;
import com.lingyang.cloud.entity.SysUserEntity;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.cloud.mapper.SysRoleMapper;
import com.lingyang.cloud.mapper.SysUserMapper;
import com.lingyang.cloud.model.edit.user.SysUserEditDTO;
import com.lingyang.cloud.model.query.user.SysUserQueryDTO;
import com.lingyang.cloud.model.vo.user.SysUserDetailVO;
import com.lingyang.cloud.model.vo.user.SysUserListVO;
import com.lingyang.cloud.model.vo.user.SysUserRoleVO;
import com.lingyang.cloud.api.service.system.SystemUserService;
import com.lingyang.common.core.exception.http.HttpParamsException;
import com.lingyang.common.core.model.RequestSource;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.BeanUtils;
import com.lingyang.common.core.utils.Optional;
import com.lingyang.common.security.TokenService;
import com.lingyang.common.security.utils.PasswordUtils;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.lingyang.cloud.common.constant.CommonHandlerConstant.SYSTEM_LOGIN_HANDLER_VALUE;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/21 15:10
 */
@Service
public class SystemUserServiceImpl implements SystemUserService {
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private TokenService tokenService;

    @Override
    public PageResult<SysUserListVO> getPage(PageQuery<SysUserQueryDTO> page) {
        page.startPage();
        return PageResult.of(getList(page.getQuery()));
    }

    @Override
    public List<SysUserListVO> getList(SysUserQueryDTO queryDTO) {
        LambdaQueryWrapper<SysUserEntity> userWrapper = Wrappers.lambdaQuery(SysUserEntity.class)
                .eq(ObjectUtils.isNotEmpty(queryDTO.getStatus()), SysUserEntity::getStatus, queryDTO.getStatus())
                .orderByDesc(SysUserEntity::getUpdateTime);
        return BeanUtils.copyList(sysUserMapper.selectList(userWrapper), SysUserListVO.class);
    }


    @Override
    public SysUserDetailVO getDetail(Long id) {
        SysUserEntity sysUserEntity = Optional.of(id)
                .flatMap(userId -> sysUserMapper.selectById(id))
                .orElseThrow(() -> Result.buildError(HttpParamsException.class, "用户不存在：" + id));
        SysUserDetailVO userDetail = BeanUtils.copyBean(sysUserEntity, SysUserDetailVO.class);
        // 获取角色
        Optional.of(sysUserEntity.getRoleId())
                .flatMap(roleId -> sysRoleMapper.selectById(roleId))
                .ifPresent(role -> {
                    SysUserRoleVO sysUserRoleVO = new SysUserRoleVO();
                    sysUserRoleVO.setRoleId(role.getId());
                    sysUserRoleVO.setRoleName(role.getRoleName());
                    userDetail.setRole(sysUserRoleVO);
                });
        return userDetail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean save(SysUserEditDTO dto) {
        dto.setPassword(PasswordUtils.encryptPassword(dto.getPassword()));
        SysUserEntity sysUserEntity = BeanUtils.copyBean(dto, SysUserEntity.class);
        boolean flag = sysUserMapper.insert(sysUserEntity) > 0;
        if (flag) {
            tokenService.clearToken(RequestSource.getSource(SYSTEM_LOGIN_HANDLER_VALUE), dto.getId());
        }
        return flag;
    }

    @Override
    public void checkSaveParams(SysUserEditDTO d) throws HttpParamsException {
        checkRole(d.getRoleId());
        d.setId(null);
        LambdaQueryWrapper<SysUserEntity> userWrapper = Wrappers.lambdaQuery(SysUserEntity.class)
                .eq(SysUserEntity::getUserName, d.getUserName());
        Optional.of(sysUserMapper.selectCount(userWrapper))
                .ifPresent(count -> Optional.of(count == 0)
                        .orElseThrow(() -> Result.buildError(HttpParamsException.class, "用户名已存在")));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(SysUserEditDTO dto) {
        Optional.of(dto.getPassword())
                .ifPresent(password -> dto.setPassword(PasswordUtils.encryptPassword(password)));
        boolean flag = sysUserMapper.updateById(BeanUtils.copyBean(dto, SysUserEntity.class)) > 0;
        if (flag) {
            tokenService.clearToken(RequestSource.getSource(SYSTEM_LOGIN_HANDLER_VALUE), dto.getId());
        }
        return flag;
    }

    @Override
    public void checkUpdateParams(SysUserEditDTO d) throws HttpParamsException {
        Long userId = Optional.of(d.getId())
                .orElseThrow(() -> Result.buildError(HttpParamsException.class, "id为空"));
        if (ObjectUtils.isNotEmpty(d.getRoleId())) {
            checkRole(d.getRoleId());
        }
        LambdaQueryWrapper<SysUserEntity> userNameWrapper = Wrappers.lambdaQuery(SysUserEntity.class)
                .eq(SysUserEntity::getUserName, d.getUserName())
                .select(SysUserEntity::getId);
        Optional.of(sysUserMapper.selectOne(userNameWrapper))
                .ifPresent(sysUserEntity -> Optional.of(sysUserEntity.getId().equals(userId))
                        .orElseThrow(() -> Result.buildError(HttpParamsException.class, "用户名已存在")));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean remove(Long id) {
        Boolean flag = sysUserMapper.deleteById(id) > 0;
        Optional.of(flag)
                .ifPresent(f -> tokenService.clearToken(RequestSource.getSource(SYSTEM_LOGIN_HANDLER_VALUE), id));
        return flag;
    }

    private void checkRole(Long roleId) {
        SysRoleEntity roleEntity = sysRoleMapper.selectById(roleId);
        if (!roleEntity.getStatus().equals(StatusEnum.OK)) {
            Result.buildError(HttpParamsException.class, "角色不存在或角色停用");
        }



//        Optional.of(sysRoleMapper.selectById(roleId))
//                .filter(sysRoleEntity -> sysRoleEntity.getStatus().equals(StatusEnum.OK))
//                .orElseThrow(() -> Result.buildError(HttpParamsException.class, "角色不存在或角色停用"));
    }
}
