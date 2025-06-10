package com.ustack.resource.dto;

import com.ustack.global.common.validation.ValidGroup;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

/**
 * @author Liyingzheng
 * @data 2025/5/8 16:50
 * @describe 收藏 DTO
 */
@Data
public class BusResourceFavoriteDTO {

    /**
     * 主键id
     */
    private Long id;

    /**
     * guid
     */
    @NotBlank(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "guid不能为空")
    private String guid;

    /**
     * 资源名称
     */
    @NotBlank(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "资源名不能为空")
    private String name;

    /**
     * 资源id
     */
    @NotNull(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "资源id不能为空")
    @Min(value = 1, message = "资源id 必须大于 0")
    private Integer resourceId;

    /**
     * 资源类型（1文件夹 2文件）
     */
    @NotNull(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "资源类型（1文件夹 2文件）不能为空")
    @Min(value = 1, message = "资源类型 只能为 1、2")
    @Max(value = 2, message = "资源类型 只能为 1、2")
    private Integer resourceType;

    /**
     * 父级资源 ID（根目录为 0）
     */
    @NotNull(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "父级资源 ID（根目录为 0）不能为空")
    @Min(value = 0, message = "资源id 必须为正数")
    private Integer parentId;

    /**
     * 文件类型：doc/docx/excel/txt/……、多值用英文逗号分隔
     */
    private String fileType;

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
}
