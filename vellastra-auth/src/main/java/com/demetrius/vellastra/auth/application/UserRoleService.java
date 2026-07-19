package com.demetrius.vellastra.auth.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demetrius.vellastra.auth.infrastructure.persistence.mapper.UserRoleMapper;
import com.demetrius.vellastra.auth.infrastructure.persistence.po.UserRolePO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>Title: UserRoleService</p>
 * <p>Description: 用户-角色绑定应用服务</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-18
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Service
public class UserRoleService {

    private final UserRoleMapper userRoleMapper;

    public UserRoleService(UserRoleMapper userRoleMapper) {
        this.userRoleMapper = userRoleMapper;
    }

    /**
     * 获取用户的所有角色ID
     */
    public List<Long> getUserRoleIds(Long userId) {
        return userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRolePO>().eq(UserRolePO::getUserId, userId)
        ).stream().map(UserRolePO::getRoleId).toList();
    }

    /**
     * 分配用户角色（全量覆盖）
     */
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        // 删除旧关联
        userRoleMapper.delete(
                new LambdaQueryWrapper<UserRolePO>().eq(UserRolePO::getUserId, userId)
        );
        // 插入新关联
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                UserRolePO po = new UserRolePO();
                po.setUserId(userId);
                po.setRoleId(roleId);
                userRoleMapper.insert(po);
            }
        }
    }
}