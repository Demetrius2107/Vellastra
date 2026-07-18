package com.demetrius.vellastra.auth.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demetrius.vellastra.auth.infrastructure.persistence.mapper.UserRoleMapper;
import com.demetrius.vellastra.auth.infrastructure.persistence.po.UserRolePO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <h3>用户-角色绑定应用服务</h3>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
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