package com.demetrius.vellastra.auth.domain.user.repository;

import com.demetrius.vellastra.auth.domain.user.entity.User;

import java.util.Optional;

/**
 * <p>Title: UserRepository</p>
 * <p>Description: 用户仓储接口（auth 模块）</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-05-17
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
public interface UserRepository {

    /** 根据用户名查询用户 */
    Optional<User> findByUsername(String username);

    /** 判断用户名是否已存在 */
    boolean existsByUsername(String username);

    /** 根据 ID 查询用户 */
    User findById(Long id);

    /** 保存用户（新增或更新） */
    void save(User user);

    /** 根据 ID 删除用户 */
    void delete(Long id);
}
