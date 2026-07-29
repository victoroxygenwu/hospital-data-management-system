-- =============================================
-- 模块二：智能数据可视化 — 菜单与超级管理员授权
-- 菜单 id 段 7100-7106，挂在医院管理(5100)下
-- 执行后请重启后端或 redis-cli flushdb
-- =============================================

SET NAMES utf8mb4;
USE hospital;

DELETE FROM system_role_menu WHERE menu_id BETWEEN 7100 AND 7106;
DELETE FROM system_menu WHERE id BETWEEN 7100 AND 7106;

-- 目录：数据看板
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (7100, '数据看板', '', 1, 25, 5100, 'hospital-visual', 'ep:data-line', NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');

-- 5 个可视化页面
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(7101, '接诊热力图', 'hospital:visual:query', 2, 1, 7100, 'heatmap', 'ep:calendar', 'hospital/visual/heatmap/index', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(7102, '科室雷达图', 'hospital:visual:query', 2, 2, 7100, 'dept-radar', 'ep:pie-chart', 'hospital/visual/dept-radar/index', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(7103, '患者画像', 'hospital:visual:query', 2, 3, 7100, 'patient-profile', 'ep:user', 'hospital/visual/patient-profile/index', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(7104, '疾病趋势', 'hospital:visual:query', 2, 4, 7100, 'disease-seasonal', 'ep:trend-charts', 'hospital/visual/disease-seasonal/index', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
(7105, '药品关联', 'hospital:visual:query', 2, 5, 7100, 'medicine-cooccurrence', 'ep:connection', 'hospital/visual/medicine-cooccurrence/index', NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');

-- 权限点
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (7106, '可视化查询', 'hospital:visual:query', 3, 1, 7100, '', NULL, NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');

-- 超级管理员(role_id=1) 授权
INSERT INTO system_role_menu (role_id, menu_id, tenant_id)
SELECT 1, id, 1 FROM system_menu WHERE id BETWEEN 7100 AND 7106
AND NOT EXISTS (SELECT 1 FROM system_role_menu rm WHERE rm.role_id = 1 AND rm.menu_id = system_menu.id AND rm.tenant_id = 1);
