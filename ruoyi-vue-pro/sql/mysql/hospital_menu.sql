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
(5190, '数据统计', 'hospital:stats:query', 2, 10, 5100, 'stats', 'ep:data-analysis', 'hospital/stats/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
(5191, '统计查询', 'hospital:stats:query', 3, 1, 5190, '', NULL, NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);