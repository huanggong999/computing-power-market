package com.lingyang.cloud.api.service.pc;

import com.lingyang.cloud.api.model.edit.PcInstancesStatusHandlerEdit;
import com.lingyang.cloud.api.model.query.PcConsoleEcsQuery;
import com.lingyang.cloud.api.model.vo.PcConsoleEcsDetailVO;
import com.lingyang.cloud.api.model.vo.PcConsoleEcsListVO;
import com.lingyang.cloud.api.model.vo.PcConsoleHomeVO;
import com.lingyang.cloud.api.model.vo.PcConsoleSourceInfoVO;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import com.lingyang.cloud.model.vo.applets.AppletsEcsWorkOperationVO;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/19 18:06
 */
public interface PcConsoleService {
    PcConsoleHomeVO getHome(Long userId);

    PcConsoleSourceInfoVO getSourceInfo(Long userId);

    PageResult<PcConsoleEcsListVO> getInstancePageList(PageQuery<PcConsoleEcsQuery> pageQuery);

    PcConsoleEcsDetailVO getInstanceDetail(String id);
//    CompletableFuture<PcConsoleEcsDetailVO> getInstanceDetailAsync(String id);
    /**
     * 处理实列状态
     * @param edit 实列列表
     * @param sourceRegionsEnum 地域
     */
    void handlerInstancesStatus(PcInstancesStatusHandlerEdit edit, SourceRegionsEnum sourceRegionsEnum);

    Boolean serverApply(AppletsEcsWorkOperationVO vo);

    void handlerCustomerBillSelfBuildHourCheckout(Long valueOf);
}
