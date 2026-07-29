package com.demetrius.vellastra.auth.interfaces.facade;

import com.demetrius.vellastra.auth.application.SystemConfigService;
import com.demetrius.vellastra.auth.application.FriendLinkService;
import com.demetrius.vellastra.auth.infrastructure.persistence.po.FriendLinkPO;
import com.demetrius.vellastra.common.response.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>Title: SystemController</p>
 * <p>Description: 系统管理控制器，提供系统配置、友情链接等接口</p>
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
@RequestMapping("/system")
public class SystemController {

    private final SystemConfigService systemConfigService;
    private final FriendLinkService friendLinkService;

    public SystemController(SystemConfigService systemConfigService, FriendLinkService friendLinkService) {
        this.systemConfigService = systemConfigService;
        this.friendLinkService = friendLinkService;
    }

    // ====================== 系统配置 ======================

    /** 获取所有配置 */
    @GetMapping("/config")
    public Result<Map<String, String>> getAllConfig() {
        return Result.success(systemConfigService.getAllAsMap());
    }

    /** 根据 key 获取配置 */
    @GetMapping("/config/{key}")
    public Result<String> getConfig(@PathVariable String key) {
        return Result.success(systemConfigService.getValue(key));
    }

    /** 设置配置 */
    @PutMapping("/config/{key}")
    public Result<Void> setConfig(@PathVariable String key, @RequestBody String value) {
        systemConfigService.setValue(key, value);
        return Result.success();
    }

    // ====================== 友情链接 ======================

    /** 获取所有友情链接 */
    @GetMapping("/friend-link")
    public Result<List<FriendLinkPO>> listFriendLinks() {
        return Result.success(friendLinkService.listAll());
    }

    /** 新增友情链接 */
    @PostMapping("/friend-link")
    public Result<Long> createFriendLink(@RequestBody FriendLinkPO request) {
        return Result.success(friendLinkService.create(
                request.getName(), request.getUrl(),
                request.getDescription(), request.getSortOrder()));
    }

    /** 更新友情链接 */
    @PutMapping("/friend-link/{id}")
    public Result<Void> updateFriendLink(@PathVariable Long id, @RequestBody FriendLinkPO request) {
        friendLinkService.update(id, request.getName(), request.getUrl(),
                request.getDescription(), request.getSortOrder());
        return Result.success();
    }

    /** 删除友情链接 */
    @DeleteMapping("/friend-link/{id}")
    public Result<Void> deleteFriendLink(@PathVariable Long id) {
        friendLinkService.delete(id);
        return Result.success();
    }
}