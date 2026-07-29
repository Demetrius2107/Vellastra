package com.demetrius.vellastra.auth.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demetrius.vellastra.auth.infrastructure.persistence.mapper.FriendLinkMapper;
import com.demetrius.vellastra.auth.infrastructure.persistence.po.FriendLinkPO;
import com.demetrius.vellastra.common.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>Title: FriendLinkService</p>
 * <p>Description: 友情链接服务，提供友情链接 CRUD</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-29
 * @updateTime 2026-07-29
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Service
public class FriendLinkService {

    private final FriendLinkMapper friendLinkMapper;

    public FriendLinkService(FriendLinkMapper friendLinkMapper) {
        this.friendLinkMapper = friendLinkMapper;
    }

    /** 获取所有友情链接（按排序） */
    public List<FriendLinkPO> listAll() {
        return friendLinkMapper.selectList(
                new LambdaQueryWrapper<FriendLinkPO>()
                        .eq(FriendLinkPO::getStatus, 1)
                        .orderByAsc(FriendLinkPO::getSortOrder));
    }

    /** 根据 ID 获取 */
    public FriendLinkPO getById(Long id) {
        FriendLinkPO po = friendLinkMapper.selectById(id);
        if (po == null) throw ErrorCode.NOT_FOUND.toException();
        return po;
    }

    /** 新增 */
    @Transactional
    public Long create(String name, String url, String description, Integer sortOrder) {
        FriendLinkPO po = new FriendLinkPO();
        po.setName(name);
        po.setUrl(url);
        po.setDescription(description);
        po.setSortOrder(sortOrder != null ? sortOrder : 0);
        po.setStatus(1);
        po.setCreateTime(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());
        friendLinkMapper.insert(po);
        return po.getId();
    }

    /** 更新 */
    @Transactional
    public void update(Long id, String name, String url, String description, Integer sortOrder) {
        FriendLinkPO po = friendLinkMapper.selectById(id);
        if (po == null) throw ErrorCode.NOT_FOUND.toException();
        po.setName(name);
        po.setUrl(url);
        po.setDescription(description);
        if (sortOrder != null) po.setSortOrder(sortOrder);
        po.setUpdateTime(LocalDateTime.now());
        friendLinkMapper.updateById(po);
    }

    /** 删除 */
    @Transactional
    public void delete(Long id) {
        friendLinkMapper.deleteById(id);
    }
}