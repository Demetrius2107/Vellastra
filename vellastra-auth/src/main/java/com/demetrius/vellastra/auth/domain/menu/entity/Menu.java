package com.demetrius.vellastra.auth.domain.menu.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <p>Title: Menu</p>
 * <p>Description: 菜单/权限领域实体，对应 t_menu 表（三级树形结构：目录→菜单→按钮）</p>
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Menu {

    /** 主键ID */
    private Long id;

    /** 父菜单ID（0为顶级） */
    private Long parentId;

    /** 菜单名称 */
    private String menuName;

    /** 类型：1目录 2菜单 3按钮 */
    private Integer menuType;

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

    /** 更新时间 */
    private LocalDateTime updateTime;

    public void initCreateTime() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    public void updateTime() {
        this.updateTime = LocalDateTime.now();
    }
}