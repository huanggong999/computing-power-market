package com.lingyang.cloud.api.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysMenuEntity;
import com.lingyang.cloud.entity.SysRoleMenuEntity;
import com.lingyang.cloud.mapper.SysMenuMapper;
import com.lingyang.cloud.mapper.SysRoleMenuMapper;
import com.lingyang.cloud.model.query.menu.SysMenuQueryDTO;
import com.lingyang.cloud.model.vo.menu.SysMenuListVO;
import com.lingyang.cloud.api.service.system.SystemMenuService;
import com.lingyang.common.core.exception.http.HttpParamsException;
import com.lingyang.common.core.exception.http.HttpServiceException;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.BeanUtils;
import com.lingyang.common.core.utils.Optional;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/21 15:03
 */
@Service
public class SystemMenuServiceImpl implements SystemMenuService {
    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;
    @Resource
    private SysMenuMapper sysMenuMapper;

    @Override
    public List<SysMenuListVO> getList(SysMenuQueryDTO menuListQueryDTO) {
        List<Long> menuIdList = Optional.of(menuListQueryDTO.getRoleIdList())
                .flatMap(roleIdList -> sysRoleMenuMapper.selectList(Wrappers.lambdaQuery(SysRoleMenuEntity.class)
                        .in(SysRoleMenuEntity::getRoleId, roleIdList)))
                .flatMap(roleMenuList -> roleMenuList.stream().map(SysRoleMenuEntity::getMenuId).toList())
                .getValue();
        List<SysMenuEntity> sysMenuEntities = sysMenuMapper.selectList(Wrappers.lambdaQuery(SysMenuEntity.class)
                .in(ObjectUtils.isNotEmpty(menuIdList), SysMenuEntity::getId, menuIdList)
                .in(ObjectUtils.isNotEmpty(menuListQueryDTO.getMenuIdList()), SysMenuEntity::getId, menuListQueryDTO.getMenuIdList())
        );
        return BeanUtils.copyList(sysMenuEntities, SysMenuListVO.class);
    }


    @Override
    public SysMenuListVO getDetail(Long id) {
        SysMenuEntity sysMenuEntity = Optional.of(sysMenuMapper.selectById(id))
                .orElseThrow(() -> Result.buildError(HttpServiceException.class, "菜单不存在"));
        return BeanUtils.copyBean(sysMenuEntity, SysMenuListVO.class);
    }

    @Override
    public Boolean save(SysMenuEntity dto) {
        return sysMenuMapper.insert(dto) > 0;
    }

    @Override
    public void checkSaveParams(SysMenuEntity d) throws HttpParamsException {
        d.setId(null);
        LambdaQueryWrapper<SysMenuEntity> menuWrapper = Wrappers.lambdaQuery(SysMenuEntity.class)
                .eq(SysMenuEntity::getMenuName, d.getMenuName());
        Optional.of(sysMenuMapper.selectCount(menuWrapper))
                .ifPresent(count -> Optional.of(count == 0)
                        .orElseThrow(() -> Result.buildError(HttpParamsException.class, "菜单名称已存在"))
                );
    }

    @Override
    public Boolean update(SysMenuEntity dto) {
        return sysMenuMapper.updateById(dto) > 0;
    }

    @Override
    public void checkUpdateParams(SysMenuEntity d) throws HttpParamsException {
        Long id = Optional.of(d.getId())
                .orElseThrow( () -> Result.buildError(HttpParamsException.class, "id 不存在"));
        LambdaQueryWrapper<SysMenuEntity> menuWrapper = Wrappers.lambdaQuery(SysMenuEntity.class)
                .eq(SysMenuEntity::getMenuName, d.getMenuName())
                .select(SysMenuEntity::getId);
        Optional.of(sysMenuMapper.selectOne(menuWrapper))
                .ifPresent( menuEntity -> Optional.of(menuEntity.getId().equals(id))
                        .orElseThrow( () -> Result.buildError(HttpParamsException.class, "菜单名称已存在")));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean remove(Long id) {
        // 删除菜单
        boolean flag = sysMenuMapper.deleteById(id) > 0;
        if (flag) {
            // 删除角色关联菜单
            sysRoleMenuMapper.delete(Wrappers.lambdaQuery(SysRoleMenuEntity.class)
                    .eq(SysRoleMenuEntity::getMenuId, id));
        }
        return flag;
    }
}
