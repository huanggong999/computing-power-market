package com.lingyang.cloud.api.service.system;

import com.lingyang.cloud.model.edit.role.SysRoleEditDTO;
import com.lingyang.cloud.model.query.role.SysRoleQueryDTO;
import com.lingyang.cloud.model.vo.role.SysRoleDetailVO;
import com.lingyang.cloud.model.vo.role.SysRoleListVO;
import com.lingyang.common.web.service.BaseService;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/21 15:00
 */
public interface SystemRoleService  extends BaseService<SysRoleQueryDTO, SysRoleEditDTO, SysRoleListVO, SysRoleDetailVO> {
}
