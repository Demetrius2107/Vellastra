package com.demetrius.vellastra.recycle.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demetrius.vellastra.recycle.infrastructure.persistence.mapper.RecycleItemMapper;
import com.demetrius.vellastra.recycle.infrastructure.persistence.po.RecycleItemPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class RecycleService {
    private final RecycleItemMapper recycleItemMapper;
    public RecycleService(RecycleItemMapper recycleItemMapper) { this.recycleItemMapper = recycleItemMapper; }

    public IPage<RecycleItemPO> list(int current, int size, String type) {
        LambdaQueryWrapper<RecycleItemPO> wrapper = new LambdaQueryWrapper<RecycleItemPO>()
                .orderByDesc(RecycleItemPO::getDeletedAt);
        if (type != null) wrapper.eq(RecycleItemPO::getItemType, type);
        return recycleItemMapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Transactional
    public void add(Long itemId, String itemType, String title, String operator) {
        RecycleItemPO po = new RecycleItemPO();
        po.setItemId(itemId); po.setItemType(itemType); po.setTitle(title);
        po.setOperator(operator); po.setDeletedAt(LocalDateTime.now());
        recycleItemMapper.insert(po);
    }

    @Transactional
    public void restore(Long id) {
        recycleItemMapper.deleteById(id);
    }

    @Transactional
    public void deletePermanently(Long id) {
        recycleItemMapper.deleteById(id);
    }
}
