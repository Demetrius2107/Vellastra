package com.demetrius.vellastra.auth.domain.role.repository;

import com.demetrius.vellastra.auth.domain.role.entity.Role;

import java.util.List;
import java.util.Optional;

/**
 * <h3>角色仓储接口</h3>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
 */
public interface RoleRepository {

    Optional<Role> findById(Long id);

    Role getById(Long id);

    List<Role> findAll();

    List<Role> findByIds(List<Long> ids);

    void save(Role role);

    void delete(Long id);
}