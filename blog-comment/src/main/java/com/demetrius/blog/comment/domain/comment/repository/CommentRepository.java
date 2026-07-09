package com.demetrius.blog.comment.domain.comment.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demetrius.blog.comment.domain.comment.entity.Comment;

/**
 * <p>Title: CommentRepository</p>
 * <p>Description: 评论仓储接口</p>
 * <p>项目名称: Blog-BackEnd-MS</p>
 *
 * @author wanqiu
 * @version 1.0
 * @date 2026年05月17日 首次创建
 * @date 2026年07月05日 最后修改
 *
 * All rights Reserved, Designed By wanqiu
 * @Copyright: 2026
 */
public interface CommentRepository {

    Comment findById(Long id);

    Page<Comment> findPage(long current, long size, Long articleId, Integer status);

    void save(Comment comment);

    void delete(Long id);
}
