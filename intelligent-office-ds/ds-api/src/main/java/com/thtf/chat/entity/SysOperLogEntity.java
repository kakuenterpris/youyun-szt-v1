package com.thtf.chat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.thtf.global.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@TableName(value ="sys_oper_log")
@Data
@Schema(name = "日志记录表")
public class SysOperLogEntity  extends BaseEntity implements Serializable {

    @TableField(value = "oper_name")
    private String operName;
    @TableField(value = "oper_ip")
    private String operIp;
    @TableField(value = "oper_dept")
    private String operDept;
    @TableField(value = "oper_type")
    private String operType;
    @TableField(value = "oper_method")
    private String operMethod;
    @TableField(value = "request_body")
    private String requestBody;
    @TableField(value = "params")
    private String params;
    @TableField(value = "status")
    private Integer status;
    @TableField(value = "response")
    private String response;
    @TableField(value = "error_msg")
    private String errorMsg;
    @TableField(value = "oper_time")
    private Date operTime;
    @TableField(value = "cost_time")
    private Long costTime;

}
