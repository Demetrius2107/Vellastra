package com.demetrius.vellastra.auth.interfaces.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>Title: RoleVO</p>
 * <p>Description: 角色视图对象</p>
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
public class RoleVO {
    private Long id;
    private String roleName;
    private String roleCode;
    private String description;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createTime;
}