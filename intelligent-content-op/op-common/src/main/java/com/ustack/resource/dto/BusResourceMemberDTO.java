package com.ustack.resource.dto;

import lombok.Data;

import java.util.Date;

/**
 * 文件夹成员权限表
 * @author allm
 * @TableName bus_resource_member
 */
@Data
public class BusResourceMemberDTO {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * guid
     */
    private String guid;

    /**
     * 文件夹ID
     */
    private Integer folderId;

    /**
     * 成员ID
     */
    private String memberId;

    /**
     * 成员姓名
     */
    private String memberName;

    /**
     * 是否管理员
     */
    private Boolean isAdmin;

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
    private Boolean uploadAuth;

    /**
     * 编辑权限
     */
    private Boolean editAuth;

    /**
     * 删除权限
     */
    private Boolean deleteAuth;

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
}
