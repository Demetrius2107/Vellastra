package com.demetrius.vellastra.auth.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>Title: SystemConfigPO</p>
 * <p>Description: 系统配置持久化对象，与 t_system_config 表对应</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-29
 * @updateTime 2026-07-29
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Data
@TableName("t_system_config")
public class SystemConfigPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 配置Key */
    private String configKey;

    /** 配置Value */
    private String configValue;

    /** 配置分组 */
    private String configGroup;

    /** 备注说明 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}