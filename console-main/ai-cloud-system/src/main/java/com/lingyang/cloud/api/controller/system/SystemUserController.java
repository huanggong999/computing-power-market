package com.lingyang.cloud.api.controller.system;

import com.lingyang.cloud.common.login.model.SysLoginUserInfo;
import com.lingyang.cloud.model.edit.user.SysUserEditDTO;
import com.lingyang.cloud.model.query.user.SysUserQueryDTO;
import com.lingyang.cloud.model.vo.user.SysUserDetailVO;
import com.lingyang.cloud.model.vo.user.SysUserListVO;
import com.lingyang.cloud.api.service.system.SystemUserService;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.web.controller.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/21 14:20
 */
@RestController
@Tag(name = "后台系统-用户相关")
@RequestMapping("/system/user")
public class SystemUserController  extends BaseController<SystemUserService, SysUserQueryDTO, SysUserEditDTO, SysUserListVO, SysUserDetailVO> {

    @GetMapping("/info")
    @Operation(summary = "获取用户信息")
    public Result<SysLoginUserInfo> getUserInfo(){
        return Result.success(SecurityContext.getUserInfo());
    }

    @Override
//    // @PreAuthorize("@ss.hasPermission('system:user:page')")
    public Result<PageResult<SysUserListVO>> page(SysUserQueryDTO sysUserQueryDTO) {
        return super.page(sysUserQueryDTO);
    }


    @Override
    // @PreAuthorize("@ss.hasPermission('system:user:save')")
    public Result<Void> save(@RequestBody @Valid SysUserEditDTO t) {
        return super.save(t);
    }

    @Override
    // @PreAuthorize("@ss.hasPermission('system:user:update')")
    public Result<Void> update(@RequestBody SysUserEditDTO t) {
        return super.update(t);
    }

    @Override
    // @PreAuthorize("@ss.hasPermission('system:user:remove')")
    public Result<Void> remove(Long id) {
        return super.remove(id);
    }
}
