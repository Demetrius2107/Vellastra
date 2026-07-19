package com.demetrius.vellastra.auth.infrastructure.persistence.converter;

import com.demetrius.vellastra.auth.domain.user.entity.User;
import com.demetrius.vellastra.auth.domain.user.valueobject.UserStatus;
import com.demetrius.vellastra.auth.infrastructure.persistence.po.UserPO;
import org.springframework.stereotype.Component;


/**
 * <p>Title: UserConverter</p>
 * <p>Description: 用户对象转换器（PO <-> Domain），全字段映射</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-05-17
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Component
public class UserConverter {

    /**
     * 将持久化对象转换为领域实体
     *
     * @param po 用户持久化对象
     * @return 用户领域实体
     */
    public User toDomain(UserPO po) {
        if (po == null) return null;
        return User.builder()
                .id(po.getId())
                .username(po.getUsername())
                .password(po.getPassword())
                .email(po.getEmail())
                .nickname(po.getNickname())
                .avatar(po.getAvatar())
                .status(UserStatus.of(po.getStatus()))
                .createTime(po.getCreateTime())
                .updateTime(po.getUpdateTime())
                .build();
    }

    /**
     * 将领域实体转换为持久化对象
     *
     * @param domain 用户领域实体
     * @return 用户持久化对象
     */
    public UserPO toPO(User domain) {
        if (domain == null) return null;
        UserPO po = new UserPO();
        po.setId(domain.getId());
        po.setUsername(domain.getUsername());
        po.setPassword(domain.getPassword());
        po.setEmail(domain.getEmail());
        po.setNickname(domain.getNickname());
        po.setAvatar(domain.getAvatar());
        po.setStatus(domain.getStatus().getCode());
        po.setCreateTime(domain.getCreateTime());
        po.setUpdateTime(domain.getUpdateTime());
        return po;
    }
}
