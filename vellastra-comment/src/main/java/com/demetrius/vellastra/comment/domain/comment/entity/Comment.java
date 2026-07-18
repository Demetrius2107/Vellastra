package com.demetrius.vellastra.comment.domain.comment.entity;

import com.demetrius.vellastra.comment.domain.comment.valueobject.CommentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <p>Title: Comment</p>
 * <p>Description: 评论领域实体，与 blog_comment 表对应</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
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

    /** 评论用户ID */
    private Long userId;

    /** 父评论ID（0=顶层评论） */
    private Long parentId;

    /** 回复目标用户ID */
    private Long replyUserId;

    /** 评论内容 */
    private String content;

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
