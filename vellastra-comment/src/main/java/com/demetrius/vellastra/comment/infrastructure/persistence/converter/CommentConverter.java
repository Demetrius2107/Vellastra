package com.demetrius.vellastra.comment.infrastructure.persistence.converter;

import com.demetrius.vellastra.comment.domain.comment.entity.Comment;
import com.demetrius.vellastra.comment.infrastructure.persistence.po.CommentPO;
import org.springframework.stereotype.Component;

/**
 * <p>Title: CommentConverter</p>
 * <p>Description: 评论对象转换器（PO <-> Domain），与 blog_comment 表对应</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
 */
@Component
public class CommentConverter {

    public Comment toDomain(CommentPO po) {
        if (po == null) return null;
        return Comment.builder()
                .id(po.getId())
                .articleId(po.getArticleId())
                .userId(po.getUserId())
                .parentId(po.getParentId())
                .replyUserId(po.getReplyUserId())
                .content(po.getContent())
                .status(po.getStatus())
                .likeCount(po.getLikeCount())
                .createTime(po.getCreateTime())
                .updateTime(po.getUpdateTime())
                .build();
    }

    public CommentPO toPO(Comment domain) {
        if (domain == null) return null;
        CommentPO po = new CommentPO();
        po.setId(domain.getId());
        po.setArticleId(domain.getArticleId());
        po.setUserId(domain.getUserId());
        po.setParentId(domain.getParentId());
        po.setReplyUserId(domain.getReplyUserId());
        po.setContent(domain.getContent());
        po.setStatus(domain.getStatus());
        po.setLikeCount(domain.getLikeCount());
        po.setCreateTime(domain.getCreateTime());
        po.setUpdateTime(domain.getUpdateTime());
        return po;
    }
}
