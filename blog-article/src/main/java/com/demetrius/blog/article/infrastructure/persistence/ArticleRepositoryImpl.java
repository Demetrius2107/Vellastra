package com.demetrius.blog.article.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demetrius.blog.article.domain.article.entity.Article;
import com.demetrius.blog.article.domain.article.repository.ArticleRepository;
import com.demetrius.blog.article.infrastructure.persistence.converter.ArticleConverter;
import com.demetrius.blog.article.infrastructure.persistence.mapper.ArticleMapper;
import com.demetrius.blog.article.infrastructure.persistence.po.ArticlePO;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>Title: ArticleRepositoryImpl</p>
 * <p>Description: 文章仓储实现（MyBatis-Plus）</p>
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
@Repository
public class ArticleRepositoryImpl implements ArticleRepository {

    private final ArticleMapper articleMapper;
    private final ArticleConverter articleConverter;

    public ArticleRepositoryImpl(ArticleMapper articleMapper, ArticleConverter articleConverter) {
        this.articleMapper = articleMapper;
        this.articleConverter = articleConverter;
    }

    @Override
    public Article findById(Long id) {
        ArticlePO po = articleMapper.selectById(id);
        return po != null ? articleConverter.toDomain(po) : null;
    }

    @Override
    public Page<Article> findPage(long current, long size, Long categoryId,
                                  String keyword, String tag, Long authorId) {
        LambdaQueryWrapper<ArticlePO> wrapper = new LambdaQueryWrapper<ArticlePO>()
                .eq(categoryId != null, ArticlePO::getCategoryId, categoryId)
                .eq(authorId != null, ArticlePO::getAuthorId, authorId)
                .like(StringUtils.hasText(keyword), ArticlePO::getTitle, keyword)
                .like(StringUtils.hasText(tag), ArticlePO::getTags, tag)
                .orderByDesc(ArticlePO::getIsTop)
                .orderByDesc(ArticlePO::getCreateTime);

        Page<ArticlePO> poPage = articleMapper.selectPage(new Page<>(current, size), wrapper);

        Page<Article> domainPage = new Page<>(poPage.getCurrent(), poPage.getSize(), poPage.getTotal());
        domainPage.setRecords(poPage.getRecords().stream().map(articleConverter::toDomain).toList());
        return domainPage;
    }

    @Override
    public void save(Article article) {
        ArticlePO po = articleConverter.toPO(article);
        if (po.getId() == null) {
            articleMapper.insert(po);
            article.setId(po.getId());
        } else {
            articleMapper.updateById(po);
        }
    }

    @Override
    public void delete(Long id) {
        articleMapper.deleteById(id);
    }

    @Override
    public void updateViewCount(Long id) {
        articleMapper.updateViewCount(id);
    }

    @Override
    public boolean toggleLike(Long articleId, Long userId) {
        // 检查是否已点赞
        Integer count = articleMapper.checkLikeExists(articleId, userId);
        if (count != null && count > 0) {
            // 已点赞 → 取消
            articleMapper.deleteLike(articleId, userId);
            return false;
        } else {
            // 未点赞 → 新增
            articleMapper.insertLike(articleId, userId);
            return true;
        }
    }

    @Override
    public List<Article> findLatest(int size) {
        LambdaQueryWrapper<ArticlePO> wrapper = new LambdaQueryWrapper<ArticlePO>()
                .orderByDesc(ArticlePO::getCreateTime)
                .last("LIMIT " + size);
        List<ArticlePO> poList = articleMapper.selectList(wrapper);
        return poList.stream().map(articleConverter::toDomain).toList();
    }
}
