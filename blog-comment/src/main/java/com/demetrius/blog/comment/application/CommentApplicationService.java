package com.demetrius.blog.comment.application;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demetrius.blog.comment.domain.comment.entity.Comment;
import com.demetrius.blog.comment.domain.comment.repository.CommentRepository;
import com.demetrius.blog.comment.domain.comment.valueobject.CommentStatus;
import com.demetrius.blog.comment.interfaces.dto.*;
import com.demetrius.blog.common.exception.BizException;
import com.demetrius.blog.common.exception.ErrorCode;
import com.demetrius.blog.common.response.PageResult;
import org.springframework.stereotype.Service;

/**
 * <p>Title: CommentApplicationService</p>
 * <p>Description: 评论应用服务，负责评论的增删改查、回复、审核等业务逻辑</p>
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
@Service
public class CommentApplicationService {

    private final CommentRepository commentRepository;

    public CommentApplicationService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /**
     * 分页查询评论列表
     *
     * @param current   页码
     * @param size      每页条数
     * @param articleId 文章ID（可选）
     * @param status    审核状态（可选）
     * @return 分页评论列表
     */
    public PageResult<CommentVO> list(long current, long size, Long articleId, Integer status) {
        Page<Comment> page = commentRepository.findPage(current, size, articleId, status);
        return PageResult.of(
                page.getRecords().stream().map(this::toVO).toList(),
                page.getTotal(), current, size
        );
    }

    /**
     * 创建评论
     *
     * @param request 创建评论请求
     * @param userId  用户ID
     * @return 评论ID
     */
    public Long create(CreateCommentRequest request, Long userId) {
        Comment comment = Comment.builder()
                .articleId(request.getArticleId())
                .userId(userId)
                .content(request.getContent())
                .parentId(0L)
                .status(CommentStatus.PENDING.getCode())
                .likeCount(0)
                .build();
        comment.initCreateTime();
        commentRepository.save(comment);
        return comment.getId();
    }

    /**
     * 回复评论
     *
     * @param request 回复评论请求
     * @param userId  用户ID
     * @return 评论ID
     */
    public Long reply(ReplyCommentRequest request, Long userId) {
        Comment parent = commentRepository.findById(request.getParentId());
        if (parent == null) {
            throw ErrorCode.COMMENT_NOT_FOUND.toException();
        }

        Comment replyTo = commentRepository.findById(request.getReplyToId());
        if (replyTo == null) {
            throw ErrorCode.COMMENT_NOT_FOUND.toException();
        }

        Comment comment = Comment.builder()
                .articleId(request.getArticleId())
                .userId(userId)
                .content(request.getContent())
                .parentId(request.getParentId())
                .replyToId(request.getReplyToId())
                .replyToUserId(replyTo.getUserId())
                .status(CommentStatus.PENDING.getCode())
                .likeCount(0)
                .build();
        comment.initCreateTime();
        commentRepository.save(comment);
        return comment.getId();
    }

    /**
     * 删除评论
     *
     * @param id 评论ID
     */
    public void delete(Long id) {
        Comment comment = commentRepository.findById(id);
        if (comment == null) {
            throw ErrorCode.COMMENT_NOT_FOUND.toException();
        }
        commentRepository.delete(id);
    }

    /**
     * 审核评论
     *
     * @param id     评论ID
     * @param status 目标状态（1-通过 2-拒绝）
     */
    public void audit(Long id, Integer status) {
        Comment comment = commentRepository.findById(id);
        if (comment == null) {
            throw ErrorCode.COMMENT_NOT_FOUND.toException();
        }
        comment.setStatus(status);
        comment.updateTime();
        commentRepository.save(comment);
    }

    /**
     * 领域对象转视图对象
     *
     * @param c 评论领域对象
     * @return 评论视图对象
     */
    private CommentVO toVO(Comment c) {
        CommentVO vo = new CommentVO();
        vo.setId(c.getId());
        vo.setArticleId(c.getArticleId());
        vo.setUserId(c.getUserId());
        vo.setParentId(c.getParentId() != null && c.getParentId() > 0 ? c.getParentId() : null);
        vo.setReplyToId(c.getReplyToId());
        vo.setReplyToUserId(c.getReplyToUserId());
        vo.setContent(c.getContent());
        vo.setStatus(c.getStatus());
        vo.setLikeCount(c.getLikeCount());
        vo.setCreateTime(c.getCreateTime());
        return vo;
    }
}
