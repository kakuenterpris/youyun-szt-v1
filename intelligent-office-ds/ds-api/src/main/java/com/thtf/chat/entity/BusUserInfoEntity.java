package com.thtf.chat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 人员信息表
 * @TableName bus_user_info
 */
@TableName(value ="bus_user_info")
@Data
public class BusUserInfoEntity {
    /**
     * 自增 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 同方OA的人员id
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 同方OA的人员编号
     */
    @TableField(value = "user_num")
    private String userNum;

    /**
     * 同方OA登陆账户名
     */
    @TableField(value = "login_id")
    private String loginId;

    /**
     * MD5加密的同方OA的登陆账户名
     */
    @TableField(value = "encrypt_login_id")
    private String encryptLoginId;

    /**
     * 人员姓名
     */
    @TableField(value = "user_name")
    private String userName;

    /**
     * 用户密码
     */
    @TableField(value = "password")
    private String password;
    /**
     * 同方部门编码
     */
    @TableField(value = "dep_code")
    private String depCode;

    /**
     * 部门编码-同方部门id
     */
    @TableField(value = "dep_num")
    private String depNum;

    /**
     * 部门名称
     */
    @TableField(value = "dep_name")
    private String depName;

    /**
     * 岗位编码
     */
    @TableField(value = "post_num")
    private String postNum;

    /**
     * 岗位名称
     */
    @TableField(value = "post")
    private String post;

    /**
     * 手机号
     */
    @TableField(value = "mobile_phone")
    private String mobilePhone;

    /**
     * 电话号
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField(value = "email")
    private String email;

    /**
     * 特殊权限
     */
    @TableField(value = "special_auth")
    private String specialAuth;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 上次登录成功时间
     */
    @TableField(value = "last_log_in_time")
    private Date lastLogInTime;

    /**
     * 删除标志 (0-未删除, 1-已删除)
     */
    @TableField(value = "is_deleted")
    private Boolean isDeleted;

    @Override
    public String toString() {
//        返回json格式的字符串

        return "{"
                + "\"id\":" + (id != null ? id : "null") + ","
                + "\"userId\":\"" + (userId != null ? userId.replace("\"", "\\\"") : "") + "\","
                + "\"userNum\":\"" + (userNum != null ? userNum.replace("\"", "\\\"") : "") + "\","
                + "\"loginId\":\"" + (loginId != null ? loginId.replace("\"", "\\\"") : "") + "\","
                + "\"userName\":\"" + (userName != null ? userName.replace("\"", "\\\"") : "") + "\","
                + "\"depCode\":\"" + (depCode != null ? depCode.replace("\"", "\\\"") : "") + "\","
                + "\"depName\":\"" + (depName != null ? depName.replace("\"", "\\\"") : "") + "\""
                + "}";
    }
}