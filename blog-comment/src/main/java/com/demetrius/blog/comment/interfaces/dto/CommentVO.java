package com.demetrius.blog.comment.interfaces.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>Title: CommentVO</p>
 * <p>Description: 评论视图对象</p>
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
public class CommentVO {

    /** 评论ID */
    private Long id;
    /** 文章ID */
    private Long articleId;
    /** 评论者用户ID */
    private Long userId;
    /** 评论者用户名 */
    private String username;
    /** 评论者头像 */
    private String userAvatar;
    /** 父评论ID */
    private Long parentId;
    /** 被回复评论ID */
    private Long replyToId;
    /** 被回复者用户ID */
    private Long replyToUserId;
    /** 被回复者用户名 */
    private String replyToUsername;
    /** 评论内容 */
    private String content;
    /** 状态：0待审核 1已通过 2已拒绝 */
    private Integer status;
    /** 点赞数 */
    private Integer likeCount;
    /** 子评论列表 */
    private List<CommentVO> children;
    /** 创建时间 */
    private LocalDateTime createTime;
}
