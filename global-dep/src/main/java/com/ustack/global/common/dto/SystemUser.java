package com.ustack.global.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 系统用户信息
 * @author：linxin
 * @ClassName: SystemUser
 * @Date: 2023-12-21 09:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SystemUser {

    private String id;

    private String userId;

    private String userName;

    private String loginId;

    /**
     * 用户编号
     */
    private String userNum;

    /**
     * 头像ID
     */
    private String avatar;

    /**
     * 当前登录token
     */
    private String token;

    /**
     * 岗位
     */
    private String post;

    /**
     * 岗位编号
     */
    private String postNum;

    /**
     * 直接上级部门名称
     */
    private String depName;

    /**
     * 直接上级部门编号
     */
    private String depNum;

    private String account;

    /**
     * 一级部门名称
     */
    private String firstDepName;

    /**
     * 一级部门编号
     */
    private String firstDepNum;

    /**
     * 单位名称：所属机构根节点名称
     */
    private String unitName;

    /**
     * 单位编号：所属机构根节点编号
     */
    private String unitNum;

    /**
     * 手机号
     */
    private String mobilePhone;

    /**
     * 电话（固定电话）
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 特殊权限
     */
    private String specialAuth;
}
