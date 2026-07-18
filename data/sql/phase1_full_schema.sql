-- ============================================================
-- 星垂野内容系统（Vellastra Content System）— 阶段一全量 DDL
-- 数据库: vellastra
-- 字符集: utf8mb4 / utf8mb4_unicode_ci
-- 引擎:   InnoDB
-- 版本:   1.0
-- 日期:   2026-07-18
-- ============================================================

-- ============================================================
-- 创建数据库
-- ============================================================
CREATE DATABASE IF NOT EXISTS `vellastra`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `vellastra`;

-- ============================================================
-- 1.1.1 核心业务表
-- ============================================================

-- -----------------------------------------------------------
-- 1. sys_user（用户表）
-- 存储系统用户，含登录认证与基础信息
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`        VARCHAR(32)  NOT NULL COMMENT '用户名，唯一索引',
    `password`        VARCHAR(128) NOT NULL COMMENT 'BCrypt 加密密码',
    `nickname`        VARCHAR(32)  DEFAULT NULL COMMENT '昵称',
    `avatar`          VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
    `role`            TINYINT      NOT NULL COMMENT '角色：1超级管理员 2撰稿人 3访客',
    `email`           VARCHAR(64)  DEFAULT NULL COMMENT '邮箱',
    `status`          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1正常 0禁用',
    `last_login_time` DATETIME     DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip`   VARCHAR(32)  DEFAULT NULL COMMENT '最后登录IP',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_status` (`status`),
    KEY `idx_role` (`role`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户表';

-- -----------------------------------------------------------
-- 2. blog_article（文章表）
-- 核心内容实体，含 SEO 与统计字段
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `blog_article`;
CREATE TABLE `blog_article`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title`           VARCHAR(128) NOT NULL COMMENT '文章标题',
    `summary`         VARCHAR(255) DEFAULT NULL COMMENT '摘要',
    `content`         LONGTEXT     NOT NULL COMMENT '正文内容（Markdown）',
    `content_html`    LONGTEXT     NOT NULL COMMENT '渲染后 HTML',
    `cover_image`     VARCHAR(255) DEFAULT NULL COMMENT '封面图',
    `category_id`     BIGINT       DEFAULT NULL COMMENT '分类ID',
    `status`          TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0草稿 1待审核 2已发布 3下架',
    `is_top`          TINYINT      NOT NULL DEFAULT 0 COMMENT '是否置顶：0否 1是',
    `view_count`      INT          NOT NULL DEFAULT 0 COMMENT '浏览量，默认0',
    `like_count`      INT          NOT NULL DEFAULT 0 COMMENT '点赞数，默认0',
    `comment_count`   INT          NOT NULL DEFAULT 0 COMMENT '评论数，默认0',
    `publish_time`    DATETIME     DEFAULT NULL COMMENT '发布时间',
    `seo_title`       VARCHAR(128) DEFAULT NULL COMMENT 'SEO标题',
    `seo_description` VARCHAR(255) DEFAULT NULL COMMENT 'SEO描述',
    `seo_keywords`    VARCHAR(255) DEFAULT NULL COMMENT 'SEO关键词',
    `author_id`       BIGINT       NOT NULL COMMENT '作者用户ID',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    FULLTEXT KEY `ft_title_content` (`title`, `content`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_author_id` (`author_id`),
    KEY `idx_status` (`status`),
    KEY `idx_is_top` (`is_top`),
    KEY `idx_publish_time` (`publish_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='文章表';

-- -----------------------------------------------------------
-- 3. blog_category（分类表）
-- 支持三级树形结构
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `blog_category`;
CREATE TABLE `blog_category`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`        VARCHAR(32)  NOT NULL COMMENT '分类名称，唯一索引',
    `parent_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '父分类ID，0为一级分类',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序权重，越小越靠前',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '分类描述',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_sort` (`sort`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='分类表';

-- -----------------------------------------------------------
-- 4. blog_tag（标签表）
-- 独立标签，与文章多对多关联
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `blog_tag`;
CREATE TABLE `blog_tag`
(
    `id`        BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`      VARCHAR(32) NOT NULL COMMENT '标签名称，唯一索引',
    `use_count` INT         NOT NULL DEFAULT 0 COMMENT '使用次数',
    `create_time` DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`),
    KEY `idx_use_count` (`use_count`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='标签表';

-- -----------------------------------------------------------
-- 5. blog_article_tag（文章标签关联表）
-- 实现文章与标签的多对多关系
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `blog_article_tag`;
CREATE TABLE `blog_article_tag`
(
    `id`         BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `article_id` BIGINT NOT NULL COMMENT '文章ID',
    `tag_id`     BIGINT NOT NULL COMMENT '标签ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_tag` (`article_id`, `tag_id`),
    KEY `idx_tag_id` (`tag_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='文章标签关联表';

-- -----------------------------------------------------------
-- 6. blog_comment（评论表）
-- 支持楼中楼回复，含审核状态机
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `blog_comment`;
CREATE TABLE `blog_comment`
(
    `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `article_id`    BIGINT        NOT NULL COMMENT '文章ID',
    `user_id`       BIGINT        NOT NULL COMMENT '评论用户ID',
    `parent_id`     BIGINT        NOT NULL DEFAULT 0 COMMENT '父评论ID，0为一级评论',
    `reply_user_id` BIGINT        DEFAULT NULL COMMENT '回复目标用户ID',
    `content`       VARCHAR(1024) NOT NULL COMMENT '评论内容',
    `status`        TINYINT       NOT NULL DEFAULT 0 COMMENT '状态：0待审核 1已通过 2驳回',
    `like_count`    INT           NOT NULL DEFAULT 0 COMMENT '点赞数',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_article_id` (`article_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='评论表';

-- -----------------------------------------------------------
-- 7. sys_media（媒体资源表）
-- 管理上传的图片、视频、文档等文件资源
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `sys_media`;
CREATE TABLE `sys_media`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`      BIGINT       NOT NULL COMMENT '上传用户ID',
    `file_name`    VARCHAR(128) NOT NULL COMMENT '原始文件名',
    `file_path`    VARCHAR(255) NOT NULL COMMENT '文件存储路径',
    `file_size`    BIGINT       NOT NULL COMMENT '文件大小（字节）',
    `file_type`    VARCHAR(32)  NOT NULL COMMENT '文件类型：image/video/document',
    `mime_type`    VARCHAR(64)  NOT NULL COMMENT 'MIME类型',
    `storage_type` TINYINT      NOT NULL DEFAULT 1 COMMENT '存储方式：1本地存储 2MinIO 3OSS',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_file_type` (`file_type`),
    KEY `idx_storage_type` (`storage_type`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='媒体资源表';

-- ============================================================
-- 1.1.2 星垂野联邦专属表
-- ============================================================

-- -----------------------------------------------------------
-- 8. fed_node（联邦节点表）
-- 星垂野联邦体系中的节点注册与心跳管理
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `fed_node`;
CREATE TABLE `fed_node`
(
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `node_name`      VARCHAR(64)  NOT NULL COMMENT '节点名称',
    `node_domain`    VARCHAR(128) NOT NULL COMMENT '节点域名，唯一索引',
    `node_type`      TINYINT      NOT NULL COMMENT '节点类型：1中心节点 2子节点',
    `public_key`     TEXT         NOT NULL COMMENT '节点公钥',
    `status`         TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0待审核 1正常 2离线 3禁用',
    `last_heartbeat` DATETIME     DEFAULT NULL COMMENT '最后心跳时间',
    `sync_scope`     TINYINT      NOT NULL DEFAULT 1 COMMENT '同步范围：1不同步 2全量同步 3仅标签同步',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_node_domain` (`node_domain`),
    KEY `idx_status` (`status`),
    KEY `idx_node_type` (`node_type`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='联邦节点表';

-- -----------------------------------------------------------
-- 9. fed_sync_record（同步记录表）
-- 联邦节点间数据同步的幂等记录
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `fed_sync_record`;
CREATE TABLE `fed_sync_record`
(
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `source_node_id` BIGINT       NOT NULL COMMENT '源节点ID',
    `target_node_id` BIGINT       NOT NULL COMMENT '目标节点ID',
    `biz_type`       TINYINT      NOT NULL COMMENT '业务类型：1文章 2评论 3用户',
    `biz_id`         BIGINT       NOT NULL COMMENT '业务数据ID',
    `sync_status`    TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0待同步 1成功 2失败',
    `retry_count`    INT          NOT NULL DEFAULT 0 COMMENT '重试次数',
    `error_msg`      VARCHAR(255) DEFAULT NULL COMMENT '失败原因',
    `request_id`     VARCHAR(64)  NOT NULL COMMENT '幂等请求ID，唯一索引',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_request_id` (`request_id`),
    KEY `idx_source_node` (`source_node_id`),
    KEY `idx_target_node` (`target_node_id`),
    KEY `idx_sync_status` (`sync_status`),
    KEY `idx_biz_type_biz_id` (`biz_type`, `biz_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='同步记录表';

-- ============================================================
-- 1.1.3 系统基础表
-- ============================================================

-- -----------------------------------------------------------
-- 10. sys_operate_log（操作日志表）
-- 记录所有用户操作行为，由 AOP 切面自动写入
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `sys_operate_log`;
CREATE TABLE `sys_operate_log`
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

-- -----------------------------------------------------------
-- 11. sys_login_log（登录日志表）
-- 记录每次登录尝试（成功/失败），用于安全审计
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`    VARCHAR(32)  NOT NULL COMMENT '登录用户名',
    `ip`          VARCHAR(32)  DEFAULT NULL COMMENT '登录IP',
    `user_agent`  VARCHAR(500) DEFAULT NULL COMMENT '浏览器UA',
    `browser`     VARCHAR(64)  DEFAULT NULL COMMENT '浏览器名称',
    `os`          VARCHAR(64)  DEFAULT NULL COMMENT '操作系统',
    `status`      TINYINT      NOT NULL COMMENT '登录状态：0失败 1成功',
    `message`     VARCHAR(255) DEFAULT NULL COMMENT '提示信息（失败原因）',
    `login_time`  DATETIME     NOT NULL COMMENT '登录时间',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`),
    KEY `idx_status` (`status`),
    KEY `idx_login_time` (`login_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='登录日志表';

-- -----------------------------------------------------------
-- 12. sys_config（系统配置表）
-- 存储站点基本信息、SEO、社交链接等键值对配置
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `config_key`   VARCHAR(64)  NOT NULL COMMENT '配置键名',
    `config_value` TEXT         NOT NULL COMMENT '配置值',
    `config_group` VARCHAR(32)  NOT NULL DEFAULT 'site' COMMENT '配置分组：site/seo/social/upload',
    `description`  VARCHAR(255) DEFAULT NULL COMMENT '配置描述',
    `status`       TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
    `sort_order`   INT          NOT NULL DEFAULT 0 COMMENT '排序权重',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`),
    KEY `idx_config_group` (`config_group`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='系统配置表';

-- ============================================================
-- 初始数据
-- ============================================================

-- -----------------------------------------------------------
-- 默认管理员账户（密码: admin123，BCrypt 加密）
-- -----------------------------------------------------------
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `role`, `email`, `status`)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        '系统管理员', 1, 'admin@vellastra.com', 1)
ON DUPLICATE KEY UPDATE `nickname` = VALUES(`nickname`);

-- -----------------------------------------------------------
-- 默认站点配置
-- -----------------------------------------------------------
INSERT INTO `sys_config` (`config_key`, `config_value`, `config_group`, `description`, `sort_order`)
VALUES ('site_name', '星垂野内容系统', 'site', '站点名称', 1),
       ('site_description', '一个渐进式的内容管理系统', 'site', '站点描述', 2),
       ('site_keywords', '博客,内容管理,星垂野', 'seo', 'SEO关键词', 3),
       ('site_icp', '', 'site', 'ICP备案号', 4)
ON DUPLICATE KEY UPDATE `config_value` = VALUES(`config_value`);