package com.demetrius.vellastra.auth.interfaces.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>Title: MenuVO</p>
 * <p>Description: 菜单视图对象（含子菜单树）</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-18
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Data
public class MenuVO {
    /** 主键ID */
    private Long id;
    /** 菜单名称 */
    private String menuName;
    /** 类型：1目录 2菜单 3按钮 */
    private Integer menuType;
    /** 父菜单ID */
    private Long parentId;
    /** 路由路径 */
    private String path;
    /** 组件路径 */
    private String component;
    /** 权限标识，如 "article:create" */
    private String perms;
    /** 图标 */
    private String icon;
    /** 排序权重 */
    private Integer sortOrder;
    /** 是否显示：0隐藏 1显示 */
    private Integer visible;
    /** 状态：0禁用 1正常 */
    private Integer status;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 子菜单列表 */
    private List<MenuVO> children;
}