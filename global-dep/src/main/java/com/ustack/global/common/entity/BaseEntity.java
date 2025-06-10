package com.ustack.global.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Description: 数据库表基类
 * @author：linxin
 * @ClassName: BaseEntity
 * @Date: 2023-12-20 13:57
 */
@Data
public class BaseEntity {


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     *
     */
    @TableField(value = "guid", fill = FieldFill.INSERT)
    private String guid;

    /**
     * 创建人
     */
    @TableField(value = "create_user", fill = FieldFill.INSERT)
    private String createUser;

    /**
     * 创建人ID
     */
    @TableField(value = "create_user_id", fill = FieldFill.INSERT)
    private String createUserId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 修改人
     */
    @TableField(value = "update_user", fill = FieldFill.INSERT_UPDATE)
    private String updateUser;

    /**
     * 修改人ID
     */
    @TableField(value = "update_user_id", fill = FieldFill.INSERT_UPDATE)
    private String updateUserId;

    /**
     * 修改时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 乐观锁版本号
     */
    @Version
    @TableField(value = "version", fill = FieldFill.INSERT)
    private Long version;

    /**
     * 逻辑删除字段
     */
    @TableField(value = "is_deleted")
    @TableLogic
    private Boolean deleted;

}
