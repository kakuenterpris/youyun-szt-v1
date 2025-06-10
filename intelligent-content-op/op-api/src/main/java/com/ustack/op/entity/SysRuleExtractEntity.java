package com.ustack.op.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 知识规则提取配置
 * @TableName SYS_RULE_EXTRACT
 */
@TableName(value ="SYS_RULE_EXTRACT")
@Data
public class SysRuleExtractEntity implements Serializable {
    /**
     * id
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    @TableField(value = "NAME")
    private String name;

    /**
     * 编码
     */
    @TableField(value = "CODE")
    private String code;

    /**
     * 创建人
     */
    @TableField(value = "CREATOR")
    private String creator;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME")
    private Date createTime;

    /**
     * 标签数量
     */
    @TableField(exist = false)
    private Integer tagCount;


}