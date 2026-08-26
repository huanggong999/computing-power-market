package com.lingyang.cloud.service;

import com.lingyang.cloud.api.model.dto.PcRealNameDTO;
import com.lingyang.cloud.api.model.vo.PcRealNameVO;
import com.lingyang.cloud.entity.SysCustomerEntity;
import com.lingyang.cloud.enums.customer.SysTransactionType;
import com.lingyang.cloud.model.query.customer.SysCompanyQuery;
import com.lingyang.cloud.model.query.customer.SysCustomerQuery;
import com.lingyang.cloud.model.vo.customer.SysCustomerDiscountVO;
import com.lingyang.cloud.model.vo.customer.SysCustomerListVO;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/21 17:17
 */
public interface SysCustomerService {
    boolean updateById(SysCustomerEntity entity);

    boolean insert(SysCustomerEntity entity);

    SysCustomerEntity getByEntity(SysCustomerEntity entity);

    /**
     * 获取用户列表
     * @param pageQuery 分页查询条件
     * @return 分页数据
     */
    PageResult<SysCustomerListVO> getPage(PageQuery<SysCustomerQuery> pageQuery);

    /**
     * 修改客户资源折扣
     * @param discountRation 折扣比列
     */
    void updateCustomerDiscount(SysCustomerDiscountVO discountRation);

    void updateCustomerBalance(Long orderId, String orderNo, Long userId, BigDecimal balancePayAmount, SysTransactionType sysTransactionType);

    boolean updateByPhone(SysCustomerEntity entity);

    PageResult<SysCustomerEntity> getCompanyPage(PageQuery<SysCompanyQuery> build);

    PcRealNameDTO realNameVerify(PcRealNameVO vo);

    String getEidToken(PcRealNameVO vo);

    Result<Boolean> getEidResult( Long logId);

    Result<Boolean> remove(Long customerId);
}
