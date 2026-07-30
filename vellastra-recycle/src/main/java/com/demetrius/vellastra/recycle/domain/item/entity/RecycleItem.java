package com.demetrius.vellastra.recycle.domain.item.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecycleItem {
    private Long id;
    private Long itemId;
    private String itemType;
    private String title;
    private String contentPath;
    private Long deletedBy;
    private String operatorName;
    private Integer retentionDays;
    private LocalDateTime expireAt;
    private String sourceModule;
    private String description;
    private LocalDateTime deletedAt;
    private LocalDateTime restoredAt;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
