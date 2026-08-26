package com.lingyang.cloud.config.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 微信session返回
 */
@Data
public class WxSessionResultVO implements Serializable {

    private Integer errcode;

    private String errmsg;

    private String openid;

    private String unionid;

    private String session_key;

}
