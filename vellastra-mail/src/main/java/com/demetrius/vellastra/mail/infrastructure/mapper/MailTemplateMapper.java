package com.demetrius.vellastra.mail.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demetrius.vellastra.mail.infrastructure.po.MailTemplatePO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>Title: MailTemplateMapper</p>
 * <p>Description: 邮件模板 Mapper（MyBatis-Plus）</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-08-03
 * @updateTime 2026-08-03
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Mapper
public interface MailTemplateMapper extends BaseMapper<MailTemplatePO> {
}
