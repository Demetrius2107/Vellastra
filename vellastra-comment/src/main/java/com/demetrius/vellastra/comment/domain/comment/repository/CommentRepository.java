package com.demetrius.vellastra.comment.domain.comment.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demetrius.vellastra.comment.domain.comment.entity.Comment;

/**
 * <p>Title: CommentRepository</p>
 * <p>Description: 评论仓储接口</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-05-17
 * @updateTime 2026-07-05
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
public interface CommentRepository {

    Comment findById(Long id);

    Page<Comment> findPage(long current, long size, Long articleId, Integer status);

    void save(Comment comment);

    void delete(Long id);
}
