package com.demetrius.vellastra.mail.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demetrius.vellastra.mail.infrastructure.po.MailTemplatePO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MailTemplateMapper extends BaseMapper<MailTemplatePO> {
}
