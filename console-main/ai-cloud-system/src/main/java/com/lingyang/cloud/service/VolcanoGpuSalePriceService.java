package com.lingyang.cloud.service;

import com.alibaba.fastjson2.JSONObject;
import com.lingyang.cloud.entity.VolcanoGpuSalePriceEntity;
import com.lingyang.cloud.model.edit.gpu.VolcanoGpuSalePriceEdit;
import com.lingyang.cloud.model.vo.system.VolcanoGpuSalePriceVO;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;

import java.math.BigDecimal;

/**
 * 火山云 GPU 平台售价服务
 */
public interface VolcanoGpuSalePriceService {

    PageResult<VolcanoGpuSalePriceVO> getPricePage(PageQuery pageQuery, String regionCode,
            String instanceTypeId, String gpuModel, String billingType, Integer status);

    VolcanoGpuSalePriceVO getById(Long id);

    void saveOrUpdate(VolcanoGpuSalePriceEdit edit);

    void updateStatus(Long id, Integer status);

    void restoreDefaultPrice(Long id);

    VolcanoGpuSalePriceEntity resolvePrice(String regionCode, String instanceTypeId, String billingType);

    BigDecimal resolveSalePrice(String regionCode, String instanceTypeId, String billingType, BigDecimal upstreamPrice);

    JSONObject injectSalePrices(JSONObject catalog);

    JSONObject getDisplayCatalog(String billingType);

    JSONObject getStoredCatalog(String billingType);

    /**
     * 从火山云目录快照中读取 GPU 原始价格，不访问上游接口。
     * 返回 null 表示快照不存在、规格不存在或价格无效。
     */
    BigDecimal findSnapshotGpuPrice(String regionCode, String gpuModel, String gpuMemory,
            String instanceTypeId, String billingType);

    JSONObject refreshCatalog(String billingType);
}
