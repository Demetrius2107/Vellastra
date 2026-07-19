package com.demetrius.vellastra.auth.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demetrius.vellastra.auth.infrastructure.persistence.po.MenuPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>Title: MenuMapper</p>
 * <p>Description: 菜单 Mapper（MyBatis-Plus）</p>
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
public interface MenuMapper extends BaseMapper<MenuPO> {
}