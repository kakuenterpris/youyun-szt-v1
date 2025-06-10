package com.ustack.resource.dto;

import com.ustack.global.common.validation.ValidGroup;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 文件夹表
 * @author allm
 * @TableName bus_resource_folder
 */
@Data
public class BusResourceFolderDTO {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * guid
     */
    private String guid;

    /**
     * 文件夹名称
     */
    @NotBlank(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "名称不能为空")
    private String name;

    /**
     * 父级资源 ID（根目录为 0）
     */
    private Integer parentId;

    /**
     * 父级guid
     */
    private String parentGuid;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 向量化配置编码
     */
    private String embeddingConfigCode;

    /**
     * 向量化配置
     */
    private String embeddingConfigName;

    /**
     * 是否公开
     */
    private Boolean openView;

    /**
     * 是否能“创建/修改/删除”下级目录
     */
    private Boolean canAddSub;

    /**
     * 创建人
     */
    private String createUser;

    /**
     创建 *人ID
     */
    private String createUserId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 修改人ID
     */
    private String updateUserId;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 乐观锁版本号
     */
    private Integer version;

    /**
     * 成员列表
     */
    private List<BusResourceMemberDTO> memberList;

    /**
     * 文件夹类型
     */
    private Integer type;
}
