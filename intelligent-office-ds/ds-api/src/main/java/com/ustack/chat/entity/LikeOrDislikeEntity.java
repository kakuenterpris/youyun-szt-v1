package com.ustack.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ustack.global.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


/**
 * @author zhoufei
 * 点赞点踩记录表
 */
@TableName(value ="like_or_dislike")
@Data
@Schema(name = "点赞点踩记录表")
public class LikeOrDislikeEntity extends BaseEntity implements Serializable {

    @TableField(value = "user_id")
    private String userId;

    @TableField(value = "conversation_id")
    private String conversationId;

    @TableField(value = "message_id")
    private String messageId;

    @TableField(value = "like_status")
    private Integer likeStatus;

    @TableField(value = "dict_id")
    private String dictId;

    @TableField(value = "suggestion")
    private String suggestion;

}
