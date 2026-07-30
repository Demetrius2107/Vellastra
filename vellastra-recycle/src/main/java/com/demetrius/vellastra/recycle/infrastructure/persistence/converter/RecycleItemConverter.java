package com.demetrius.vellastra.recycle.infrastructure.persistence.converter;

import com.demetrius.vellastra.recycle.domain.item.entity.RecycleItem;
import com.demetrius.vellastra.recycle.infrastructure.persistence.po.RecycleItemPO;
import org.springframework.stereotype.Component;

@Component
public class RecycleItemConverter {
    public RecycleItem toDomain(RecycleItemPO po) {
        if (po == null) return null;
        return RecycleItem.builder().id(po.getId()).itemId(po.getItemId())
                .itemType(po.getItemType()).title(po.getTitle())
                .contentPath(po.getContentPath()).deletedBy(po.getDeletedBy())
                .operatorName(po.getOperatorName()).retentionDays(po.getRetentionDays())
                .expireAt(po.getExpireAt()).sourceModule(po.getSourceModule())
                .description(po.getDescription()).deletedAt(po.getDeletedAt())
                .restoredAt(po.getRestoredAt()).status(po.getStatus())
                .createTime(po.getCreateTime()).updateTime(po.getUpdateTime()).build();
    }
    public RecycleItemPO toPO(RecycleItem domain) {
        if (domain == null) return null;
        RecycleItemPO po = new RecycleItemPO();
        po.setId(domain.getId()); po.setItemId(domain.getItemId());
        po.setItemType(domain.getItemType()); po.setTitle(domain.getTitle());
        po.setContentPath(domain.getContentPath()); po.setDeletedBy(domain.getDeletedBy());
        po.setOperatorName(domain.getOperatorName()); po.setRetentionDays(domain.getRetentionDays());
        po.setExpireAt(domain.getExpireAt()); po.setSourceModule(domain.getSourceModule());
        po.setDescription(domain.getDescription()); po.setDeletedAt(domain.getDeletedAt());
        po.setRestoredAt(domain.getRestoredAt()); po.setStatus(domain.getStatus());
        po.setCreateTime(domain.getCreateTime()); po.setUpdateTime(domain.getUpdateTime());
        return po;
    }
}
