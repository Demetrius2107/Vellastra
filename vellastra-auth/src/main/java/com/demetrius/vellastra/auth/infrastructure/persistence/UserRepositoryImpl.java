package com.demetrius.vellastra.auth.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demetrius.vellastra.auth.domain.user.entity.User;
import com.demetrius.vellastra.auth.domain.user.repository.UserRepository;
import com.demetrius.vellastra.auth.infrastructure.persistence.converter.UserConverter;
import com.demetrius.vellastra.auth.infrastructure.persistence.mapper.UserMapper;
import com.demetrius.vellastra.auth.infrastructure.persistence.po.UserPO;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * <p>Title: UserRepositoryImpl</p>
 * <p>Description: 用户仓储实现（MyBatis-Plus）</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-05-17
 * @updateTime 2026-07-05
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;
    private final UserConverter userConverter;

    public UserRepositoryImpl(UserMapper userConverter, UserMapper userMapper) {
        this.userMapper = userMapper;
        this.userConverter = new UserConverter();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        UserPO po = userMapper.selectOne(
                new LambdaQueryWrapper<UserPO>().eq(UserPO::getUsername, username)
        );
        return Optional.ofNullable(po).map(userConverter::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userMapper.exists(
                new LambdaQueryWrapper<UserPO>().eq(UserPO::getUsername, username)
        );
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

    @Override
    public void delete(Long id) {
        userMapper.deleteById(id);
    }
}
