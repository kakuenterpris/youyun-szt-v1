package com.ustack.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ustack.global.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@TableName(value ="sys_oper_log")
@Data
@Schema(name = "日志记录表")
public class SysOperLogEntity  extends BaseEntity implements Serializable {

    /** 操作模块 */
    @TableField(value = "title")
    private String title;

    /** 业务类型（0其它 1新增 2修改 3删除）
     * "0=其它,1=新增,2=修改,3=删除,4=授权,5=导出,6=导入,7=强退,8=生成代码,9=清空数据"
     * */
    @TableField(value = "business_type")
    private Integer businessType;

    /** 业务类型数组 */
    private Integer[] businessTypes;

    /** 请求方法 */
    @TableField(value = "method")
    private String method;

    /** 请求方式 */
    @TableField(value = "request_method")
    private String requestMethod;

    /** 操作类别（0其它 1后台用户 2手机端用户） */
    @TableField(value = "operator_type")
    private Integer operatorType;

    /** 操作人员 */
    @TableField(value = "oper_name")
    private String operName;

    /** 部门名称 */
    @TableField(value = "dept_name")
    private String deptName;

    /** 请求url */
    @TableField(value = "oper_url")
    private String operUrl;

    /** 操作地址 */
    @TableField(value = "oper_ip")
    private String operIp;

    /** 操作地点 */
    @TableField(value = "oper_location")
    private String operLocation;

    /** 请求参数 */
    @TableField(value = "oper_param")
    private String operParam;

    /** 返回参数 */
    @TableField(value = "json_result")
    private String jsonResult;

    /** 操作状态（0正常 1异常） */
    @TableField(value = "status")
    private Integer status;

    /** 错误消息 */
    @TableField(value = "error_msg")
    private String errorMsg;

    /** 操作时间 */
    @TableField(value = "oper_time")
    private Date operTime;

    /** 消耗时间 */
    @TableField(value = "cost_time")
    private Long costTime;

}
