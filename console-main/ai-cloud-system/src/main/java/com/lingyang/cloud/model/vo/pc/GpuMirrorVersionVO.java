package com.lingyang.cloud.model.vo.pc;

import lombok.Data;

import java.util.Map;

@Data
public class GpuMirrorVersionVO {
    private String id;
    private String mirrorId;
    private String version;
    private String cudaVersion;
    private String pythonVersion;
    private String description;
    private String image;
    private Map<String, String> frameworks;
}
