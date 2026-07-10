package com.demetrius.vellastra.article.infrastructure.persistence.converter;

import com.demetrius.vellastra.article.domain.article.entity.Article;
import com.demetrius.vellastra.article.infrastructure.persistence.po.ArticlePO;
import org.springframework.stereotype.Component;

/**
 * <p>Title: ArticleConverter</p>
 * <p>Description: 文章对象转换器（PO <-> Domain）</p>
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
                .coverImage(po.getCoverImage())
                .categoryId(po.getCategoryId())
                .status(po.getStatus())
                .isTop(po.getIsTop())
                .tags(po.getTags())
                .authorId(po.getAuthorId())
                .viewCount(po.getViewCount())
                .likeCount(po.getLikeCount())
                .createTime(po.getCreateTime())
                .updateTime(po.getUpdateTime())
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
        po.setCoverImage(domain.getCoverImage());
        po.setCategoryId(domain.getCategoryId());
        po.setStatus(domain.getStatus());
        po.setIsTop(domain.getIsTop());
        po.setTags(domain.getTags());
        po.setAuthorId(domain.getAuthorId());
        po.setViewCount(domain.getViewCount());
        po.setLikeCount(domain.getLikeCount());
        po.setCommentCount(domain.getCommentCount());
        po.setPublishTime(domain.getPublishTime());
        po.setCreateTime(domain.getCreateTime());
        po.setUpdateTime(domain.getUpdateTime());
        return po;
    }
}
