package com.demetrius.vellastra.auth.domain.role.repository;

import com.demetrius.vellastra.auth.domain.role.entity.Role;

import java.util.List;
import java.util.Optional;

/**
 * <p>Title: RoleRepository</p>
 * <p>Description: 角色仓储接口</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-18
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
public interface RoleRepository {

    Optional<Role> findById(Long id);

    Role getById(Long id);

    List<Role> findAll();

    List<Role> findByIds(List<Long> ids);

    void save(Role role);

    void delete(Long id);
}