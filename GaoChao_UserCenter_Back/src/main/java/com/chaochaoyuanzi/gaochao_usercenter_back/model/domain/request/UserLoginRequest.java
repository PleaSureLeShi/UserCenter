package com.chaochaoyuanzi.gaochao_usercenter_back.model.domain.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 7469099363714555003L;
    private String userAccount;
    private String userPassword;
}
