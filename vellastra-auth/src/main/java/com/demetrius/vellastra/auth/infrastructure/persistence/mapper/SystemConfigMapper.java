package com.demetrius.vellastra.auth.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demetrius.vellastra.auth.infrastructure.persistence.po.SystemConfigPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>Title: SystemConfigMapper</p>
 * <p>Description: 系统配置 Mapper（MyBatis-Plus）</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-29
 * @updateTime 2026-07-29
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfigPO> {
}