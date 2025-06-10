package com.ustack.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ustack.global.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@TableName(value ="sys_dept")
@Data
@Schema(name = "部门表")
public class SysDeptEntity extends BaseEntity implements Serializable {

    /** 部门ID */
    @TableField(value = "dept_id")
    private Long deptId;

    /** 父部门ID */
    @TableField(value = "parent_id")
    private Long parentId;

    /** 祖级列表 */
    @TableField(value = "ancestors")
    private String ancestors;

    /** 部门名称 */
    @TableField(value = "dept_name")
    private String deptName;

    /** 显示顺序 */
    @TableField(value = "order_num")
    private Integer orderNum;

    /** 负责人 */
    @TableField(value = "leader")
    private String leader;

    /** 联系电话 */
    @TableField(value = "phone")
    private String phone;

    /** 邮箱 */
    @TableField(value = "email")
    private String email;

    /** 部门状态:0正常,1停用 */
    @TableField(value = "status")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    @TableField(value = "del_flag")
    private String delFlag;

    /** 父部门名称 */
    @TableField(value = "parent_name")
    private String parentName;

    /** 子部门 */
    private List<SysDeptEntity> children = new ArrayList<SysDeptEntity>();
}
