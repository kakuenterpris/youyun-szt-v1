package com.ustack.resource.vo;

import com.ustack.global.common.validation.ValidGroup;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

/**
 * @author Liyingzheng
 * @data 2025/5/9 15:55
 * @describe
 */
@Data
public class BusResourceFileVO {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * guid
     */
    private String guid;

    /**
     * 文件名称
     */
    @NotBlank(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "文件名不能为空")
    private String name;

    /**
     * 文件夹ID
     */
    private Integer folderId;

    /**
     * 文件下载id
     */
    private String fileId;

    /**
     * 资源大小（文件夹为0）、单位为字节
     */
    private String size;

    /**
     * 文件类型：doc/docx/excel/txt/……
     */
    private String fileType;

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
     * 文件年份
     */
    private Integer fileYear;

    /**
     * 是否参与问答
     */
    private Boolean joinQuery;

    /**
     * md5预览文件id
     */
    private String previewFileId;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建人ID
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
     * 资源类型：1-文件夹；2-文件
     */
    private Integer resourceType;

    /**
     * 文件向量化状态名称（前端显示：知识化状态）：有云系统返回
     */
    private String indexingStatusName;

    /**
     * 文件位置
     */
    private String fileLocation;

    /**
     * 查看权限
     */
    private Boolean viewAuth;

    /**
     * 下载权限
     */
    private Boolean downloadAuth;

    /**
     * 共享权限
     */
    private Boolean shareAuth;

    /**
     * 上传权限
     */
//    private Boolean uploadAuth;

    /**
     * 编辑权限
     */
    private Boolean editAuth;

    /**
     * 删除权限
     */
    private Boolean deleteAuth;

    /**
     * 查看操作日志权限
     */
    private Boolean viewLogAuth;
}
