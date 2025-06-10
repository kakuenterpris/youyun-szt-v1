package com.ustack.op.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 菜单权限表
 * @TableName sys_menu
 */
@TableName(value ="sys_menu")
@Data
public class SysMenuEntity implements Serializable {
    /**
     * 菜单ID
     */
    @TableId(value = "menu_id", type = IdType.AUTO)
    private Long menuId;

    /**
     * 菜单名称
     */
    @TableField(value = "menu_name")
    private String menuName;

    /**
     * 父菜单ID
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 权限类型（system:系统管理员, security:安全管理员, audit:审计员, user:普通用户）
     */
    @TableField(value = "auth_type")
    private String authType;

    /**
     * 显示顺序
     */
    @TableField(value = "order_num")
    private Integer orderNum;

    /**
     * 路由地址
     */
    @TableField(value = "path")
    private String path;

    /**
     * 组件路径
     */
    @TableField(value = "component")
    private String component;

    /**
     * 路由参数
     */
    @TableField(value = "query")
    private String query;

    /**
     * 路由名称
     */
    @TableField(value = "route_name")
    private String routeName;

    /**
     * 是否为外链（0是 1否）
     */
    @TableField(value = "is_frame")
    private Integer isFrame;

    /**
     * 是否缓存（0缓存 1不缓存）
     */
    @TableField(value = "is_cache")
    private Integer isCache;

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    @TableField(value = "menu_type")
    private String menuType;

    /**
     * 菜单状态（0显示 1隐藏）
     */
    @TableField(value = "visible")
    private String visible;

    /**
     * 菜单状态（0正常 1停用）
     */
    @TableField(value = "status")
    private String status;

    /**
     * 权限标识
     */
    @TableField(value = "perms")
    private String perms;

    /**
     * 菜单图标
     */
    @TableField(value = "icon")
    private String icon;

    /**
     * 创建者
     */
    @TableField(value = "create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新者
     */
    @TableField(value = "update_by")
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    //子菜单
    @TableField(exist = false)
    private List<SysMenuEntity> Children;

}