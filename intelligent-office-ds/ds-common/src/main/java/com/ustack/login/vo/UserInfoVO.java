package com.ustack.login.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Description: 用户信息
 * @author：linxin
 * @ClassName: UserInfoVO
 * @Date: 2023-01-31 13:48
 */
@Data
public class UserInfoVO {

    /**
     * 自增ID
     */
    private Integer id;

    /**
     * guid
     */
    private String guid;

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

    /**
     * 用户部门编码
     */
    private String userDepNum;

    /**
     * 用户部门名称
     */
    private String userDepName;

    /**
     * 上次登录成功时间
     */
    private Date lastLoginTime;

    /**
     * 是否锁定
     */
    private Boolean locked;



}
