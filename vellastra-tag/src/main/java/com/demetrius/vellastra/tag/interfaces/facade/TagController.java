package com.demetrius.vellastra.tag.interfaces.facade;

import com.demetrius.vellastra.common.response.Result;
import com.demetrius.vellastra.tag.application.TagApplicationService;
import com.demetrius.vellastra.tag.interfaces.dto.CreateTagRequest;
import com.demetrius.vellastra.tag.interfaces.dto.TagVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>Title: TagController</p>
 * <p>Description: 标签管理控制器，提供标签 CRUD、热门标签等接口</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-29
 * @updateTime 2026-07-29
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@RestController
@RequestMapping("/tag")
public class TagController {

    private final TagApplicationService tagApplicationService;

    public TagController(TagApplicationService tagApplicationService) {
        this.tagApplicationService = tagApplicationService;
    }

    /** 获取所有标签 */
    @GetMapping
    public Result<List<TagVO>> listAll() {
        return Result.success(tagApplicationService.listAll());
    }

    /** 获取热门标签 */
    @GetMapping("/hot")
    public Result<List<TagVO>> getHotTags(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(tagApplicationService.getHotTags(limit));
    }

    /** 获取标签详情 */
    @GetMapping("/{id}")
    public Result<TagVO> getById(@PathVariable Long id) {
        return Result.success(tagApplicationService.getById(id));
    }

    /** 创建标签 */
    @PostMapping
    public Result<Long> create(@RequestBody CreateTagRequest request) {
        return Result.success(tagApplicationService.create(request.getName(), request.getSlug()));
    }

    /** 更新标签 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody CreateTagRequest request) {
        tagApplicationService.update(id, request.getName(), request.getSlug());
        return Result.success();
    }

    /** 删除标签 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        tagApplicationService.delete(id);
        return Result.success();
    }
}