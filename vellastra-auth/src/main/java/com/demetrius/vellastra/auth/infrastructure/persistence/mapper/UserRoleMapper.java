package com.demetrius.vellastra.auth.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demetrius.vellastra.auth.infrastructure.persistence.po.UserRolePO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <h3>用户-角色关联 Mapper</h3>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRolePO> {
}