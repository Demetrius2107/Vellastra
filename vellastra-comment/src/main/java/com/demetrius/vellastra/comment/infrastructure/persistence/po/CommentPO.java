package com.demetrius.vellastra.comment.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>Title: CommentPO</p>
 * <p>Description: 评论持久化对象，与 blog_comment 表 1:1 对应</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
 */
@Data
@TableName("blog_comment")
public class CommentPO {

    /** 评论ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 文章ID */
    private Long articleId;

    /** 评论用户ID */
    private Long userId;

    /** 父评论ID（0为一级评论） */
    private Long parentId;

    /** 回复目标用户ID */
    private Long replyUserId;

    /** 评论内容 */
    private String content;

    /** 状态：0待审核 1已通过 2驳回 */
    private Integer status;

    /** 点赞数 */
    private Integer likeCount;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
