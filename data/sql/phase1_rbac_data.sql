-- ============================================================
-- RBAC 初始数据（在 phase1_full_schema.sql 基础上补充）
-- 使用 vellastra 数据库
-- ============================================================

USE vellastra;

-- -----------------------------------------------------------
-- 1. 默认角色
-- -----------------------------------------------------------
INSERT INTO `t_role` (`role_name`, `role_code`, `description`, `sort_order`, `status`)
VALUES ('超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', 1, 1),
       ('内容编辑', 'EDITOR', '内容编辑，可管理文章和分类', 2, 1),
       ('访客', 'GUEST', '普通访客，仅可浏览', 3, 1)
ON DUPLICATE KEY UPDATE `role_name` = VALUES(`role_name`);

-- -----------------------------------------------------------
-- 2. 默认菜单（三级树形结构：目录→菜单→按钮）
-- parent_id 按插入顺序关联
-- -----------------------------------------------------------
-- 文章管理
INSERT INTO `t_menu` (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort_order`, `visible`, `status`)
VALUES (0, '文章管理', 1, '/article', 'Layout', NULL, 'document', 1, 1, 1);
INSERT INTO `t_menu` (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort_order`, `visible`, `status`)
VALUES (1, '文章列表', 2, '/article/list', 'article/list', 'article:list', 'list', 1, 1, 1);
INSERT INTO `t_menu` (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort_order`, `visible`, `status`)
VALUES (1, '创建文章', 3, NULL, NULL, 'article:create', NULL, 2, 1, 1);
INSERT INTO `t_menu` (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort_order`, `visible`, `status`)
VALUES (1, '编辑文章', 3, NULL, NULL, 'article:edit', NULL, 3, 1, 1);
INSERT INTO `t_menu` (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort_order`, `visible`, `status`)
VALUES (1, '删除文章', 3, NULL, NULL, 'article:delete', NULL, 4, 1, 1);

-- 分类管理
INSERT INTO `t_menu` (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort_order`, `visible`, `status`)
VALUES (0, '分类管理', 1, '/category', 'Layout', NULL, 'category', 2, 1, 1);
INSERT INTO `t_menu` (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort_order`, `visible`, `status`)
VALUES (6, '分类列表', 2, '/category/list', 'category/list', 'category:list', 'list', 1, 1, 1);
INSERT INTO `t_menu` (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort_order`, `visible`, `status`)
VALUES (6, '创建分类', 3, NULL, NULL, 'category:create', NULL, 2, 1, 1);
INSERT INTO `t_menu` (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort_order`, `visible`, `status`)
VALUES (6, '删除分类', 3, NULL, NULL, 'category:delete', NULL, 3, 1, 1);

-- 评论管理
INSERT INTO `t_menu` (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort_order`, `visible`, `status`)
VALUES (0, '评论管理', 1, '/comment', 'Layout', NULL, 'comment', 3, 1, 1);
INSERT INTO `t_menu` (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort_order`, `visible`, `status`)
VALUES (10, '评论列表', 2, '/comment/list', 'comment/list', 'comment:list', 'list', 1, 1, 1);
INSERT INTO `t_menu` (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort_order`, `visible`, `status`)
VALUES (10, '审核评论', 3, NULL, NULL, 'comment:audit', NULL, 2, 1, 1);

-- 用户管理
INSERT INTO `t_menu` (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort_order`, `visible`, `status`)
VALUES (0, '用户管理', 1, '/user', 'Layout', NULL, 'user', 4, 1, 1);
INSERT INTO `t_menu` (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort_order`, `visible`, `status`)
VALUES (13, '用户列表', 2, '/user/list', 'user/list', 'user:list', 'list', 1, 1, 1);
INSERT INTO `t_menu` (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort_order`, `visible`, `status`)
VALUES (13, '分配角色', 3, NULL, NULL, 'user:assign-role', NULL, 2, 1, 1);

-- 系统管理
INSERT INTO `t_menu` (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort_order`, `visible`, `status`)
VALUES (0, '系统管理', 1, '/system', 'Layout', NULL, 'system', 5, 1, 1);
INSERT INTO `t_menu` (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort_order`, `visible`, `status`)
VALUES (16, '菜单管理', 2, '/system/menu', 'system/menu', 'system:menu', 'tree', 1, 1, 1);
INSERT INTO `t_menu` (`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort_order`, `visible`, `status`)
VALUES (16, '角色管理', 2, '/system/role', 'system/role', 'system:role', 'role', 2, 1, 1);

-- -----------------------------------------------------------
-- 3. 超级管理员（角色ID=1）拥有所有菜单权限
-- -----------------------------------------------------------
INSERT INTO `t_role_menu` (`role_id`, `menu_id`)
SELECT 1, `id` FROM `t_menu`
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);

-- 内容编辑（角色ID=2）拥有文章+分类+评论权限
INSERT INTO `t_role_menu` (`role_id`, `menu_id`)
SELECT 2, `id` FROM `t_menu`
WHERE `perms` IS NOT NULL
  AND (`perms` LIKE 'article:%' OR `perms` LIKE 'category:%' OR `perms` LIKE 'comment:%')
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);

-- -----------------------------------------------------------
-- 4. 给默认管理员（admin）分配超级管理员角色
-- -----------------------------------------------------------
INSERT INTO `t_user_role` (`user_id`, `role_id`)
SELECT `id`, 1 FROM `sys_user` WHERE `username` = 'admin'
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);