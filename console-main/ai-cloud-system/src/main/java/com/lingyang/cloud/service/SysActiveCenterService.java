package com.lingyang.cloud.service;

import com.lingyang.cloud.api.model.dto.PcVoucherListDTO;
import com.lingyang.cloud.entity.SysActiveCenterEntity;
import com.lingyang.cloud.entity.SysActiveRecordEntity;
import com.lingyang.cloud.model.dto.SysActiveCouponDTO;
import com.lingyang.cloud.model.dto.SysActiveDetailsDTO;
import com.lingyang.cloud.model.query.active.SysActiveCouponQuery;
import com.lingyang.cloud.model.query.active.SysActiveQuery;
import com.lingyang.cloud.model.query.active.SysActiveRecordQuery;
import com.lingyang.cloud.model.vo.active.SysActiveVO;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;

import java.util.List;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/16 15:58
 */
public interface SysActiveCenterService {

    /**
     * 分页查询
     * @param pageQuery
     * @return
     */
    Result<PageResult<SysActiveCenterEntity>> getPage(PageQuery<SysActiveQuery> pageQuery);

    /**
     * 新增活动
     * @param vo
     * @return
     */
    Result<Void> save(SysActiveVO vo);

    /**
     * 分页查询优惠券
     * @param pageQuery
     * @return
     */
    Result<PageResult<SysActiveCouponDTO>> getCoupons(PageQuery<SysActiveCouponQuery> pageQuery);

    /**
     *  获取活动详情
     * @param id
     * @return
     */
    Result<SysActiveDetailsDTO> getActiveDetails(Long id);

    /**
     * 更新活动
     * @param vo
     * @return
     */
    Result<Void> update(SysActiveVO vo);

    /**
     * 获取PC端活动详情
     * @param id
     * @return
     */
    Result<SysActiveDetailsDTO> getPcActiveDetails(Long id);

    /**
     * 分页查询后台活动记录
     * @param build
     * @return
     */
    Result<PageResult<SysActiveRecordEntity>> getActiveRecord(PageQuery<SysActiveRecordQuery> build);

    /**
     * 获取活动列表
     * @return
     */
    Result<List<SysActiveDetailsDTO>> getActiveList();

    Result<List<PcVoucherListDTO>> getCouponList();

    Result<Void> delete(Long id);
}
