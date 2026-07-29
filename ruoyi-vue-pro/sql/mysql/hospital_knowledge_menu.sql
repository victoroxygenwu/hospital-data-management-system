-- =============================================
-- 模块一：知识图谱菜单与超级管理员授权
-- 菜单 id 段 7000-7024，挂在医院管理(5100)下
-- 执行后请重启后端或 redis-cli flushdb
-- =============================================

SET NAMES utf8mb4;
USE hospital;

DELETE FROM system_role_menu WHERE menu_id BETWEEN 7000 AND 7024;
DELETE FROM system_menu WHERE id BETWEEN 7000 AND 7024;

-- 一级目录：知识图谱
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (7000, '知识图谱', '', 1, 50, 5100, 'knowledge', 'ep:connection', NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');

-- 疾病字典
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(7001, '疾病字典', 'hospital:disease:query', 2, 1, 7000, 'disease', 'ep:first-aid-kit', 'hospital/disease/index', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(7007, '疾病查询', 'hospital:disease:query', 3, 1, 7001, '', NULL, NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(7008, '疾病创建', 'hospital:disease:create', 3, 2, 7001, '', NULL, NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(7009, '疾病修改', 'hospital:disease:update', 3, 3, 7001, '', NULL, NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(7010, '疾病删除', 'hospital:disease:delete', 3, 4, 7001, '', NULL, NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');

-- 症状字典
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(7002, '症状字典', 'hospital:symptom:query', 2, 2, 7000, 'symptom', 'ep:warning', 'hospital/symptom/index', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(7011, '症状查询', 'hospital:symptom:query', 3, 1, 7002, '', NULL, NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(7012, '症状创建', 'hospital:symptom:create', 3, 2, 7002, '', NULL, NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(7013, '症状修改', 'hospital:symptom:update', 3, 3, 7002, '', NULL, NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(7014, '症状删除', 'hospital:symptom:delete', 3, 4, 7002, '', NULL, NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');

-- 关联管理
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(7003, '关联管理', 'hospital:disease-symptom:query', 2, 3, 7000, 'kg-relations', 'ep:share', 'hospital/kg-relations/index', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(7015, '关联查询', 'hospital:disease-symptom:query', 3, 1, 7003, '', NULL, NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(7016, '关联创建', 'hospital:disease-symptom:create', 3, 2, 7003, '', NULL, NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(7017, '关联删除', 'hospital:disease-symptom:delete', 3, 3, 7003, '', NULL, NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(7018, '药品关联查询', 'hospital:disease-medicine:query', 3, 4, 7003, '', NULL, NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(7019, '药品关联创建', 'hospital:disease-medicine:create', 3, 5, 7003, '', NULL, NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(7020, '药品关联修改', 'hospital:disease-medicine:update', 3, 6, 7003, '', NULL, NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(7021, '药品关联删除', 'hospital:disease-medicine:delete', 3, 7, 7003, '', NULL, NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');

-- 图谱可视化
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(7004, '图谱可视化', 'hospital:knowledge-graph:query', 2, 4, 7000, 'knowledge-graph', 'ep:data-line', 'hospital/knowledge-graph/index', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(7022, '图谱查询', 'hospital:knowledge-graph:query', 3, 1, 7004, '', NULL, NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');

-- AI 辅助诊断
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(7005, 'AI 辅助诊断', 'hospital:ai:assist-diagnosis', 2, 5, 7000, 'ai-assist-diagnosis', 'ep:magic-stick', 'hospital/ai-assist-diagnosis/index', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(7023, 'AI 辅助诊断按钮', 'hospital:ai:assist-diagnosis', 3, 1, 7005, '', NULL, NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');

-- AI 处方审核
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(7006, 'AI 处方审核', 'hospital:ai:prescription-review', 2, 6, 7000, 'ai-review', 'ep:document-checked', 'hospital/ai-review/index', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(7024, 'AI 处方审核按钮', 'hospital:ai:prescription-review', 3, 1, 7006, '', NULL, NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');

-- 超级管理员(role_id=1) 授权全部知识图谱菜单
INSERT INTO system_role_menu (role_id, menu_id, tenant_id)
SELECT 1, id, 1 FROM system_menu WHERE id BETWEEN 7000 AND 7024
AND NOT EXISTS (SELECT 1 FROM system_role_menu rm WHERE rm.role_id = 1 AND rm.menu_id = system_menu.id AND rm.tenant_id = 1);
