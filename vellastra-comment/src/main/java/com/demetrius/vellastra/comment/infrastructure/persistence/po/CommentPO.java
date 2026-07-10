package com.demetrius.vellastra.comment.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>Title: CommentPO</p>
 * <p>Description: 评论持久化对象</p>
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
@TableName("t_comment")
public class CommentPO {

    /** 评论ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 文章ID */
    private Long articleId;
    /** 评论者用户ID */
    private Long userId;
    /** 父评论ID */
    private Long parentId;
    /** 被回复评论ID */
    private Long replyToId;
    /** 被回复者用户ID */
    private Long replyToUserId;
    /** 评论内容 */
    private String content;
    /** 评论者IP */
    private String ipAddress;
    /** 浏览器UA */
    private String userAgent;
    /** 状态：0待审核 1已通过 2已拒绝 */
    private Integer status;
    /** 点赞数 */
    private Integer likeCount;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
