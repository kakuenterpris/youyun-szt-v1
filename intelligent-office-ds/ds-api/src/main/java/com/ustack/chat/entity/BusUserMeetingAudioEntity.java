package com.ustack.chat.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 用户会议纪要-语音文件表
 *
 * @TableName bus_user_meeting_audio
 */
@TableName(value = "bus_user_meeting_audio")
@Data
public class BusUserMeetingAudioEntity implements Serializable {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 文件ID
     */
    @TableField(value = "file_id")
    private String fileId;

    /**
     * 文件原名
     */
    @TableField(value = "file_origin_name")
    private String fileOriginName;

    /**
     * 是否转写成功
     */
    @TableField(value = "is_trans")
    private Integer isTrans;

    /**
     * 文件大小
     */
    @TableField(value = "size")
    private String size;

    /**
     * 排序
     */
    @TableField(value = "sort")
    private Integer sort;

    /**
     * 文件类型：wav、mp3,wav,pcm,aac,opus,flac,ogg,m4a,amr
     */
    @TableField(value = "file_type")
    private String fileType;

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
}