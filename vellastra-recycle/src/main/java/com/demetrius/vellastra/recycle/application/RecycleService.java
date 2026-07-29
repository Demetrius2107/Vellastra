package com.demetrius.vellastra.recycle.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demetrius.vellastra.recycle.infrastructure.persistence.mapper.RecycleItemMapper;
import com.demetrius.vellastra.recycle.infrastructure.persistence.po.RecycleItemPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class RecycleService {

    private final RecycleItemMapper recycleItemMapper;

    @Value("${recycle.retention-days:30}")
    private int defaultRetentionDays;

    public RecycleService(RecycleItemMapper recycleItemMapper) {
        this.recycleItemMapper = recycleItemMapper;
    }

    // ===================== 回收项目管理 =====================

    @Transactional
    public void add(Long itemId, String itemType, String title, String contentSummary,
                    Long deletedBy, String operator, Integer retentionDays) {
        int days = retentionDays != null ? retentionDays : defaultRetentionDays;
        RecycleItemPO po = new RecycleItemPO();
        po.setItemId(itemId);
        po.setItemType(itemType);
        po.setTitle(title);
        po.setContentSummary(contentSummary);
        po.setDeletedBy(deletedBy);
        po.setOperator(operator);
        po.setStatus(0);
        po.setRetentionDays(days);
        po.setExpireAt(LocalDateTime.now().plusDays(days));
        po.setDeletedAt(LocalDateTime.now());
        po.setCreateTime(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());
        recycleItemMapper.insert(po);
        log.info("已移入回收站: type={}, itemId={}, title={}, expireAt={}", itemType, itemId, title, po.getExpireAt());
    }

    @Transactional
    public void addWithRelated(Long itemId, String itemType, String title,
                                Long deletedBy, String operator) {
        // 主项目
        add(itemId, itemType, title, null, deletedBy, operator, null);

        // 跨模块联动：回收文章时同时回收其评论和标签关系
        if ("article".equals(itemType)) {
            add(null, "comment", title + " 的评论", null, deletedBy, operator, defaultRetentionDays);
            add(null, "article_tag", title + " 的标签", null, deletedBy, operator, defaultRetentionDays);
        }
    }

    // ===================== 查询 =====================

    public IPage<RecycleItemPO> list(int current, int size, String type) {
        LambdaQueryWrapper<RecycleItemPO> wrapper = new LambdaQueryWrapper<RecycleItemPO>()
                .orderByDesc(RecycleItemPO::getDeletedAt);
        if (type != null) wrapper.eq(RecycleItemPO::getItemType, type);
        return recycleItemMapper.selectPage(new Page<>(current, size), wrapper);
    }

    public RecycleItemPO getById(Long id) {
        return recycleItemMapper.selectById(id);
    }

    // ===================== 批量操作 =====================

    @Transactional
    public void restore(Long id) {
        RecycleItemPO po = recycleItemMapper.selectById(id);
        if (po == null) return;
        po.setStatus(1);
        po.setRestoredAt(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());
        recycleItemMapper.updateById(po);
        log.info("已恢复: type={}, itemId={}, title={}", po.getItemType(), po.getItemId(), po.getTitle());
    }

    @Transactional
    public void batchRestore(List<Long> ids) {
        ids.forEach(this::restore);
        log.info("批量恢复完成: count={}", ids.size());
    }

    @Transactional
    public void deletePermanently(Long id) {
        RecycleItemPO po = recycleItemMapper.selectById(id);
        if (po == null) return;
        recycleItemMapper.deleteById(id);
        log.info("已永久删除: type={}, itemId={}, title={}", po.getItemType(), po.getItemId(), po.getTitle());
    }

    @Transactional
    public void batchDeletePermanently(List<Long> ids) {
        ids.forEach(this::deletePermanently);
        log.info("批量永久删除完成: count={}", ids.size());
    }

    // ===================== 自动过期清理 =====================

    @Scheduled(cron = "${recycle.cleanup-cron:0 0 3 * * ?}")
    @Transactional
    public void autoCleanExpired() {
        List<RecycleItemPO> expiredList = recycleItemMapper.selectList(
                new LambdaQueryWrapper<RecycleItemPO>()
                        .eq(RecycleItemPO::getStatus, 0)
                        .isNotNull(RecycleItemPO::getExpireAt)
                        .lt(RecycleItemPO::getExpireAt, LocalDateTime.now()));
        if (expiredList.isEmpty()) {
            log.debug("回收站过期清理: 无过期项目");
            return;
        }
        for (RecycleItemPO po : expiredList) {
            recycleItemMapper.deleteById(po.getId());
        }
        log.info("回收站过期清理完成: 清理 {} 项", expiredList.size());
    }
}
