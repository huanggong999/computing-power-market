package com.lingyang.cloud.api.service.pc;

import com.lingyang.cloud.api.model.dto.PcContainerDetailsDTO;
import com.lingyang.cloud.api.model.dto.PcContainerListDTO;
import com.lingyang.cloud.api.model.dto.PcContainerOverviewDTO;
import com.lingyang.cloud.api.model.vo.PcContainerImageVO;
import com.lingyang.cloud.api.model.vo.PcContainerListVO;
import com.lingyang.cloud.entity.SysImageEntity;

import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/19 18:06
 */
public interface PcContainerService {

    /**
     * 删除容器
     * @param id
     * @return
     */
    Boolean deleteContainer(Long id);

    /**
     * 获取容器概览
     * @list
     */
    PcContainerOverviewDTO getContainerOverview();

    List<PcContainerListDTO> list(PcContainerListVO vo);

    /**
     * 获取容器详情
     * @param id
     * @return
     */
    PcContainerDetailsDTO details(Long id);

    List<SysImageEntity> getImageVersion(PcContainerImageVO vo);
}
