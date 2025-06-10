package com.ustack.chat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

import lombok.Data;

/**
 * 文件夹表
 *
 * @TableName BUS_RESOURCE_FOLDER
 */
@TableName(value = "BUS_RESOURCE_FOLDER")
@Data
public class BusResourceFolderEntity {
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
     * 文件夹名称
     */
    @TableField(value = "NAME")
    private String name;

    /**
     * 父级资源 ID（根目录为 0）
     */
    @TableField(value = "PARENT_ID")
    private Integer parentId;

    /**
     * 父级guid
     */
    @TableField(value = "PARENT_GUID")
    private String parentGuid;

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
     * 是否公开
     */
    @TableField(value = "OPEN_VIEW")
    private Boolean openView;

    /**
     * 是否能“创建/修改/删除”下级目录
     */
    @TableField(value = "CAN_ADD_SUB")
    private Boolean canAddSub;

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
     * 文件夹类型（5个人6部门7企业）
     */
    @TableField(value = "TYPE")
    private Integer type;

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
        BusResourceFolderEntity other = (BusResourceFolderEntity) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getGuid() == null ? other.getGuid() == null : this.getGuid().equals(other.getGuid()))
                && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
                && (this.getParentId() == null ? other.getParentId() == null : this.getParentId().equals(other.getParentId()))
                && (this.getParentGuid() == null ? other.getParentGuid() == null : this.getParentGuid().equals(other.getParentGuid()))
                && (this.getSort() == null ? other.getSort() == null : this.getSort().equals(other.getSort()))
                && (this.getEmbeddingConfigCode() == null ? other.getEmbeddingConfigCode() == null : this.getEmbeddingConfigCode().equals(other.getEmbeddingConfigCode()))
                && (this.getEmbeddingConfigName() == null ? other.getEmbeddingConfigName() == null : this.getEmbeddingConfigName().equals(other.getEmbeddingConfigName()))
                && (this.getOpenView() == null ? other.getOpenView() == null : this.getOpenView().equals(other.getOpenView()))
                && (this.getCanAddSub() == null ? other.getCanAddSub() == null : this.getCanAddSub().equals(other.getCanAddSub()))
                && (this.getCreateUser() == null ? other.getCreateUser() == null : this.getCreateUser().equals(other.getCreateUser()))
                && (this.getCreateUserId() == null ? other.getCreateUserId() == null : this.getCreateUserId().equals(other.getCreateUserId()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
                && (this.getUpdateUser() == null ? other.getUpdateUser() == null : this.getUpdateUser().equals(other.getUpdateUser()))
                && (this.getUpdateUserId() == null ? other.getUpdateUserId() == null : this.getUpdateUserId().equals(other.getUpdateUserId()))
                && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
                && (this.getVersion() == null ? other.getVersion() == null : this.getVersion().equals(other.getVersion()))
                && (this.getIsDeleted() == null ? other.getIsDeleted() == null : this.getIsDeleted().equals(other.getIsDeleted()))
                && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getGuid() == null) ? 0 : getGuid().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getParentId() == null) ? 0 : getParentId().hashCode());
        result = prime * result + ((getParentGuid() == null) ? 0 : getParentGuid().hashCode());
        result = prime * result + ((getSort() == null) ? 0 : getSort().hashCode());
        result = prime * result + ((getEmbeddingConfigCode() == null) ? 0 : getEmbeddingConfigCode().hashCode());
        result = prime * result + ((getEmbeddingConfigName() == null) ? 0 : getEmbeddingConfigName().hashCode());
        result = prime * result + ((getOpenView() == null) ? 0 : getOpenView().hashCode());
        result = prime * result + ((getCanAddSub() == null) ? 0 : getCanAddSub().hashCode());
        result = prime * result + ((getCreateUser() == null) ? 0 : getCreateUser().hashCode());
        result = prime * result + ((getCreateUserId() == null) ? 0 : getCreateUserId().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateUser() == null) ? 0 : getUpdateUser().hashCode());
        result = prime * result + ((getUpdateUserId() == null) ? 0 : getUpdateUserId().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        result = prime * result + ((getIsDeleted() == null) ? 0 : getIsDeleted().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
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
        sb.append(", parentId=").append(parentId);
        sb.append(", parentGuid=").append(parentGuid);
        sb.append(", sort=").append(sort);
        sb.append(", embeddingConfigCode=").append(embeddingConfigCode);
        sb.append(", embeddingConfigName=").append(embeddingConfigName);
        sb.append(", openView=").append(openView);
        sb.append(", canAddSub=").append(canAddSub);
        sb.append(", createUser=").append(createUser);
        sb.append(", createUserId=").append(createUserId);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateUser=").append(updateUser);
        sb.append(", updateUserId=").append(updateUserId);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", version=").append(version);
        sb.append(", isDeleted=").append(isDeleted);
        sb.append(", type=").append(type);
        sb.append("]");
        return sb.toString();
    }
}