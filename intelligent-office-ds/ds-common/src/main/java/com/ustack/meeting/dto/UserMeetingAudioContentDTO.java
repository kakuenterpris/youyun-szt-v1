package com.ustack.meeting.dto;

import com.baomidou.mybatisplus.annotation.*;
import com.ustack.global.common.entity.BaseEntity;
import com.ustack.global.common.validation.ValidGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

/**
 * Author：PingY
 * Package：com.ustack.dto
 * Project：intelligent-office-platform
 * Classname：UserMeetingAudioContentDTO
 * Date：2025/3/25  11:42
 * Description: 用户会议纪要语音文件DTO
 */

@Data
public class UserMeetingAudioContentDTO {

    private Long id; // 等同于audioId

    private Long contentId;

    private Long audioId;
    /**
     *
     */
    private String userId;

    /**
     * 文件下载id
     */
    @NotBlank(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "文件ID不为空")
    private String fileId;

    /**
     * 文件原名
     */
    @NotBlank(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "文件名称不为空")
    private String fileOriginName;

    /**
     * 是否转写成功
     */
    private Integer isTrans;

    /**
     * 真实时长-毫秒
     */
    private Long realDuration;

    /**
     * 真实时长-时间格式
     */
    private String realDurationString;
    /**
     * 资源大小（文件夹为0）、单位为字节
     */
    @NotBlank(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "文件大小不为空")
    private String size;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 文件类型
     */
    @NotBlank(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "文件类型不为空")
    private String fileType;
    /**
     * 文件内容
     */
    private String content;

    private String queryParam; // 文件名和内容检索参数

    private String fileIds; // 文件入参
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

}
