package com.demetrius.vellastra.mail.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>Title: EmailSendLogPO</p>
 * <p>Description: 邮件发送记录持久化对象，与 t_mail_send_log 表对应</p>
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
@TableName("t_mail_send_log")
public class EmailSendLogPO {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 收件人邮箱 */
    private String to;
    /** 邮件主题 */
    private String subject;
    /** 模板编码（关联 t_mail_template） */
    private String templateCode;
    /** 批量发送批次号 */
    private String batchNo;
    /** 状态: pending / sending / sent / failed / bounced */
    private String status;
    /** 重试次数 */
    private Integer retryCount;
    /** 失败原因 */
    private String errorMsg;
    /** 退信原因 */
    private String bounceReason;
    /** 打开时间（埋点） */
    private LocalDateTime openedAt;
    /** 首次点击时间（埋点） */
    private LocalDateTime clickedAt;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
