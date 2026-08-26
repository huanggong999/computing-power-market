package com.lingyang.cloud.api.service.system;

import com.lingyang.cloud.model.edit.user.SysUserEditDTO;
import com.lingyang.cloud.model.query.user.SysUserQueryDTO;
import com.lingyang.cloud.model.vo.user.SysUserDetailVO;
import com.lingyang.cloud.model.vo.user.SysUserListVO;
import com.lingyang.common.web.service.BaseService;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/21 15:10
 */
public interface SystemUserService extends BaseService<SysUserQueryDTO, SysUserEditDTO, SysUserListVO, SysUserDetailVO> {
}
