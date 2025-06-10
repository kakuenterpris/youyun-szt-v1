package com.ustack.login.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Description: 用户信息
 * @author：linxin
 * @ClassName: UserInfoDTO
 * @Date: 2023-01-31 13:58
 */
@Data
public class UserInfoDTO {

    /**
     * 自增ID
     */
    private Integer id;

    /**
     * guid
     */
    private String guid;
    /**
     * 用户头像
     */
    private String avatar;
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

    /**
     * 排序字段
     */
    private Integer orderBy;

    /**
     * 是否锁定
     */
    private Boolean locked;

    /**
     * 是否删除
     */
    private Boolean deleted;
}
