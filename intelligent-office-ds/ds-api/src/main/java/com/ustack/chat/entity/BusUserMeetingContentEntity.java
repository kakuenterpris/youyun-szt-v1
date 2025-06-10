package com.ustack.chat.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.ustack.global.common.entity.BaseEntity;
import lombok.Data;

/**
 * 用户会议纪要-语音内容表
 *
 * @TableName bus_user_meeting_content
 */
@TableName(value = "bus_user_meeting_content")
@Data
public class BusUserMeetingContentEntity implements Serializable {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "audio_id")
    private Long audioId;

    /**
     *
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 文件下载id
     */
    @TableField(value = "file_id")
    private String fileId;

    /**
     * 文件内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 真实时长-毫秒
     */
    @TableField(value = "real_duration")
    private Long realDuration;

    @TableField(exist = false)
    private String realDurationString;
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
     * 逻辑删除字段1 表示删除，0 表示未删除
     */
    @TableField(value = "is_deleted")
    @TableLogic
    private Integer isDeleted;

    /**
     *
     */
    @TableField(value = "guid", fill = FieldFill.INSERT)
    private String guid;

    /**
     * 讯飞语音识别订单ID
     */
    @TableField(value = "order_id", fill = FieldFill.INSERT)
    private String orderId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public BusUserMeetingContentEntity() {
    }

    public BusUserMeetingContentEntity(String userId, String fileId, String content, Long audioId) {
        this.userId = userId;
        this.fileId = fileId;
        this.content = content;
        this.audioId = audioId;
    }

    public BusUserMeetingContentEntity(Long id, String content) {
        this.id = id;
        this.content = content;
    }
}