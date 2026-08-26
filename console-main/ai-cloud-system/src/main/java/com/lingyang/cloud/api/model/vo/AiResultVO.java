package com.lingyang.cloud.api.model.vo;

import lombok.Data;

@Data
public class AiResultVO {

    private Long id;

    private Boolean finish;

    private String userValue;

    private String aiValue;

}
