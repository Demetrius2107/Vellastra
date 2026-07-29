package com.demetrius.vellastra.auth.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demetrius.vellastra.auth.infrastructure.persistence.mapper.SystemConfigMapper;
import com.demetrius.vellastra.auth.infrastructure.persistence.po.SystemConfigPO;
import com.demetrius.vellastra.common.exception.BizException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title: SystemConfigService</p>
 * <p>Description: 系统配置服务，提供 KV 配置的读写能力</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-29
 * @updateTime 2026-07-29
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Service
public class SystemConfigService {

    private final SystemConfigMapper systemConfigMapper;

    public SystemConfigService(SystemConfigMapper systemConfigMapper) {
        this.systemConfigMapper = systemConfigMapper;
    }

    /** 根据 key 获取配置值 */
    public String getValue(String key) {
        SystemConfigPO po = systemConfigMapper.selectOne(
                new LambdaQueryWrapper<SystemConfigPO>().eq(SystemConfigPO::getConfigKey, key));
        return po != null ? po.getConfigValue() : null;
    }

    /** 设置配置值 */
    public void setValue(String key, String value) {
        SystemConfigPO po = systemConfigMapper.selectOne(
                new LambdaQueryWrapper<SystemConfigPO>().eq(SystemConfigPO::getConfigKey, key));
        if (po == null) {
            po = new SystemConfigPO();
            po.setConfigKey(key);
            po.setConfigValue(value);
            po.setConfigGroup("default");
            systemConfigMapper.insert(po);
        } else {
            po.setConfigValue(value);
            systemConfigMapper.updateById(po);
        }
    }

    /** 按分组获取所有配置 */
    public List<SystemConfigPO> getByGroup(String group) {
        return systemConfigMapper.selectList(
                new LambdaQueryWrapper<SystemConfigPO>()
                        .eq(SystemConfigPO::getConfigGroup, group));
    }

    /** 获取所有配置（Map 形式） */
    public Map<String, String> getAllAsMap() {
        return systemConfigMapper.selectList(null).stream()
                .collect(Collectors.toMap(SystemConfigPO::getConfigKey, SystemConfigPO::getConfigValue));
    }
}