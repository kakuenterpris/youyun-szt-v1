package com.ustack.resource.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ustack.global.common.validation.ValidGroup;
import com.ustack.resource.vo.BaseTreeNodeVO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 常用语表 DTO
 */
@Data
public class BusResourceManageListDTO extends BaseTreeNodeVO {

    /**
     * 自增 ID
     */
    @NotNull(groups = {ValidGroup.Update.class}, message = "id不能为空")
    private Integer id;
    /**
     * 资源名称：文件或者文件夹
     */
    @NotBlank(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "名称不能为空")
    private String name;

    /**
     * 父级资源 ID（根目录为 0）
     */
    @NotNull(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "父级ID不能为空，顶层父级传0")
    private Integer parentId;

    /**
     * 父级资源 ID（根目录为 0）
     */
    private Integer folderId;

    /**
     * 资源类型：1-文件夹;2-文件
     */
    @NotNull(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "资源类型不能为空，1-文件夹；2-文件")
    private Integer resourceType;

    /**
     * 文件下载id、多值用英文逗号分隔
     */
    private String fileId;

    /**
     * 资源大小（文件夹为0）、单位为字节、多值用英文逗号分隔
     */
    private String size;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 文件类型：doc/docx/excel/txt/……、多值用英文逗号分隔
     */
    private String fileType;

    /**
     * 文件向量化状态代码：有云系统返回
     */
    private String indexingStatus;

    /**
     * 文件向量化状态名称：有云系统返回
     */
    private String indexingStatusName;

    /**
     * guid
     */
    private String guid;

    /**
     * 父级guid
     */
    private String parentGuid;

    /**
     * 分类（机构、部门、个人）
     */
    private String category;

    /**
     * 部门编码-部门id
     */
    private String depNum;

    /**
     * 全称
     */
    private String depName;

    /**
     * 文件向量化配置编码
     */
    private String embeddingConfigCode;

    /**
     * 文件向量化配置
     */
    private String embeddingConfigName;

    /**
     * 文件年份
     */
    private Integer fileYear;

    /**
     * 是否参与问答
     */
    private Boolean joinQuery;

    /**
     * 预览md文件id
     */
    private String previewFileId;

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
     * 创建人 ID
     */
    private String createUserId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 更新人 ID
     */
    private String updateUserId;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 版本号（用于乐观锁）
     */
    private Integer version;

    /**
     * 文件夹是否可编辑或删除
     */
    private Boolean editAuth;

    /**
     * 查看权限
     */
    private Boolean viewAuth;

    /**
     * 删除权限
     */
    private Boolean deleteAuth;

    /**
     * 下载权限
     */
    private Boolean downloadAuth;

    /**
     * 共享权限
     */
    private Boolean shareAuth;

    /**
     * 是否可建子文件夹
     */
    private Boolean addFolderAuth;

    /**
     * 查看操作日志权限
     */
    private Boolean viewLogAuth;

    /**
     * 文件向量化配置
     */
    private FileEmbeddingConfigDTO embeddingConfig;

    /**
     * 是否可以移动权限
     */
    private Boolean moveAuth;


    private Boolean uploadAuth;
    /**
     * 是否收藏了
     */
    private Boolean favorite;
    /**
     *  进度
     */
    private Double progress;

    /**
     * 密级
     */
    private String level;

    /**
     * 提取状态
     */
    private String embedStatus;

    /**
     * 知悉范围
     */
    private List<Integer> scope;

    /**
     * 知悉规则
     */
    private String scopeRule;

    /**
     * 文件夹类型
     */
    private Integer type;


}