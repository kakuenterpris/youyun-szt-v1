package com.ustack.chat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

import lombok.Data;

/**
 * 文件表
 *
 * @TableName BUS_RESOURCE_FILE
 */
@TableName(value = "BUS_RESOURCE_FILE")
@Data
public class BusResourceFileEntity {
    /**
     *
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * guid
     */
    @TableField(value = "GUID")
    private String guid;

    /**
     * 文件名称
     */
    @TableField(value = "NAME")
    private String name;

    /**
     * 文件夹ID
     */
    @TableField(value = "FOLDER_ID")
    private Integer folderId;

    /**
     * 文件下载id
     */
    @TableField(value = "FILE_ID")
    private String fileId;

    /**
     * 资源大小（文件夹为0）、单位为字节
     */
    @TableField(value = "SIZE")
    private String size;

    /**
     * 文件类型：doc/docx/excel/txt/……
     */
    @TableField(value = "FILE_TYPE")
    private String fileType;

    /**
     * 排序
     */
    @TableField(value = "SORT")
    private Integer sort;

    /**
     * 向量化配置编码
     */
    @TableField(value = "EMBEDDING_CONFIG_CODE")
    private String embeddingConfigCode;

    /**
     * 向量化配置
     */
    @TableField(value = "EMBEDDING_CONFIG_NAME")
    private String embeddingConfigName;

    /**
     * 文件年份
     */
    @TableField(value = "FILE_YEAR")
    private Long fileYear;

    /**
     * 是否参与问答
     */
    @TableField(value = "JOIN_QUERY")
    private Boolean joinQuery;

    /**
     * md5预览文件id
     */
    @TableField(value = "PREVIEW_FILE_ID")
    private String previewFileId;

    /**
     * 创建人
     */
    @TableField(value = "CREATE_USER")
    private String createUser;

    /**
     * 创建人ID
     */
    @TableField(value = "CREATE_USER_ID")
    private String createUserId;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME")
    private Date createTime;

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

    /**
     * 乐观锁版本号
     */
    @TableField(value = "VERSION")
    private Integer version;

    /**
     * 逻辑删除字段1 表示删除，0 表示未删除
     */
    @TableField(value = "IS_DELETED")
    private Integer isDeleted;

    /**
     * 密级
     */
    @TableField(value = "LEVEL")
    private Integer level;

    /**
     *
     */
    @TableField(value = "SCOPE_RULE")
    private String scopeRule;

