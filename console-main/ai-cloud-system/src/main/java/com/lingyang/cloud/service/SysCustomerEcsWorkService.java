package com.lingyang.cloud.service;

import com.lingyang.cloud.model.dto.SysEcsWorkDetailDTO;
import com.lingyang.cloud.model.dto.SysEcsWorkPageDTO;
import com.lingyang.cloud.model.query.customer.SysCustomerEcsWorkQuery;
import com.lingyang.cloud.model.vo.customer.SysCustomerEcsWorkOperationVO;
import com.lingyang.cloud.model.vo.customer.SysCustomerEcsWorkVO;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;

/**
 * @author Administrator
 */
public interface SysCustomerEcsWorkService {

    PageResult<SysEcsWorkPageDTO> getPage(PageQuery<SysCustomerEcsWorkQuery> build);

    SysEcsWorkDetailDTO getDetail(Long id);

    Boolean openWork(SysCustomerEcsWorkVO vo);

    Boolean operation(SysCustomerEcsWorkOperationVO vo);
}
