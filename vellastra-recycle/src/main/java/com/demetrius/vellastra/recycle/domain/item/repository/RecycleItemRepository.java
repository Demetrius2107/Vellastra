package com.demetrius.vellastra.recycle.domain.item.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demetrius.vellastra.recycle.domain.item.entity.RecycleItem;
import com.demetrius.vellastra.recycle.infrastructure.persistence.converter.RecycleItemConverter;
import com.demetrius.vellastra.recycle.infrastructure.persistence.mapper.RecycleItemMapper;
import com.demetrius.vellastra.recycle.infrastructure.persistence.po.RecycleItemPO;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class RecycleItemRepository {
    private final RecycleItemMapper mapper;
    private final RecycleItemConverter converter;

    public RecycleItemRepository(RecycleItemMapper mapper, RecycleItemConverter converter) {
        this.mapper = mapper; this.converter = converter;
    }

    public void save(RecycleItem item) {
        RecycleItemPO po = converter.toPO(item);
        if (po.getId() == null) mapper.insert(po);
        else mapper.updateById(po);
    }
    public RecycleItem findById(Long id) {
        RecycleItemPO po = mapper.selectById(id);
        return po != null ? converter.toDomain(po) : null;
    }
    public void delete(Long id) { mapper.deleteById(id); }

    public IPage<RecycleItem> findPage(int current, int size, String type, String keyword,
                                        Long dateFrom, Long dateTo) {
        LambdaQueryWrapper<RecycleItemPO> w = new LambdaQueryWrapper<RecycleItemPO>()
                .eq(StringUtils.hasText(type), RecycleItemPO::getItemType, type)
                .like(StringUtils.hasText(keyword), RecycleItemPO::getTitle, keyword)
                .ge(dateFrom != null, RecycleItemPO::getDeletedAt, new java.sql.Timestamp(dateFrom).toLocalDateTime())
                .le(dateTo != null, RecycleItemPO::getDeletedAt, new java.sql.Timestamp(dateTo).toLocalDateTime())
                .orderByDesc(RecycleItemPO::getDeletedAt);
        IPage<RecycleItemPO> poPage = mapper.selectPage(new Page<>(current, size), w);
        return poPage.convert(converter::toDomain);
    }

    public List<RecycleItem> findExpired() {
        return mapper.selectList(new LambdaQueryWrapper<RecycleItemPO>()
                .eq(RecycleItemPO::getStatus, "deleted")
                .isNotNull(RecycleItemPO::getExpireAt)
                .lt(RecycleItemPO::getExpireAt, LocalDateTime.now()))
                .stream().map(converter::toDomain).toList();
    }

    public long countByType(String type) {
        return mapper.selectCount(new LambdaQueryWrapper<RecycleItemPO>()
                .eq(RecycleItemPO::getItemType, type)
                .eq(RecycleItemPO::getStatus, "deleted"));
    }

    public long totalRecycled() { return mapper.totalRecycledCount(); }
    public long totalStorageBytes() { return mapper.totalStorageBytes(); }
}
