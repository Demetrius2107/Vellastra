package com.demetrius.vellastra.auth.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * <h3>用户-角色关联持久化对象</h3>
 *
 * <p>与 t_user_role 表 1:1 对应。</p>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
 */
@Data
@TableName("t_user_role")
public class UserRolePO {

    private Long id;

    private Long userId;

    private Long roleId;
}