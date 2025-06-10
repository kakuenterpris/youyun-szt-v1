package com.ustack.access.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

@Data
public class UserInfoDto {
    /**
     * 自增 ID
     */
    private Integer id;

    /**
     * OA的人员id
     */
    private String userId;

    /**
     * OA的人员编号
     */
    private String userNum;


    private String password;
    /**
     * OA登陆账户名
     */
    private String loginId;

    /**
     * MD5加密的OA的登陆账户名
     */
    private String encryptLoginId;

    /**
     * 人员姓名
     */
    private String userName;


    /**
     * 部门编码
     */
    private String depCode;

    /**
     * 部门编码-部门id
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
     * 手机号
     */
    private String mobilePhone;

    /**
     * 电话号
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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 上次登录成功时间
     */
    private Date lastLogInTime;

    /**
     * 删除标志 (0-未删除, 1-已删除)
     */
    private Boolean isDeleted;

//    角色
    private String roleIds;

    /**
     * 人员密级
     */
    private String secretLevel;

    /**
     * 是否解锁
     */
    private Boolean unlock;

}
