package com.demetrius.vellastra.mail.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

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
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
