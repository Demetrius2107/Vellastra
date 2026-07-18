-- ============================================================
-- 1.1.3 系统基础表
-- vellastra 数据库，阶段一
-- ============================================================

-- ============================================================
-- 10. sys_operate_log（操作日志表）
-- 记录所有用户操作行为，由 AOP 切面自动写入
-- ============================================================

USE Vellastra;
CREATE TABLE IF NOT EXISTS `sys_operate_log`
(
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `module`         VARCHAR(32)  NOT NULL COMMENT '操作模块：user/article/category/comment/system',
    `operation`      VARCHAR(64)  NOT NULL COMMENT '操作类型：create/update/delete/audit/export',
    `biz_type`       VARCHAR(32)  DEFAULT NULL COMMENT '业务类型：User/Article/Category/Comment',
    `biz_id`         BIGINT       DEFAULT NULL COMMENT '业务数据ID',
    `request_url`    VARCHAR(255) DEFAULT NULL COMMENT '请求URL',
    `request_method` VARCHAR(10)  DEFAULT NULL COMMENT '请求方式：GET/POST/PUT/DELETE',
    `request_params` TEXT         DEFAULT NULL COMMENT '请求参数（JSON）',
    `response_data`  TEXT         DEFAULT NULL COMMENT '响应数据（JSON）',
    `status`         TINYINT      NOT NULL DEFAULT 1 COMMENT '操作状态：0失败 1成功',
    `cost_time`      BIGINT       DEFAULT NULL COMMENT '耗时（毫秒）',
    `user_id`        BIGINT       DEFAULT NULL COMMENT '操作用户ID',
    `username`       VARCHAR(32)  DEFAULT NULL COMMENT '操作用户名',
    `ip`             VARCHAR(32)  DEFAULT NULL COMMENT '操作IP',
    `user_agent`     VARCHAR(500) DEFAULT NULL COMMENT '浏览器UA',
    `error_msg`      TEXT         DEFAULT NULL COMMENT '错误信息',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_module` (`module`),
    KEY `idx_biz_type_biz_id` (`biz_type`, `biz_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='操作日志表';

-- ============================================================
-- 11. sys_login_log（登录日志表）
-- 记录每次登录尝试（成功/失败），用于安全审计
-- ============================================================
CREATE TABLE IF NOT EXISTS `sys_login_log`
(
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`    VARCHAR(32) NOT NULL COMMENT '登录用户名',
    `ip`          VARCHAR(32) DEFAULT NULL COMMENT '登录IP',
    `user_agent`  VARCHAR(500) DEFAULT NULL COMMENT '浏览器UA',
    `browser`     VARCHAR(64) DEFAULT NULL COMMENT '浏览器名称',
    `os`          VARCHAR(64) DEFAULT NULL COMMENT '操作系统',
    `status`      TINYINT     NOT NULL COMMENT '登录状态：0失败 1成功',
    `message`     VARCHAR(255) DEFAULT NULL COMMENT '提示信息（失败原因）',
    `login_time`  DATETIME    NOT NULL COMMENT '登录时间',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`),
    KEY `idx_status` (`status`),
    KEY `idx_login_time` (`login_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='登录日志表';

-- ============================================================
-- 12. sys_config（系统配置表）
-- 存储站点基本信息、SEO、社交链接等键值对配置
-- ============================================================
CREATE TABLE IF NOT EXISTS `sys_config`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `config_key`  VARCHAR(64)  NOT NULL COMMENT '配置键名',
    `config_value` TEXT        NOT NULL COMMENT '配置值',
    `config_group` VARCHAR(32) NOT NULL DEFAULT 'site' COMMENT '配置分组：site/seo/social/upload',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '配置描述',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
    `sort_order`  INT          NOT NULL DEFAULT 0 COMMENT '排序权重',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`),
    KEY `idx_config_group` (`config_group`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='系统配置表';