package com.demetrius.vellastra.auth.interfaces.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色视图对象
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