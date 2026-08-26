package com.lingyang.cloud.service;

import com.lingyang.cloud.api.model.vo.PcIsEmailExistVO;
import com.lingyang.cloud.api.model.vo.PcUpdateAutoRenewVO;
import com.lingyang.cloud.api.model.vo.PcUpdateCustomerNameVO;
import com.lingyang.cloud.entity.SysOrderEntity;
import com.lingyang.cloud.entity.SysOrderSourceEntity;
import com.lingyang.cloud.model.dto.SysEcsWorkPageDTO;
import com.lingyang.cloud.model.edit.order.SysOrderCreateDTO;
import com.lingyang.cloud.model.query.home.SysExtendOrderQuery;
import com.lingyang.cloud.model.query.order.SysOrderQuery;
import com.lingyang.cloud.model.vo.order.ExtendOrderVO;
import com.lingyang.cloud.model.vo.order.OrderCreateVO;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;

import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/12 17:30
 */
public interface SysOrderService {

    /**
     * 创建订单
     * @param createDTO 订单创建信息
     * @return 订单号
     */
    OrderCreateVO createOrder(SysOrderCreateDTO createDTO);

    /**
     * 构建订单信息
     * @param createDTO 订单创建信息
     * @return 订单信息
     */
    SysOrderEntity buildOrderInfo(SysOrderCreateDTO createDTO);

    void cancelOrder(Long orderId);

    PageResult<SysOrderEntity> getPage(PageQuery<SysOrderQuery> query);

    PageResult<SysOrderSourceEntity> getOrderSourceList(PageQuery<Long> build);

    void paySuccess(SysOrderEntity sysOrderEntity, String onlinePaySerialNumber);

    SysOrderEntity getOrderDetailByOrderNo(String orderNo);

    Result<PageResult<ExtendOrderVO>> getExtendPage(PageQuery<SysExtendOrderQuery> build);

    SysEcsWorkPageDTO getOrderDetail(String orderNo);

    SysOrderEntity buildAGOrderInfo(SysOrderCreateDTO createDTO);

    OrderCreateVO createAGOrder(SysOrderCreateDTO createDTO);

    Boolean updateAgiCustomerName(PcUpdateCustomerNameVO vo);

    Boolean updateAgiIsAutoRenew(PcUpdateAutoRenewVO vo);

    List<String> isEmailExist(PcIsEmailExistVO vo);

    PageResult<SysOrderEntity> getAGICOrderPage(PageQuery<SysOrderQuery> build);

    String getAlipayForm(String orderNo);

    Result<OrderCreateVO> getWechatPayQrCode(String orderNo);
}
