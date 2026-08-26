package com.lingyang.cloud.api.service.system;

import com.lingyang.cloud.entity.SysMenuEntity;
import com.lingyang.cloud.model.query.menu.SysMenuQueryDTO;
import com.lingyang.cloud.model.vo.menu.SysMenuListVO;
import com.lingyang.common.web.service.BaseService;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/21 15:03
 */
public interface SystemMenuService extends BaseService<SysMenuQueryDTO, SysMenuEntity, SysMenuListVO, SysMenuListVO> {
}
