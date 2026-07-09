package com.demetrius.blog.comment.infrastructure.persistence.converter;

import com.demetrius.blog.comment.domain.comment.entity.Comment;
import com.demetrius.blog.comment.infrastructure.persistence.po.CommentPO;
import org.springframework.stereotype.Component;

/**
 * <p>Title: CommentConverter</p>
 * <p>Description: 评论对象转换器（PO <-> Domain）</p>
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
@Component
public class CommentConverter {

    public Comment toDomain(CommentPO po) {
        if (po == null) return null;
        return Comment.builder()
                .id(po.getId())
                .articleId(po.getArticleId())
                .userId(po.getUserId())
                .parentId(po.getParentId())
                .replyToId(po.getReplyToId())
                .replyToUserId(po.getReplyToUserId())
                .content(po.getContent())
                .ipAddress(po.getIpAddress())
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
        po.setReplyToId(domain.getReplyToId());
        po.setReplyToUserId(domain.getReplyToUserId());
        po.setContent(domain.getContent());
        po.setIpAddress(domain.getIpAddress());
        po.setStatus(domain.getStatus());
        po.setLikeCount(domain.getLikeCount());
        po.setCreateTime(domain.getCreateTime());
        po.setUpdateTime(domain.getUpdateTime());
        return po;
    }
}
