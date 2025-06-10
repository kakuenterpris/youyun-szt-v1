package com.ustack.resource.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author Liyingzheng
 * @data 2025/5/9 16:50
 * @describe
 */
@Data
public class BusResourceFavoriteVO {

    /**
     * 主键id
     */
    private Long id;

    /**
     * guid
     */
    private String guid;

    /**
     * 资源名称
     */
    private String name;

    /**
     * 资源id
     */
    private Integer resourceId;

    /**
     * 资源类型（1文件夹 2文件）
     */
    private Integer resourceType;

    /**
     * 父级资源 ID（根目录为 0）
     */
    private Integer parentId;

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
    private Long version;

    /**
     * 逻辑删除字段
     */
    private Boolean deleted;

    /**
     * 文件位置
     */
    private String fileLocation;

    /**
     * 文件向量化状态名称（前端显示：知识化状态）：有云系统返回
     */
    private String indexingStatusName;

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

    /**
     * 文件类型：doc/docx/excel/txt/……、多值用英文逗号分隔
     */
    private String fileType;
}
