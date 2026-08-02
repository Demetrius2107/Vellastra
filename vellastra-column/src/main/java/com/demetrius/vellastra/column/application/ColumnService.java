package com.demetrius.vellastra.column.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demetrius.vellastra.column.domain.column.entity.Column;
import com.demetrius.vellastra.column.infrastructure.persistence.mapper.ColumnMapper;
import com.demetrius.vellastra.column.infrastructure.persistence.po.ColumnPO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ColumnService {

    private final ColumnMapper columnMapper;

    public ColumnService(ColumnMapper columnMapper) { this.columnMapper = columnMapper; }

    public IPage<ColumnPO> list(int current, int size, String status, Boolean featured) {
        return columnMapper.selectPage(new Page<>(current, size),
                new LambdaQueryWrapper<ColumnPO>()
                        .eq(status != null, ColumnPO::getStatus, status)
                        .eq(featured != null, ColumnPO::getIsFeatured, featured)
                        .orderByAsc(ColumnPO::getSortOrder)
                        .orderByDesc(ColumnPO::getCreateTime));
    }

    public List<ColumnPO> listAll(String status) {
        return columnMapper.selectList(new LambdaQueryWrapper<ColumnPO>()
                .eq(status != null, ColumnPO::getStatus, status)
                .orderByAsc(ColumnPO::getSortOrder));
    }

    public ColumnPO getById(Long id) { return columnMapper.selectById(id); }

    @Transactional
    public Long create(String name, String slug, String description, String coverImage,
                       Long authorId, String authorName, Boolean isFeatured) {
        ColumnPO po = new ColumnPO();
        po.setName(name); po.setSlug(slug); po.setDescription(description);
        po.setCoverImage(coverImage); po.setAuthorId(authorId);
        po.setAuthorName(authorName); po.setStatus("active");
        po.setArticleCount(0); po.setSortOrder(0);
        po.setIsFeatured(isFeatured != null ? isFeatured : false);
        po.setCreateTime(java.time.LocalDateTime.now());
        po.setUpdateTime(java.time.LocalDateTime.now());
        columnMapper.insert(po);
        log.info("专栏创建成功: id={}, name={}", po.getId(), name);
        return po.getId();
    }

    @Transactional
    public void update(Long id, String name, String description, String coverImage,
                       Integer sortOrder, String status, Boolean isFeatured) {
        ColumnPO po = columnMapper.selectById(id);
        if (po == null) throw new RuntimeException("专栏不存在");
        if (name != null) po.setName(name);
        if (description != null) po.setDescription(description);
        if (coverImage != null) po.setCoverImage(coverImage);
        if (sortOrder != null) po.setSortOrder(sortOrder);
        if (status != null) po.setStatus(status);
        if (isFeatured != null) po.setIsFeatured(isFeatured);
        po.setUpdateTime(java.time.LocalDateTime.now());
        columnMapper.updateById(po);
        log.info("专栏更新成功: id={}", id);
    }

    @Transactional
    public void delete(Long id) { columnMapper.deleteById(id); log.info("专栏删除成功: id={}", id); }

    @Transactional
    public void updateArticleCount(Long columnId) {
        ColumnPO po = columnMapper.selectById(columnId);
        if (po != null) {
            long count = columnMapper.selectCount(new LambdaQueryWrapper<>());
            // 实际应从 t_column_article 表统计
            po.setUpdateTime(java.time.LocalDateTime.now());
            columnMapper.updateById(po);
        }
    }
}
