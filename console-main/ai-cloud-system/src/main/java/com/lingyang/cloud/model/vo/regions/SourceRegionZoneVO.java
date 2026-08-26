package com.lingyang.cloud.model.vo.regions;

import com.lingyang.common.core.exception.http.HttpServiceException;
import com.lingyang.common.core.utils.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/1 17:15
 */
@Data
public class SourceRegionZoneVO {

    @Schema(description = "可用区域id")
    private String id;

    @Schema(description = "可用区域名称")
    private String name;

    public SourceRegionZoneVO(String id) {
        if (StringUtils.isEmpty(id)) {
            throw new HttpServiceException("可用区域为空");
        }
        this.id = id;
    }

    public String getName() {
        String[] split = id.split("-");
        return "可用区" + split[split.length - 1].toUpperCase();
    }
}
