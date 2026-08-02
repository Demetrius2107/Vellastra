package com.demetrius.vellastra.mail.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_mail_subscriber")
public class SubscriberPO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String email;
    private String name;
    /** 状态: pending 待确认 / confirmed 已确认 / unsubscribed 已退订 */
    private String status;
    private String confirmToken;
    private String unsubscribeToken;
    private LocalDateTime confirmedAt;
    private LocalDateTime unsubscribedAt;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
