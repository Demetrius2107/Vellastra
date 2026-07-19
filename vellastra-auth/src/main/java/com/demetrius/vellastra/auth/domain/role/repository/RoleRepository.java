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

    /** 根据 ID 查询角色（返回 Optional） */
    Optional<Role> findById(Long id);

    /** 根据 ID 获取角色（不存在返回 null） */
    Role getById(Long id);

    /** 查询所有角色 */
    List<Role> findAll();

    /** 根据 ID 列表批量查询角色 */
    List<Role> findByIds(List<Long> ids);

    /** 保存角色（新增或更新） */
    void save(Role role);

    /** 根据 ID 删除角色 */
    void delete(Long id);
}