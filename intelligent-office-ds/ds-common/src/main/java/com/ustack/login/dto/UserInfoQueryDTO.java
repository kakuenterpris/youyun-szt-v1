package com.ustack.login.dto;

import lombok.Data;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: UserInfoQueryDTO
 * @Date: 2023-02-15 11:01
 */
@Data
public class UserInfoQueryDTO {

    /**
     * guid
     */
    private String guid;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户账号
     */
    private String account;

    /**
     * 用户编号
     */
    private String userNum;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户手机号
     */
    private String userPhone;

    /**
     * 用户邮箱
     */
    private String userEmail;


}
