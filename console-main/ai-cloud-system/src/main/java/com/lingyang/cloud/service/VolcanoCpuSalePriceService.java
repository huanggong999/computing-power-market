package com.lingyang.cloud.service;

import com.alibaba.fastjson2.JSONObject;
import com.lingyang.cloud.model.edit.gpu.VolcanoCpuSalePriceEdit;
import com.lingyang.cloud.model.vo.system.VolcanoCpuSalePriceVO;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;

public interface VolcanoCpuSalePriceService {
    JSONObject refreshCatalog();
    JSONObject getStoredCatalog();
    JSONObject getDisplayPrices(String region, String instanceTypeId, String billingMode);
    JSONObject quote(JSONObject request);
    PageResult<VolcanoCpuSalePriceVO> getPricePage(PageQuery page, String region, String instanceType, String billingType, Integer status);
    VolcanoCpuSalePriceVO getById(Long id);
    void saveOrUpdate(VolcanoCpuSalePriceEdit edit);
    void updateStatus(Long id, Integer status);
    void restoreDefaultPrice(Long id);
}
