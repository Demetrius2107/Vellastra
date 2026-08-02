package com.demetrius.vellastra.mail.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>Title: SubscriberPO</p>
 * <p>Description: 邮件订阅者持久化对象，与 t_mail_subscriber 表对应</p>
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
@TableName("t_mail_subscriber")
public class SubscriberPO {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 订阅邮箱 */
    private String email;
    /** 订阅者姓名 */
    private String name;
    /** 状态: pending 待确认 / confirmed 已确认 / unsubscribed 已退订 */
    private String status;
    /** 确认 token（用于确认链接） */
    private String confirmToken;
    /** 退订 token（用于退订链接） */
    private String unsubscribeToken;
    /** 确认时间 */
    private LocalDateTime confirmedAt;
    /** 退订时间 */
    private LocalDateTime unsubscribedAt;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
