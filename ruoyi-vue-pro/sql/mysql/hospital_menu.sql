-- =============================================
-- 医院病人数据管理系统 - 菜单权限初始化数据
-- ID区间: 5100-5191 (避免与框架内置菜单 1-5012 冲突)
-- =============================================

-- ============ 第一步：清理旧数据 ============
-- 删除之前导入的医院管理菜单（如果有）
DELETE FROM system_menu WHERE id BETWEEN 5100 AND 5191;

-- ============ 第二步：插入新数据 ============
-- 一级菜单：医院管理
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (5100, '医院管理', '', 1, 10, 0, '/hospital', 'ep:hospital', NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- ============ 科室管理 ============
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES 
(5101, '科室管理', 'hospital:department:query', 2, 1, 5100, 'department', 'ep:office-building', 'hospital/department/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5102, '科室查询', 'hospital:department:query', 3, 1, 5101, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5103, '科室创建', 'hospital:department:create', 3, 2, 5101, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5104, '科室修改', 'hospital:department:update', 3, 3, 5101, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5105, '科室删除', 'hospital:department:delete', 3, 4, 5101, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- ============ 医生管理 ============
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES 
(5110, '医生管理', 'hospital:doctor:query', 2, 2, 5100, 'doctor', 'ep:user', 'hospital/doctor/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5111, '医生查询', 'hospital:doctor:query', 3, 1, 5110, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5112, '医生创建', 'hospital:doctor:create', 3, 2, 5110, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5113, '医生修改', 'hospital:doctor:update', 3, 3, 5110, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5114, '医生删除', 'hospital:doctor:delete', 3, 4, 5110, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- ============ 病人管理 ============
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES 
(5120, '病人管理', 'hospital:patient:query', 2, 3, 5100, 'patient', 'ep:avatar', 'hospital/patient/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5121, '病人查询', 'hospital:patient:query', 3, 1, 5120, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5122, '病人创建', 'hospital:patient:create', 3, 2, 5120, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5123, '病人修改', 'hospital:patient:update', 3, 3, 5120, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5124, '病人删除', 'hospital:patient:delete', 3, 4, 5120, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- ============ 病房管理 ============
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES 
(5130, '病房管理', 'hospital:ward:query', 2, 4, 5100, 'ward', 'ep:home-filled', 'hospital/ward/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5131, '病房查询', 'hospital:ward:query', 3, 1, 5130, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5132, '病房创建', 'hospital:ward:create', 3, 2, 5130, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5133, '病房修改', 'hospital:ward:update', 3, 3, 5130, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5134, '病房删除', 'hospital:ward:delete', 3, 4, 5130, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- ============ 床位管理 ============
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES 
(5140, '床位管理', 'hospital:bed:query', 2, 5, 5100, 'bed', 'ep:bed', 'hospital/bed/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5141, '床位查询', 'hospital:bed:query', 3, 1, 5140, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5142, '床位创建', 'hospital:bed:create', 3, 2, 5140, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5143, '床位修改', 'hospital:bed:update', 3, 3, 5140, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5144, '床位删除', 'hospital:bed:delete', 3, 4, 5140, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- ============ 就诊管理 ============
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES 
(5150, '就诊管理', 'hospital:visit:query', 2, 6, 5100, 'visit', 'ep:document', 'hospital/visit/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5151, '就诊查询', 'hospital:visit:query', 3, 1, 5150, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5152, '就诊创建', 'hospital:visit:create', 3, 2, 5150, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5153, '就诊修改', 'hospital:visit:update', 3, 3, 5150, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5154, '就诊删除', 'hospital:visit:delete', 3, 4, 5150, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- ============ 药品管理 ============
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES 
(5160, '药品管理', 'hospital:medicine:query', 2, 7, 5100, 'medicine', 'ep:medicine', 'hospital/medicine/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5161, '药品查询', 'hospital:medicine:query', 3, 1, 5160, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5162, '药品创建', 'hospital:medicine:create', 3, 2, 5160, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5163, '药品修改', 'hospital:medicine:update', 3, 3, 5160, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5164, '药品删除', 'hospital:medicine:delete', 3, 4, 5160, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- ============ 处方管理 ============
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES 
(5170, '处方管理', 'hospital:prescription:query', 2, 8, 5100, 'prescription', 'ep:document-checked', 'hospital/prescription/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5171, '处方查询', 'hospital:prescription:query', 3, 1, 5170, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5172, '处方创建', 'hospital:prescription:create', 3, 2, 5170, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5173, '处方修改', 'hospital:prescription:update', 3, 3, 5170, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5174, '处方删除', 'hospital:prescription:delete', 3, 4, 5170, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- ============ 收费管理 ============
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES 
(5180, '收费管理', 'hospital:bill:query', 2, 9, 5100, 'bill', 'ep:money', 'hospital/bill/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5181, '账单查询', 'hospital:bill:query', 3, 1, 5180, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5182, '账单创建', 'hospital:bill:create', 3, 2, 5180, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5183, '账单修改', 'hospital:bill:update', 3, 3, 5180, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5184, '账单删除', 'hospital:bill:delete', 3, 4, 5180, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- ============ 数据统计 ============
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES 
(5190, '数据统计', 'hospital:stats:query', 2, 6, 7100, 'stats', 'ep:data-analysis', 'hospital/stats/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5191, '统计查询', 'hospital:stats:query', 3, 1, 5190, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- ============ 模块一：AI 增强医疗知识图谱菜单（id 7000-7024，挂在医院管理 5100 下）============
DELETE FROM system_role_menu WHERE menu_id BETWEEN 7000 AND 7024;
DELETE FROM system_menu WHERE id BETWEEN 7000 AND 7024;
DELETE FROM system_role_menu WHERE menu_id BETWEEN 7000 AND 7024;
DELETE FROM system_menu WHERE id BETWEEN 7000 AND 7024;

-- 一级目录：知识图谱
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (7000, '知识图谱', '', 1, 50, 5100, 'knowledge', 'ep:connection', NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- 疾病字典
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(7001, '疾病字典', 'hospital:disease:query', 2, 1, 7000, 'disease', 'ep:first-aid-kit', 'hospital/disease/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(7007, '疾病查询', 'hospital:disease:query', 3, 1, 7001, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(7008, '疾病创建', 'hospital:disease:create', 3, 2, 7001, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(7009, '疾病修改', 'hospital:disease:update', 3, 3, 7001, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(7010, '疾病删除', 'hospital:disease:delete', 3, 4, 7001, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- 症状字典
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(7002, '症状字典', 'hospital:symptom:query', 2, 2, 7000, 'symptom', 'ep:warning', 'hospital/symptom/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(7011, '症状查询', 'hospital:symptom:query', 3, 1, 7002, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(7012, '症状创建', 'hospital:symptom:create', 3, 2, 7002, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(7013, '症状修改', 'hospital:symptom:update', 3, 3, 7002, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(7014, '症状删除', 'hospital:symptom:delete', 3, 4, 7002, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- 关联管理
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(7003, '关联管理', 'hospital:disease-symptom:query', 2, 3, 7000, 'kg-relations', 'ep:share', 'hospital/kg-relations/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(7015, '关联查询', 'hospital:disease-symptom:query', 3, 1, 7003, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(7016, '关联创建', 'hospital:disease-symptom:create', 3, 2, 7003, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(7017, '关联删除', 'hospital:disease-symptom:delete', 3, 3, 7003, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(7018, '药品关联查询', 'hospital:disease-medicine:query', 3, 4, 7003, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(7019, '药品关联创建', 'hospital:disease-medicine:create', 3, 5, 7003, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(7020, '药品关联修改', 'hospital:disease-medicine:update', 3, 6, 7003, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(7021, '药品关联删除', 'hospital:disease-medicine:delete', 3, 7, 7003, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- 图谱可视化
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(7004, '图谱可视化', 'hospital:knowledge-graph:query', 2, 4, 7000, 'knowledge-graph', 'ep:data-line', 'hospital/knowledge-graph/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(7022, '图谱查询', 'hospital:knowledge-graph:query', 3, 1, 7004, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- AI 辅助诊断
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(7005, 'AI 辅助诊断', 'hospital:ai:assist-diagnosis', 2, 5, 7000, 'ai-assist-diagnosis', 'ep:magic-stick', 'hospital/ai-assist-diagnosis/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(7023, 'AI 辅助诊断按钮', 'hospital:ai:assist-diagnosis', 3, 1, 7005, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- AI 处方审核
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(7006, 'AI 处方审核', 'hospital:ai:prescription-review', 2, 6, 7000, 'ai-review', 'ep:document-checked', 'hospital/ai-review/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(7024, 'AI 处方审核按钮', 'hospital:ai:prescription-review', 3, 1, 7006, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- ============ 模块二：智能数据可视化菜单（id 7100-7106，挂在医院管理 5100 下）============
DELETE FROM system_role_menu WHERE menu_id BETWEEN 7100 AND 7106;
DELETE FROM system_menu WHERE id BETWEEN 7100 AND 7106;
DELETE FROM system_role_menu WHERE menu_id BETWEEN 7100 AND 7106;
DELETE FROM system_menu WHERE id BETWEEN 7100 AND 7106;

-- 目录：数据看板
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (7100, '数据看板', '', 1, 25, 5100, 'hospital-visual', 'ep:data-line', NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- 5 个可视化页面
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(7101, '接诊热力图', 'hospital:visual:query', 2, 1, 7100, 'heatmap', 'ep:calendar', 'hospital/visual/heatmap/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(7102, '科室雷达图', 'hospital:visual:query', 2, 2, 7100, 'dept-radar', 'ep:pie-chart', 'hospital/visual/dept-radar/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(7103, '患者画像', 'hospital:visual:query', 2, 3, 7100, 'patient-profile', 'ep:user', 'hospital/visual/patient-profile/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(7104, '疾病趋势', 'hospital:visual:query', 2, 4, 7100, 'disease-seasonal', 'ep:trend-charts', 'hospital/visual/disease-seasonal/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(7105, '药品关联', 'hospital:visual:query', 2, 5, 7100, 'medicine-cooccurrence', 'ep:connection', 'hospital/visual/medicine-cooccurrence/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- 权限点
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (7106, '可视化查询', 'hospital:visual:query', 3, 1, 7100, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);
