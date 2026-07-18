package com.demetrius.vellastra.auth.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * <h3>角色-菜单关联持久化对象</h3>
 *
 * <p>与 t_role_menu 表 1:1 对应。</p>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
 */
@Data
@TableName("t_role_menu")
public class RoleMenuPO {

    private Long id;

    private Long roleId;

    private Long menuId;
}