package com.demetrius.vellastra.auth.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * <p>Title: UpdateRoleRequest</p>
 * <p>Description: 更新角色请求 DTO</p>
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
public class UpdateRoleRequest {
    /** 角色名称 */
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    /** 角色编码，如 SUPER_ADMIN */
    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    /** 角色描述 */
    private String description;
    /** 排序权重 */
    private Integer sortOrder;
    /** 状态：0禁用 1正常 */
    private Integer status;
}