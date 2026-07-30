package com.demetrius.vellastra.recycle.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.demetrius.vellastra.recycle.domain.item.entity.RecycleItem;
import com.demetrius.vellastra.recycle.domain.item.repository.RecycleItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RecycleService {

    private final RecycleItemRepository repository;

    @Value("${recycle.retention-days-article:30}")
    private int retentionDaysArticle;

    @Value("${recycle.retention-days-comment:15}")
    private int retentionDaysComment;

    @Value("${recycle.retention-days-file:7}")
    private int retentionDaysFile;

    public RecycleService(RecycleItemRepository repository) {
        this.repository = repository;
    }

    /** 放入回收站（支持级联联动） */
    @Transactional
    public void recycle(Long itemId, String itemType, String title, String contentPath,
                        Long deletedBy, String operatorName, String sourceModule) {
        int days = getRetentionDays(itemType);
        RecycleItem item = RecycleItem.builder()
                .itemId(itemId).itemType(itemType).title(title)
                .contentPath(contentPath).deletedBy(deletedBy).operatorName(operatorName)
                .retentionDays(days).expireAt(LocalDateTime.now().plusDays(days))
                .sourceModule(sourceModule).status("deleted")
                .deletedAt(LocalDateTime.now()).createTime(LocalDateTime.now())
                .build();
        repository.save(item);
        log.info("回收: type={}, id={}, title={}, {}天后永久删除", itemType, itemId, title, days);

        // 级联联动：回收文章时连带回收评论和点赞记录
        if ("article".equals(itemType)) {
            recycle(null, "article_comment", title + " 的评论", null, deletedBy, operatorName, sourceModule);
            recycle(null, "article_like", title + " 的点赞", null, deletedBy, operatorName, sourceModule);
        }
    }

    /** 批量回收 */
    @Transactional
    public void batchRecycle(List<Long> itemIds, String itemType, String title,
                             Long deletedBy, String operatorName, String sourceModule) {
        for (int i = 0; i < itemIds.size(); i++) {
            recycle(itemIds.get(i), itemType, title + "_" + (i + 1), null,
                    deletedBy, operatorName, sourceModule);
        }
        log.info("批量回收: type={}, count={}", itemType, itemIds.size());
    }

    // ===================== 恢复 =====================

    @Transactional
    public void restore(Long id) {
        RecycleItem item = repository.findById(id);
        if (item == null) throw new RuntimeException("回收站记录不存在");
        item.setStatus("restored");
        item.setRestoredAt(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        repository.save(item);
        log.info("恢复: type={}, id={}, title={}", item.getItemType(), item.getItemId(), item.getTitle());
    }

    @Transactional
    public void batchRestore(List<Long> ids) {
        ids.forEach(this::restore);
        log.info("批量恢复完成: count={}", ids.size());
    }

    // ===================== 永久删除 =====================

    @Transactional
    public void deletePermanently(Long id) {
        RecycleItem item = repository.findById(id);
        if (item == null) return;
        repository.delete(id);
        log.info("永久删除: type={}, id={}, title={}", item.getItemType(), item.getItemId(), item.getTitle());
    }

    @Transactional
    public void batchDeletePermanently(List<Long> ids) {
        ids.forEach(this::deletePermanently);
        log.info("批量永久删除完成: count={}", ids.size());
    }

    @Transactional
    public void emptyRecycleBin() {
        List<RecycleItem> all = repository.findPage(1, 10000, null, null, null, null).getRecords();
        all.forEach(i -> repository.delete(i.getId()));
        log.warn("清空回收站: count={}", all.size());
    }

    // ===================== 查询 =====================

    public IPage<RecycleItem> list(int current, int size, String type, String keyword,
                                    Long dateFrom, Long dateTo) {
        return repository.findPage(current, size, type, keyword, dateFrom, dateTo);
    }

    public RecycleItem getById(Long id) { return repository.findById(id); }

    // ===================== 统计 =====================

    public Map<String, Object> stats() {
        return Map.of(
                "totalRecycled", repository.totalRecycled(),
                "storageBytes", repository.totalStorageBytes(),
                "articles", repository.countByType("article"),
                "comments", repository.countByType("comment"),
                "files", repository.countByType("file"),
                "articleComments", repository.countByType("article_comment"),
                "articleLikes", repository.countByType("article_like"));
    }

    // ===================== 自动过期清理 =====================

    @Scheduled(cron = "${recycle.cleanup-cron:0 0 3 * * ?}")
    @Transactional
    public void autoCleanExpired() {
        List<RecycleItem> expired = repository.findExpired();
        if (expired.isEmpty()) { log.debug("过期清理: 无过期项目"); return; }
        for (RecycleItem item : expired) {
            repository.delete(item.getId());
            log.info("过期自动清理: type={}, id={}, title={}", item.getItemType(), item.getItemId(), item.getTitle());
        }
        log.info("过期清理完成: 清理 {} 项", expired.size());
    }

    // ===================== 内部 =====================

    private int getRetentionDays(String type) {
        return switch (type) {
            case "article" -> retentionDaysArticle;
            case "comment", "article_comment", "article_like" -> retentionDaysComment;
            case "file" -> retentionDaysFile;
            default -> 30;
        };
    }
}
