package com.demetrius.vellastra.comment.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demetrius.vellastra.comment.domain.comment.entity.Comment;
import com.demetrius.vellastra.comment.domain.comment.repository.CommentRepository;
import com.demetrius.vellastra.comment.infrastructure.persistence.converter.CommentConverter;
import com.demetrius.vellastra.comment.infrastructure.persistence.mapper.CommentMapper;
import com.demetrius.vellastra.comment.infrastructure.persistence.po.CommentPO;
import org.springframework.stereotype.Repository;

/**
 * <p>Title: CommentRepositoryImpl</p>
 * <p>Description: 评论仓储实现</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-05-17
 * @updateTime 2026-07-05
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Repository
public class CommentRepositoryImpl implements CommentRepository {

    private final CommentMapper commentMapper;
    private final CommentConverter commentConverter;

    public CommentRepositoryImpl(CommentMapper commentMapper, CommentConverter commentConverter) {
        this.commentMapper = commentMapper;
        this.commentConverter = commentConverter;
    }

    @Override
    public Comment findById(Long id) {
        CommentPO po = commentMapper.selectById(id);
        return po != null ? commentConverter.toDomain(po) : null;
    }

    @Override
    public Page<Comment> findPage(long current, long size, Long articleId, Integer status) {
        LambdaQueryWrapper<CommentPO> wrapper = new LambdaQueryWrapper<CommentPO>()
                .eq(articleId != null, CommentPO::getArticleId, articleId)
                .eq(status != null, CommentPO::getStatus, status)
                .orderByDesc(CommentPO::getCreateTime);

        Page<CommentPO> poPage = commentMapper.selectPage(new Page<>(current, size), wrapper);

        Page<Comment> domainPage = new Page<>(poPage.getCurrent(), poPage.getSize(), poPage.getTotal());
        domainPage.setRecords(poPage.getRecords().stream().map(commentConverter::toDomain).toList());
        return domainPage;
    }

    @Override
    public void save(Comment comment) {
        CommentPO po = commentConverter.toPO(comment);
        if (po.getId() == null) {
            commentMapper.insert(po);
            comment.setId(po.getId());
        } else {
            commentMapper.updateById(po);
        }
    }

    @Override
    public void delete(Long id) {
        commentMapper.deleteById(id);
    }
}
