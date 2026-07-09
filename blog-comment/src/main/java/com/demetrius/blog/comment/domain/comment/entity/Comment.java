package com.demetrius.blog.comment.domain.comment.entity;

import com.demetrius.blog.comment.domain.comment.valueobject.CommentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <p>Title: Comment</p>
 * <p>Description: 评论领域实体</p>
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    /** 评论ID */
    private Long id;
    /** 文章ID */
    private Long articleId;
    /** 评论者用户ID */
    private Long userId;
    /** 父评论ID（0=顶层评论） */
    private Long parentId;
    /** 被回复评论ID */
    private Long replyToId;
    /** 被回复者用户ID */
    private Long replyToUserId;
    /** 评论内容 */
    private String content;
    /** 评论者IP */
    private String ipAddress;
    /** 状态：0待审核 1已通过 2已拒绝 */
    private Integer status;
    /** 点赞数 */
    private Integer likeCount;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;

    /**
     * 初始化创建和更新时间
     */
    public void initCreateTime() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新时间
     */
    public void updateTime() {
        this.updateTime = LocalDateTime.now();
    }
}
