package com.demetrius.vellastra.article.interfaces.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>Title: ArticleVO</p>
 * <p>Description: 文章视图对象</p>
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
@Data
public class ArticleVO {

    private Long id;
    private String title;
    private String content;
    private String summary;
    private String coverImage;
    private Long categoryId;
    private String categoryName;
    private Integer status;
    private String tags;
    private Long authorId;
    private String authorName;
    private Long viewCount;
    private Long likeCount;
    private Integer isTop;
    private Integer commentCount;
    private LocalDateTime publishTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
