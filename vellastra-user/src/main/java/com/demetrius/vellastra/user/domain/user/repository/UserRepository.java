package com.demetrius.vellastra.user.domain.user.repository;

import com.demetrius.vellastra.user.domain.user.entity.User;

/**
 * <p>Title: 用户仓储接口</p>
 * <p>Description: 用户领域层仓储接口，定义用户持久化契约</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-13
 * @updateTime 2026-07-13
 * Copyright © 2026 wanqiu All rights reserved
 */
public interface UserRepository {

    User findById(Long id);

    void save(User user);
}
