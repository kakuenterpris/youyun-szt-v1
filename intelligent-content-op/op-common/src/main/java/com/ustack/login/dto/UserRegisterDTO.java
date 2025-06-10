package com.ustack.login.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Description: 用户注册
 * @author：linxin
 * @ClassName: UserRegisterDTO
 * @Date: 2023-01-31 12:31
 */
@Data
public class UserRegisterDTO extends UserAccountAddDTO {

    /**
     * guid
     */
    private String guid;

    private String userName;

    private String userPhone;

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
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 上次登录成功时间
     */
    private Date lastLoginTime;

}
