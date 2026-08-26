package com.lingyang.cloud.enums.source;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.lingyang.cloud.model.vo.regions.SourceRegionVO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 资源地域枚举
 * @Author: 王小龙
 * @Date: 2024/11/1 14:39
 */
@AllArgsConstructor
@Getter
public enum SourceRegionsEnum {
    //cn-beijing
    CN_BEIJING("cn-beijing", "华北2（北京）"),
    //cn-shanghai
    CN_SHANGHAI("cn-shanghai", "华东2（上海）"),
    //cn-guangzhou
    CN_GUANGZHOU("cn-guangzhou", "华南（广州）"),
    //cn-shenzhen
    CN_SHENZHEN("cn-shenzhen", "华南（深圳）"),
    //cn-shaoguan
    CN_SHAOGUAN("cn-shaoguan", "华南（韶关）"),
    //cn-guiyang
    CN_GUIYANG("cn-guiyang", "贵州（贵阳）"),
    //cn-zhongwei
    CN_ZHONGWEI("cn-zhongwei", "宁夏（中卫）"),
    //cn-huhehaote
    CN_HUHEHAOTE("cn-huhehaote", "内蒙（呼和浩特）"),
    //cn-hongkong
    CN_HONGKONG("cn-hongkong", "中国（香港）"),
    //cn-taiwan
    CN_TAIWAN("cn-taiwan", "中国（台湾）"),
    //jpn-tokyo
    JPN_TOKYO("jpn-tokyo", "日本（东京）"),
    //vnm-henei
    VNM_HENEI("vnm-henei", "越南（河内）"),
    //usa-los-angeles
    USA_LOS_ANGELES("usa-los-angeles", "美国（洛杉矶）"),
    MALAYSIA_JOHOR("malaysia_johor", "马来西亚（柔佛）"),
    //thai-bangkok
//    //ap-southeast-1
//    AP_SOUTHEAST_1("ap-southeast-1", "亚太东南（柔佛）"),

    ;

    private final String id;

    @EnumValue
    private final String name;

    public static SourceRegionsEnum getById(String clientToken) {
        for (SourceRegionsEnum value : values()) {
            if (value.id.equals(clientToken)) {
                return value;
            }
        }
        return null;
    }
    public static SourceRegionsEnum getByName(String regionName) {
        for (SourceRegionsEnum value : values()) {
            if (value.name.equals(regionName)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return id + " = " + name;
    }

    public static List<SourceRegionVO> getSourceRegions() {
        SourceRegionsEnum[] values = values();
        List<SourceRegionVO> resultList = new ArrayList<>(values.length);
        for (SourceRegionsEnum value : values) {
            resultList.add(new SourceRegionVO(value, value.id, value.name));
        }
        return resultList;
    }
}
