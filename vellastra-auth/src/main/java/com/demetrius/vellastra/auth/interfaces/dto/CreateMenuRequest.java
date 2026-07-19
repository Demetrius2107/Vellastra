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
    @NotBlank(message = "菜单名称不能为空")
    private String menuName;

    private Integer menuType;
    private Long parentId;
    private String path;
    private String component;
    private String perms;
    private String icon;
    private Integer sortOrder;
}