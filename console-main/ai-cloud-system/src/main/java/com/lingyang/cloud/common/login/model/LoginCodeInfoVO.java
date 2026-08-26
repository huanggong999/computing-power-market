package com.lingyang.cloud.common.login.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginCodeInfoVO implements Serializable {

    private Boolean login;

    private Long id;

    private String username;


}
