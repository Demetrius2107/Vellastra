package com.demetrius.vellastra.auth.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demetrius.vellastra.auth.infrastructure.persistence.po.RoleMenuPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>Title: RoleMenuMapper</p>
 * <p>Description: 角色-菜单关联 Mapper（MyBatis-Plus）</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-18
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenuPO> {
}