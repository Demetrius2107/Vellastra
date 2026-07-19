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
    /** 主键ID */
    private Long id;
    /** 角色名称 */
    private String roleName;
    /** 角色编码，如 SUPER_ADMIN */
    private String roleCode;
    /** 角色描述 */
    private String description;
    /** 排序权重 */
    private Integer sortOrder;
    /** 状态：0禁用 1正常 */
    private Integer status;
    /** 创建时间 */
    private LocalDateTime createTime;
}