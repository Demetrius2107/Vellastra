package com.demetrius.blog.auth.domain.user.repository;

import com.demetrius.blog.auth.domain.user.entity.User;

import java.util.Optional;

/**
 * <p>Title: UserRepository</p>
 * <p>Description: 用户仓储接口（auth 模块）</p>
 * <p>项目名称: Blog-BackEnd-MS</p>
 *
 * @author wanqiu
 * @version 1.0
 * @date 2026年05月17日 首次创建
 * @date 2026年07月05日 最后修改
 *
 * All rights Reserved, Designed By wanqiu
 * @Copyright: 2026
 */
public interface UserRepository {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    User findById(Long id);

    void save(User user);

    void delete(Long id);
}
