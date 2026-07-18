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
 * @updateTime 2026-07-05
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
public interface UserRepository {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    User findById(Long id);

    void save(User user);

    void delete(Long id);
}
