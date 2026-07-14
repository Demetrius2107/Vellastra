package com.demetrius.vellastra.user.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demetrius.vellastra.user.infrastructure.persistence.po.UserPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>Title: 用户Mapper</p>
 * <p>Description: 用户MyBatis-Plus数据访问接口，提供用户表基础CRUD操作</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-13
 * @updateTime 2026-07-13
 * Copyright © 2026 wanqiu All rights reserved
 */
@Mapper
public interface UserMapper extends BaseMapper<UserPO> {
}
