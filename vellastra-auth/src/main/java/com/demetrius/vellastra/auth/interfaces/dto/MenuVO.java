package com.demetrius.vellastra.auth.interfaces.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单视图对象（含子菜单树）
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