package com.demetrius.vellastra.auth.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 更新角色请求
 */
@Data
public class UpdateRoleRequest {
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    private String description;
    private Integer sortOrder;
    private Integer status;
}