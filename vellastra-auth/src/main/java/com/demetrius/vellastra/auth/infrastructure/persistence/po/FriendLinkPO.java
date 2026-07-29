package com.demetrius.vellastra.auth.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>Title: FriendLinkPO</p>
 * <p>Description: 友情链接持久化对象，与 t_friend_link 表对应</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-29
 * @updateTime 2026-07-29
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Data
@TableName("t_friend_link")
public class FriendLinkPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 链接名称 */
    private String name;

    /** 链接地址 */
    private String url;

    /** Logo图片 */
    private String logo;

    /** 描述 */
    private String description;

    /** 排序 */
    private Integer sortOrder;

    /** 状态：0禁用 1正常 */
    private Integer status;

    /** 逻辑删除 */
    private Integer deleted;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}