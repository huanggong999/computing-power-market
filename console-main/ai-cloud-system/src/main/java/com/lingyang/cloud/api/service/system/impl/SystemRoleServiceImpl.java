package com.lingyang.cloud.api.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.common.login.model.SysRoleInfo;
import com.lingyang.cloud.entity.SysRoleEntity;
import com.lingyang.cloud.entity.SysRoleMenuEntity;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.cloud.mapper.SysRoleMapper;
import com.lingyang.cloud.mapper.SysRoleMenuMapper;
import com.lingyang.cloud.model.edit.role.SysRoleEditDTO;
import com.lingyang.cloud.model.query.role.SysRoleQueryDTO;
import com.lingyang.cloud.model.vo.role.SysRoleDetailVO;
import com.lingyang.cloud.model.vo.role.SysRoleListVO;
import com.lingyang.cloud.api.service.system.SystemRoleService;
import com.lingyang.common.core.exception.http.HttpParamsException;
import com.lingyang.common.core.exception.http.HttpServiceException;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.BeanUtils;
import com.lingyang.common.core.utils.Optional;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/21 15:00
 */
@Service
public class SystemRoleServiceImpl implements SystemRoleService {
    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public PageResult<SysRoleListVO> getPage(PageQuery<SysRoleQueryDTO> page) {
        page.startPage();
        return PageResult.of(getList(page.getQuery()));
    }

    @Override
    public List<SysRoleListVO> getList(SysRoleQueryDTO queryDTO) {
        LambdaQueryWrapper<SysRoleEntity> roleWrapper = Wrappers.lambdaQuery();
        return BeanUtils.copyList(sysRoleMapper.selectList(roleWrapper), SysRoleListVO.class);
    }

    @Override
    public SysRoleDetailVO getDetail(Long id) {
        SysRoleDetailVO sysRoleDetail = Optional.of(BeanUtils.copyBean(sysRoleMapper.selectById(id), SysRoleDetailVO.class))
                .orElseThrow(() -> Result.buildError(HttpServiceException.class, "角色不存在"));
        LambdaQueryWrapper<SysRoleMenuEntity> roleMenuWrapper = Wrappers.lambdaQuery(SysRoleMenuEntity.class)
                .eq(SysRoleMenuEntity::getRoleId, id);
        Optional.of(sysRoleMenuMapper.selectList(roleMenuWrapper))
                .ifPresent(sysRoleMenuList -> sysRoleDetail.setMenuIds(sysRoleMenuList.stream().map(SysRoleMenuEntity::getMenuId).toList()));
        return sysRoleDetail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean save(SysRoleEditDTO dto) {
        // 新增角色
        dto.setStatus(Optional.of(dto.getStatus()).orElse(StatusEnum.OK));
        SysRoleEntity roleEntity = BeanUtils.copyBean(dto, SysRoleEntity.class);
        boolean flag = sysRoleMapper.insert(roleEntity) > 0;
        final AtomicBoolean atomicFlag = new AtomicBoolean(flag);
        dto.setId(roleEntity.getId());
        Optional.of(flag)
                .ifPresent(o -> atomicFlag.set(resetRoleMenu(dto)));
        return atomicFlag.get();
    }

    @Override
    public void checkSaveParams(SysRoleEditDTO d) throws HttpParamsException {
        d.setId(null);
        LambdaQueryWrapper<SysRoleEntity> roleWrapper = Wrappers.lambdaQuery(SysRoleEntity.class)
                .eq(SysRoleEntity::getRoleName, d.getRoleName())
                .select(SysRoleEntity::getId);

        Optional.of(sysRoleMapper.selectCount(roleWrapper))
                .ifPresent(count -> Optional.of(count == 0)
                        .orElseThrow(() -> Result.buildError(HttpParamsException.class, "角色名称已存在"))
                );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(SysRoleEditDTO dto) {
        boolean flag = sysRoleMapper.updateById(BeanUtils.copyBean(dto, SysRoleEntity.class)) > 0;
        AtomicBoolean atomicFlag = new AtomicBoolean(flag);
        Optional.of(flag)
                .ifPresent(o -> atomicFlag.set(resetRoleMenu(dto)));
        return atomicFlag.get();
    }

    @Override
    public void checkUpdateParams(SysRoleEditDTO d) throws HttpParamsException {
        Long id = Optional.of(d.getId())
                .orElseThrow(() -> Result.buildError(HttpParamsException.class, "id 不存在"));
        LambdaQueryWrapper<SysRoleEntity> roleWrapper = Wrappers.lambdaQuery(SysRoleEntity.class)
                .eq(SysRoleEntity::getRoleName, d.getRoleName());
        Optional.of(sysRoleMapper.selectOne(roleWrapper))
                .flatMap(sysRoleEntity -> BeanUtils.copyBean(sysRoleEntity, SysRoleInfo.class))
                .ifPresent(sysRoleInfo -> {
                            if (sysRoleInfo.isAdmin()) {
                                Result.throwsError(HttpParamsException.class, "超管角色无法编辑");
                            }
                            Optional.of(sysRoleInfo.getId().equals(id))
                                    .orElseThrow(() -> Result.buildError(HttpParamsException.class, "角色名已经存在"));
                        }
                );
    }

    @Override
    public void checkRemove(Long id) throws HttpParamsException {
        id = Optional.of(id)
                .orElseThrow(() -> Result.buildError(HttpParamsException.class, "id 不存在"));
        Optional.of(sysRoleMapper.selectById(id))
                .flatMap(sysRoleEntity -> BeanUtils.copyBean(sysRoleEntity, SysRoleInfo.class))
                .filter(sysRoleInfo -> {
                    if (sysRoleInfo.isAdmin()) {
                        Result.throwsError(HttpParamsException.class, "超管账号无法删除！");
                        return false;
                    }
                    return true;
                })
                .orElseThrow(() -> Result.buildError(HttpParamsException.class, "角色不存在"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean remove(Long id) {
        boolean flag = sysRoleMapper.deleteById(id) > 0;
        final AtomicBoolean atomicFlag = new AtomicBoolean(flag);
        Optional.of(flag)
                .ifPresent(o -> atomicFlag.set(
                                sysRoleMenuMapper.delete(Wrappers.lambdaQuery(SysRoleMenuEntity.class).eq(SysRoleMenuEntity::getRoleId, id)) > 0
                        )
                );
        return atomicFlag.get();
    }


    private boolean resetRoleMenu(SysRoleEditDTO dto) {
        Long roleId = dto.getId();
        // 删除原有
        sysRoleMenuMapper.delete(Wrappers.lambdaQuery(SysRoleMenuEntity.class)
                .eq(SysRoleMenuEntity::getRoleId, roleId));
        // 新增
        List<Long> menuIds = dto.getMenuIds();
        List<SysRoleMenuEntity> roleMenuEntityList = new ArrayList<>(menuIds.size());
        menuIds.forEach(menuId -> {
            SysRoleMenuEntity roleMenuEntity = new SysRoleMenuEntity();
            roleMenuEntity.setRoleId(roleId);
            roleMenuEntity.setMenuId(menuId);
            roleMenuEntityList.add(roleMenuEntity);
        });
        return sysRoleMenuMapper.batchInsert(roleMenuEntityList);
    }
}