    /**
     *
     */
    @TableField(value = "YEAR")
    private String year;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        BusResourceFileEntity other = (BusResourceFileEntity) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getGuid() == null ? other.getGuid() == null : this.getGuid().equals(other.getGuid()))
                && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
                && (this.getFolderId() == null ? other.getFolderId() == null : this.getFolderId().equals(other.getFolderId()))
                && (this.getFileId() == null ? other.getFileId() == null : this.getFileId().equals(other.getFileId()))
                && (this.getSize() == null ? other.getSize() == null : this.getSize().equals(other.getSize()))
                && (this.getFileType() == null ? other.getFileType() == null : this.getFileType().equals(other.getFileType()))
                && (this.getSort() == null ? other.getSort() == null : this.getSort().equals(other.getSort()))
                && (this.getEmbeddingConfigCode() == null ? other.getEmbeddingConfigCode() == null : this.getEmbeddingConfigCode().equals(other.getEmbeddingConfigCode()))
                && (this.getEmbeddingConfigName() == null ? other.getEmbeddingConfigName() == null : this.getEmbeddingConfigName().equals(other.getEmbeddingConfigName()))
                && (this.getFileYear() == null ? other.getFileYear() == null : this.getFileYear().equals(other.getFileYear()))
                && (this.getJoinQuery() == null ? other.getJoinQuery() == null : this.getJoinQuery().equals(other.getJoinQuery()))
                && (this.getPreviewFileId() == null ? other.getPreviewFileId() == null : this.getPreviewFileId().equals(other.getPreviewFileId()))
                && (this.getCreateUser() == null ? other.getCreateUser() == null : this.getCreateUser().equals(other.getCreateUser()))
                && (this.getCreateUserId() == null ? other.getCreateUserId() == null : this.getCreateUserId().equals(other.getCreateUserId()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
                && (this.getUpdateUser() == null ? other.getUpdateUser() == null : this.getUpdateUser().equals(other.getUpdateUser()))
                && (this.getUpdateUserId() == null ? other.getUpdateUserId() == null : this.getUpdateUserId().equals(other.getUpdateUserId()))
                && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
                && (this.getVersion() == null ? other.getVersion() == null : this.getVersion().equals(other.getVersion()))
                && (this.getIsDeleted() == null ? other.getIsDeleted() == null : this.getIsDeleted().equals(other.getIsDeleted()))
                && (this.getLevel() == null ? other.getLevel() == null : this.getLevel().equals(other.getLevel()))
                && (this.getScopeRule() == null ? other.getScopeRule() == null : this.getScopeRule().equals(other.getScopeRule()))
                && (this.getYear() == null ? other.getYear() == null : this.getYear().equals(other.getYear()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getGuid() == null) ? 0 : getGuid().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getFolderId() == null) ? 0 : getFolderId().hashCode());
        result = prime * result + ((getFileId() == null) ? 0 : getFileId().hashCode());
        result = prime * result + ((getSize() == null) ? 0 : getSize().hashCode());
        result = prime * result + ((getFileType() == null) ? 0 : getFileType().hashCode());
        result = prime * result + ((getSort() == null) ? 0 : getSort().hashCode());
        result = prime * result + ((getEmbeddingConfigCode() == null) ? 0 : getEmbeddingConfigCode().hashCode());
        result = prime * result + ((getEmbeddingConfigName() == null) ? 0 : getEmbeddingConfigName().hashCode());
        result = prime * result + ((getFileYear() == null) ? 0 : getFileYear().hashCode());
        result = prime * result + ((getJoinQuery() == null) ? 0 : getJoinQuery().hashCode());
        result = prime * result + ((getPreviewFileId() == null) ? 0 : getPreviewFileId().hashCode());
        result = prime * result + ((getCreateUser() == null) ? 0 : getCreateUser().hashCode());
        result = prime * result + ((getCreateUserId() == null) ? 0 : getCreateUserId().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateUser() == null) ? 0 : getUpdateUser().hashCode());
        result = prime * result + ((getUpdateUserId() == null) ? 0 : getUpdateUserId().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        result = prime * result + ((getIsDeleted() == null) ? 0 : getIsDeleted().hashCode());
        result = prime * result + ((getLevel() == null) ? 0 : getLevel().hashCode());
        result = prime * result + ((getScopeRule() == null) ? 0 : getScopeRule().hashCode());
        result = prime * result + ((getYear() == null) ? 0 : getYear().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", guid=").append(guid);
        sb.append(", name=").append(name);
        sb.append(", folderId=").append(folderId);
        sb.append(", fileId=").append(fileId);
        sb.append(", size=").append(size);
        sb.append(", fileType=").append(fileType);
        sb.append(", sort=").append(sort);
        sb.append(", embeddingConfigCode=").append(embeddingConfigCode);
        sb.append(", embeddingConfigName=").append(embeddingConfigName);
        sb.append(", fileYear=").append(fileYear);
        sb.append(", joinQuery=").append(joinQuery);
        sb.append(", previewFileId=").append(previewFileId);
        sb.append(", createUser=").append(createUser);
        sb.append(", createUserId=").append(createUserId);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateUser=").append(updateUser);
        sb.append(", updateUserId=").append(updateUserId);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", version=").append(version);
        sb.append(", isDeleted=").append(isDeleted);
        sb.append(", level=").append(level);
        sb.append(", scopeRule=").append(scopeRule);
        sb.append(", year=").append(year);
        sb.append("]");
        return sb.toString();
    }
}