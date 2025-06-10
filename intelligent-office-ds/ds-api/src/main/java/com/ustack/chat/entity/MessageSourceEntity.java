package com.ustack.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 *
 * @TableName message_source
 */
@TableName(value ="message_source")
@Data
public class MessageSourceEntity {
    /**
     * 自增ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 会话ID
     */
    @TableField(value = "conversation_id")
    private String conversationId;

    /**
     * 消息ID
     */
    @TableField(value = "message_id")
    private String messageId;

    /**
     * 来源知识库(个人知识库或者知网知识库)
     */
    @TableField(value = "source")
    private String source;

    /**
     * 文件ID
     */
    @TableField(value = "segment_id")
    private String segmentId;

    /**
     * 内容片段ID
     */
    @TableField(value = "document_id")
    private String documentId;

    /**
     * 关键词
     */
    @TableField(value = "keyword")
    private String keyword;

    /**
     * 内容片段
     */
    @TableField(value = "context")
    private String context;

    /**
     * 标题
     */
    @TableField(value = "title")
    private String title;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}