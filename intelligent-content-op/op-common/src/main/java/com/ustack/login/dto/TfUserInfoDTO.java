package com.ustack.login.dto;

import lombok.Data;

/**
 * @Description: 用户信息
 * @author：linxin
 * @ClassName: UserInfoDTO
 * @Date: 2023-01-31 13:58
 */
@Data
public class TfUserInfoDTO {

    /**
     * ID / 岗位id
     */
    private String id;

    /**
     * 编号
     */
    private String workcode;

    /**
     * 登录名
     */
    private String loginid;

    /**
     * MD5加密的OA的登陆账户名
     */
    private String encryptLoginId;

    /**
     * 用户名称
     */
    private String lastname;

    /**
     * 部门id
     */
    private String departmentid;

    /**
     * 部门编码
     */
    private String departmentcode;

    /**
     * 部门名称
     */
    private String departmentname;

    /**
     * 岗位id
     */
    private String jobtitle;

    /**
     * 岗位名称
     */
    private String jobtitlename;

    /**
     * 用户手机号
     */
    private String mobile;

    /**
     * 电话号
     */
    private String telephone;

    /**
     * 用户邮箱
     */
    private String email;
}
