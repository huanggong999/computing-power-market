package com.lingyang.cloud.model.vo.pc;

import lombok.Data;

import java.util.List;

@Data
public class GpuMirrorItemVO {
    private String id;
    private String name;
    private String type;
    private String icon;
    private String description;
    private String baseImage;
    private String imageAddress;
    private List<String> supportedGpuModels;
}
