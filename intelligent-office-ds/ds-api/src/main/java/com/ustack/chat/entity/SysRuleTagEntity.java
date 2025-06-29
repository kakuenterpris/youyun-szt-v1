package com.ustack.chat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @TableName SYS_RULE_TAG
 */
@TableName(value ="SYS_RULE_TAG")
@Data
public class SysRuleTagEntity implements Serializable {
    /**
     * id
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 标签编码
     */
    @TableField(value = "TAG_NAME")
    private String tagName;

    /**
     * 标签名称
     */
    @TableField(value = "TAG_CODE")
    private String tagCode;

    /**
     * 标签类型
     */
    @TableField(value = "TAG_TYPE")
    private String tagType;

    /**
     * 标签长度
     */
    @TableField(value = "TAG_LENGTH")
    private Integer tagLength;

    /**
     * 标签描述
     */
    @TableField(value = "TAG_REMARK")
    private String tagRemark;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME")
    private Date createTime;

    /**
     * 创建人
     */
    @TableField(value = "CREATOR")
    private String creator;

    /**
     * 规则提取id
     */
    @TableField(value = "RULE_EXTRACT_ID")
    private Long ruleExtractId;

    /**
     * 排序
     */
    @TableField(value = "SORT")
    private Integer sort;

    /**
     * 是否必填
     */
    @TableField(value = "MUST_FILL")
    private String mustFill;


    /**
     * 修改人
     */
    @TableField(value = "UPDATE_USER")
    private String updateUser;

    /**
     * 修改人ID
     */
    @TableField(value = "UPDATE_USER_ID")
    private String updateUserId;

    /**
     * 修改时间
     */
    @TableField(value = "UPDATE_TIME")
    private Date updateTime;


}