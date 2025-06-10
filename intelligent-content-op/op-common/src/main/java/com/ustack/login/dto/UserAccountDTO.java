package com.ustack.login.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: UserAccountDTO
 * @Date: 2023-01-31 13:54
 */
@Data
public class UserAccountDTO {

    /**
     * 自增ID
     */
    private Integer id;

    /**
     * guid
     */
    private String guid;

    /**
     * 所属用户ID
     */
    private Integer userId;

    /**
     * 所属用户guid
     */
    private String userGuid;

    /**
     * 账号
     */
    private String account;

    /**
     * 用户密码（两次md5）
     */
    private String password;

    private String salt;

    /**
     * 登录验证失败次数
     */
    private Integer failTimes;

    /**
     * 上次登录时间
     */
    private Date lastLoginTime;

    /**
     * 是否需要修改密码
     */
    private Boolean needUpdatePassword;

    /**
     * 上次修改密码时间
     */
    private Date lastUpdatePasswordTime;

    /**
     * 是否删除
     */
    private Boolean deleted;
}
