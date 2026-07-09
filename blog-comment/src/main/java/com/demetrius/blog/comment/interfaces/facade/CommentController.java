package com.demetrius.blog.comment.interfaces.facade;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demetrius.blog.comment.application.CommentApplicationService;
import com.demetrius.blog.comment.interfaces.dto.*;
import com.demetrius.blog.common.response.PageResult;
import com.demetrius.blog.common.response.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * <p>Title: CommentController</p>
 * <p>Description: 评论控制器，处理评论的增删改查和审核</p>
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
@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentApplicationService commentApplicationService;

    public CommentController(CommentApplicationService commentApplicationService) {
        this.commentApplicationService = commentApplicationService;
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
    @GetMapping
    public Result<PageResult<CommentVO>> list(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) Long articleId,
            @RequestParam(required = false) Integer status) {
        return Result.success(commentApplicationService.list(current, size, articleId, status));
    }

    /**
     * 创建评论
     *
     * @param request 创建评论请求
     * @param userId  当前登录用户ID
     * @return 评论ID
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody CreateCommentRequest request,
                               @RequestHeader("X-User-Id") Long userId) {
        return Result.success(commentApplicationService.create(request, userId));
    }

    /**
     * 回复评论
     *
     * @param request 回复评论请求
     * @param userId  当前登录用户ID
     * @return 评论ID
     */
    @PostMapping("/reply")
    public Result<Long> reply(@Valid @RequestBody ReplyCommentRequest request,
                              @RequestHeader("X-User-Id") Long userId) {
        return Result.success(commentApplicationService.reply(request, userId));
    }

    /**
     * 删除评论
     *
     * @param id 评论ID
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        commentApplicationService.delete(id);
        return Result.success();
    }

    /**
     * 审核评论
     *
     * @param id     评论ID
     * @param status 目标状态（1-通过 2-拒绝）
     */
    @PatchMapping("/{id}/audit")
    public Result<Void> audit(@PathVariable Long id, @RequestParam Integer status) {
        commentApplicationService.audit(id, status);
        return Result.success();
    }
}
