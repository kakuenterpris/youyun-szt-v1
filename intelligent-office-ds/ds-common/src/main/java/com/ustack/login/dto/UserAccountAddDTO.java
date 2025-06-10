package com.ustack.login.dto;

import lombok.Data;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: UserAccountAddDTO
 * @Date: 2023-02-15 10:46
 */
@Data
public class UserAccountAddDTO {

    private String userId;

    /**
     * 账号
     */
    private String account;

    /**
     * 用户密码（两次md5）
     */
    private String password;

    /**
     * 重复密码
     */
    private String passwordRepeat;

}
