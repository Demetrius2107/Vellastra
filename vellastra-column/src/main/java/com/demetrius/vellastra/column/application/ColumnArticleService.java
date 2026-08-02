package com.demetrius.vellastra.column.application;

import com.demetrius.vellastra.column.infrastructure.persistence.mapper.ColumnArticleMapper;
import com.demetrius.vellastra.column.infrastructure.persistence.po.ColumnArticlePO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ColumnArticleService {

    private final ColumnArticleMapper articleMapper;

    public ColumnArticleService(ColumnArticleMapper articleMapper) { this.articleMapper = articleMapper; }

    public List<ColumnArticlePO> getArticles(Long columnId) {
        return articleMapper.findByColumnId(columnId);
    }

    @Transactional
    public void addArticle(Long columnId, Long articleId, String articleTitle, String note) {
        ColumnArticlePO po = new ColumnArticlePO();
        po.setColumnId(columnId); po.setArticleId(articleId);
        po.setArticleTitle(articleTitle); po.setSortOrder(0);
        po.setNote(note); po.setCreateTime(java.time.LocalDateTime.now());
        articleMapper.insert(po);
        log.info("文章收录专栏: columnId={}, articleId={}, title={}", columnId, articleId, articleTitle);
    }

    @Transactional
    public void batchAddArticles(Long columnId, List<Long> articleIds, List<String> articleTitles) {
        for (int i = 0; i < articleIds.size(); i++) {
            String title = articleTitles != null && i < articleTitles.size() ? articleTitles.get(i) : "";
            addArticle(columnId, articleIds.get(i), title, null);
        }
        log.info("批量收录文章: columnId={}, count={}", columnId, articleIds.size());
    }

    @Transactional
    public void removeArticle(Long id) {
        articleMapper.deleteById(id);
        log.info("文章移出专栏: id={}", id);
    }

    @Transactional
    public void updateSortOrder(Long id, Integer sortOrder) {
        ColumnArticlePO po = articleMapper.selectById(id);
        if (po != null) { po.setSortOrder(sortOrder); articleMapper.updateById(po); }
    }
}
