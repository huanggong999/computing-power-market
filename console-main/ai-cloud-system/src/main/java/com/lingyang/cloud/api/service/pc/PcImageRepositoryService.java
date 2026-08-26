package com.lingyang.cloud.api.service.pc;

import com.lingyang.cloud.api.model.dto.PcImageOciOverviewDTO;
import com.lingyang.cloud.api.model.dto.PcImageOciOverviewPageDTO;
import com.lingyang.cloud.api.model.dto.PcImageRepositoryDTO;
import com.lingyang.cloud.api.model.query.PcImageOciOverviewQuery;
import com.lingyang.cloud.api.model.vo.PcImageRepositoryDeleteVO;
import com.lingyang.cloud.api.model.vo.PcImageRepositoryVO;
import com.lingyang.cloud.entity.SysCustomerImageRepositoryEntity;
import com.lingyang.common.core.model.result.Result;
import com.volcengine.cr.model.DeleteTagsResponse;

import java.util.List;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/17 15:12
 */
public interface PcImageRepositoryService {

    /**
     * 查询镜像仓库列表
     * vo
     * @return
     */
    List<SysCustomerImageRepositoryEntity> list(PcImageRepositoryVO vo);

    /**
     * 获取镜像仓库详情
     * @return
     */
    PcImageRepositoryDTO detail(String instanceName);

    /**
     * 设置镜像仓库密码
     * @param vo
     */
    Boolean setPassword(PcImageRepositoryVO vo);

    /**
     * 获取镜像仓库OciOverview
     * @param instanceName
     * @return
     */
    PcImageOciOverviewDTO ociOverview(String instanceName);

    /**
     * 获取镜像仓库OciOverviewPage
     * @param query
     * @return
     */
    Result<List<PcImageOciOverviewPageDTO>> page(PcImageOciOverviewQuery query);

    String getUsername();

    DeleteTagsResponse deleteImage(PcImageRepositoryDeleteVO vo);

    Boolean imageNameExist(String imageName);
}
