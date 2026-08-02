package com.demetrius.vellastra.column.domain.column.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Column {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String coverImage;
    private Long authorId;
    private String authorName;
    private String status;
    private Integer articleCount;
    private Integer sortOrder;
    private Boolean isFeatured;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public void initCreateTime() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    public void updateTime() { this.updateTime = LocalDateTime.now(); }
}
