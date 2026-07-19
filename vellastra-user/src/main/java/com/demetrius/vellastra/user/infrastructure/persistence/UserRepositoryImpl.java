package com.demetrius.vellastra.user.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demetrius.vellastra.user.domain.user.entity.User;
import com.demetrius.vellastra.user.domain.user.repository.UserRepository;
import com.demetrius.vellastra.user.infrastructure.persistence.converter.UserConverter;
import com.demetrius.vellastra.user.infrastructure.persistence.mapper.UserMapper;
import com.demetrius.vellastra.user.infrastructure.persistence.po.UserPO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>Title: 用户仓储实现</p>
 * <p>Description: 用户基础设施层仓储实现，基于MyBatis-Plus持久化用户数据</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-13
 * @updateTime 2026-07-19
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

    @Override
    public List<User> findPage(int current, int size, String keyword, Integer status) {
        LambdaQueryWrapper<UserPO> wrapper = buildQueryWrapper(keyword, status);
        // MyBatis-Plus 分页查询，手动分页
        long offset = (long) (current - 1) * size;
        wrapper.last("LIMIT " + offset + ", " + size);
        return userMapper.selectList(wrapper).stream()
                .map(userConverter::toDomain).toList();
    }

    @Override
    public long count(String keyword, Integer status) {
        LambdaQueryWrapper<UserPO> wrapper = buildQueryWrapper(keyword, status);
        return userMapper.selectCount(wrapper);
    }

    private LambdaQueryWrapper<UserPO> buildQueryWrapper(String keyword, Integer status) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w
                    .like(UserPO::getUsername, keyword)
                    .or()
                    .like(UserPO::getNickname, keyword)
                    .or()
                    .like(UserPO::getEmail, keyword));
        }
        if (status != null) {
            wrapper.eq(UserPO::getStatus, status);
        }
        wrapper.orderByDesc(UserPO::getCreateTime);
        return wrapper;
    }
}
