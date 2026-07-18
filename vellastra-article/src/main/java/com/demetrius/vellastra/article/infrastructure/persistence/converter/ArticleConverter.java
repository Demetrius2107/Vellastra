package com.demetrius.vellastra.article.infrastructure.persistence.converter;

import com.demetrius.vellastra.article.domain.article.entity.Article;
import com.demetrius.vellastra.article.infrastructure.persistence.po.ArticlePO;
import org.springframework.stereotype.Component;

/**
 * <p>Title: ArticleConverter</p>
 * <p>Description: 文章对象转换器（PO <-> Domain），全字段映射</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-05-17
 * @updateTime 2026-07-05
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Component
public class ArticleConverter {

    public Article toDomain(ArticlePO po) {
        if (po == null) {
            return null;
        }
        return Article.builder()
                .id(po.getId())
                .title(po.getTitle())
                .summary(po.getSummary())
                .content(po.getContent())
                .contentHtml(po.getContentHtml())
                .coverImage(po.getCoverImage())
                .categoryId(po.getCategoryId())
                .status(po.getStatus())
                .isTop(po.getIsTop())
                .authorId(po.getAuthorId())
                .viewCount(po.getViewCount())
                .likeCount(po.getLikeCount())
                .commentCount(po.getCommentCount())
                .publishTime(po.getPublishTime())
                .createTime(po.getCreateTime())
                .updateTime(po.getUpdateTime())
                .seoTitle(po.getSeoTitle())
                .seoDescription(po.getSeoDescription())
                .seoKeywords(po.getSeoKeywords())
                .build();
    }

    public ArticlePO toPO(Article domain) {
        if (domain == null) {
            return null;
        }
        ArticlePO po = new ArticlePO();
        po.setId(domain.getId());
        po.setTitle(domain.getTitle());
        po.setSummary(domain.getSummary());
        po.setContent(domain.getContent());
        po.setContentHtml(domain.getContentHtml());
        po.setCoverImage(domain.getCoverImage());
        po.setCategoryId(domain.getCategoryId());
        po.setStatus(domain.getStatus());
        po.setIsTop(domain.getIsTop());
        po.setAuthorId(domain.getAuthorId());
        po.setViewCount(domain.getViewCount());
        po.setLikeCount(domain.getLikeCount());
        po.setCommentCount(domain.getCommentCount());
        po.setPublishTime(domain.getPublishTime());
        po.setCreateTime(domain.getCreateTime());
        po.setUpdateTime(domain.getUpdateTime());
        po.setSeoTitle(domain.getSeoTitle());
        po.setSeoDescription(domain.getSeoDescription());
        po.setSeoKeywords(domain.getSeoKeywords());
        return po;
    }
}