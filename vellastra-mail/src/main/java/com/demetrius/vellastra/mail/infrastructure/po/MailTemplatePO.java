package com.demetrius.vellastra.mail.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>Title: MailTemplatePO</p>
 * <p>Description: 邮件模板持久化对象，与 t_mail_template 表对应</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-08-03
 * @updateTime 2026-08-03
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Data
@TableName("t_mail_template")
public class MailTemplatePO {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 模板名称 */
    private String name;
    /** 模板编码（唯一标识，用于代码引用） */
    private String code;
    /** 邮件主题（支持 {{var}} 变量） */
    private String subject;
    /** 邮件正文（HTML，支持 {{var}} 变量） */
    private String content;
    /** 状态: active / disabled */
    private String status;
    /** 创建人 */
    private String createdBy;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
