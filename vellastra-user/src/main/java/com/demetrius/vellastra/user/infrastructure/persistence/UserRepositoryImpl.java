package com.demetrius.vellastra.user.infrastructure.persistence;

import com.demetrius.vellastra.user.domain.user.entity.User;
import com.demetrius.vellastra.user.domain.user.repository.UserRepository;
import com.demetrius.vellastra.user.infrastructure.persistence.converter.UserConverter;
import com.demetrius.vellastra.user.infrastructure.persistence.mapper.UserMapper;
import com.demetrius.vellastra.user.infrastructure.persistence.po.UserPO;
import org.springframework.stereotype.Repository;

/**
 * <p>Title: 用户仓储实现</p>
 * <p>Description: 用户基础设施层仓储实现，基于MyBatis-Plus持久化用户数据</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-13
 * @updateTime 2026-07-13
 * Copyright © 2026 wanqiu All rights reserved
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;
    private final UserConverter userConverter;

    public UserRepositoryImpl(UserMapper userMapper, UserConverter userConverter) {
        this.userMapper = userMapper;
        this.userConverter = userConverter;
    }

    @Override
    public User findById(Long id) {
        UserPO po = userMapper.selectById(id);
        return po != null ? userConverter.toDomain(po) : null;
    }

    @Override
    public void save(User user) {
        UserPO po = userConverter.toPO(user);
        if (po.getId() == null) {
            userMapper.insert(po);
            user.setId(po.getId());
        } else {
            userMapper.updateById(po);
        }
    }
}
