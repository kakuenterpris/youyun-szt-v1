package com.thtf.global.common.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

/**
 * @Description: 用户信息
 * @author：linxin
 * @ClassName: UserInfoDTO
 * @Date: 2023-01-31 13:58
 */
@Data
public class BusUserInfoDTO {

    /**
     * 自增ID
     */
    private Integer id;

    /**
     * 同方OA的人员id
     */
    private String userId;

    /**
     * 同方OA的人员编号
     */
    private String userNum;

    /**
     * 同方OA登陆账户名
     */
    private String loginId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 同方部门编码
     */
    private String depCode;

    /**
     * 部门编码-同方部门id
     */
    private String depNum;

    /**
     * 部门名称
     */
    private String depName;

    /**
     * 岗位编码
     */
    private String postNum;

    /**
     * 岗位名称
     */
    private String post;

    /**
     * 用户手机号
     */
    private String mobilePhone;

    /**
     * 电话号
     */
    private String phone;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 特殊权限
     */
    private String specialAuth;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 上次登录成功时间
     */
    private Date lastLoginTime;
}
