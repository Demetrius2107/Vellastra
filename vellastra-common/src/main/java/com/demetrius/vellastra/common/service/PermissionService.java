package com.demetrius.vellastra.common.service;

import java.util.List;

/**
 * <p>Title: PermissionService</p>
 * <p>Description: 权限查询服务接口，用于根据角色ID获取权限标识列表</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-19
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
public interface PermissionService {

    /**
     * 根据角色ID列表查询对应的权限标识列表
     *
     * @param roleIds 角色ID列表
     * @return 权限标识列表，如 ["article:create", "article:edit"]
     */
    List<String> getPermissionsByRoleIds(List<Long> roleIds);
}