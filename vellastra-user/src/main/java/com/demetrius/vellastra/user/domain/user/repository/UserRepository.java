package com.demetrius.vellastra.user.domain.user.repository;

import com.demetrius.vellastra.user.domain.user.entity.User;

import java.util.List;

/**
 * <p>Title: 用户仓储接口</p>
 * <p>Description: 用户领域仓储接口，定义用户持久化契约</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-13
 * @updateTime 2026-07-19
 * Copyright © 2026 wanqiu All rights reserved
 */
public interface UserRepository {

    /** 根据ID查询用户 */
    User findById(Long id);

    /** 保存用户（新增或更新） */
    void save(User user);

    /**
     * 分页查询用户列表
     *
     * @param current 当前页码
     * @param size    每页大小
     * @param keyword 搜索关键词（模糊匹配 username/nickname/email）
     * @param status  状态筛选（可选）
     * @return 用户列表
     */
    List<User> findPage(int current, int size, String keyword, Integer status);

    /** 查询总数（用于分页） */
    long count(String keyword, Integer status);
}