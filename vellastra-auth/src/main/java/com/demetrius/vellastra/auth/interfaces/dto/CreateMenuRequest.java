package com.demetrius.vellastra.auth.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * <p>Title: CreateMenuRequest</p>
 * <p>Description: 创建菜单请求 DTO</p>
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
public class CreateMenuRequest {
    /** 菜单名称 */
    @NotBlank(message = "菜单名称不能为空")
    private String menuName;

    /** 类型：1目录 2菜单 3按钮 */
    private Integer menuType;
    /** 父菜单ID（0为顶级） */
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
}