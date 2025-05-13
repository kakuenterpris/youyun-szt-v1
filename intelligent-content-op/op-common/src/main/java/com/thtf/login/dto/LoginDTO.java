package com.thtf.login.dto;

import lombok.Data;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: LoginDTO
 * @Date: 2023-01-31 10:36
 */
@Data
public class LoginDTO {

    private String account;

    private String password;

    private String verifyCode;

    private String uuid;

}
