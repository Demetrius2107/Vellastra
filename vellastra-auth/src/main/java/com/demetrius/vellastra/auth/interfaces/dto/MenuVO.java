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
    private Long id;
    private String menuName;
    private Integer menuType;
    private Long parentId;
    private String path;
    private String component;
    private String perms;
    private String icon;
    private Integer sortOrder;
    private Integer visible;
    private Integer status;
    private LocalDateTime createTime;
    private List<MenuVO> children;
}