-- =============================================
-- 医院项目精简 - 清理不需要的菜单和字典
-- 保留：系统管理(1)、基础设施(2)、医院管理(待重建)
-- 删除：支付/工作流/报表/公众号/会员/商城/CRM/ERP/AI/IoT/开发文档等
-- =============================================

-- 第一步：删除不需要的顶级菜单（会级联删除子菜单的 parent_id 引用）
DELETE FROM system_menu WHERE name = '支付管理';       -- id 1117
DELETE FROM system_menu WHERE name = '工作流程';       -- id 1185
DELETE FROM system_menu WHERE name = '作者动态';       -- id 1254
DELETE FROM system_menu WHERE name = '报表管理';       -- id 1281
DELETE FROM system_menu WHERE name = '公众号管理';     -- id 2084
DELETE FROM system_menu WHERE name = 'Boot 开发文档';  -- id 2159
DELETE FROM system_menu WHERE name = 'Cloud 开发文档'; -- id 2160
DELETE FROM system_menu WHERE name = '会员中心';       -- id 2262
DELETE FROM system_menu WHERE name = '商城系统';       -- id 2362
DELETE FROM system_menu WHERE name = 'CRM 系统';       -- id 2397
DELETE FROM system_menu WHERE name = 'ERP 系统';       -- id 2563
DELETE FROM system_menu WHERE name = 'AI 大模型';      -- id 2758
DELETE FROM system_menu WHERE name = 'IoT 物联网';     -- id 4000

-- 第二步：删除孤儿子菜单（循环执行，直到影响行数为0）
DELETE FROM system_menu WHERE parent_id NOT IN (SELECT id FROM (SELECT id FROM system_menu) AS TEMP) AND parent_id > 0;
DELETE FROM system_menu WHERE parent_id NOT IN (SELECT id FROM (SELECT id FROM system_menu) AS TEMP) AND parent_id > 0;
DELETE FROM system_menu WHERE parent_id NOT IN (SELECT id FROM (SELECT id FROM system_menu) AS TEMP) AND parent_id > 0;
DELETE FROM system_menu WHERE parent_id NOT IN (SELECT id FROM (SELECT id FROM system_menu) AS TEMP) AND parent_id > 0;
DELETE FROM system_menu WHERE parent_id NOT IN (SELECT id FROM (SELECT id FROM system_menu) AS TEMP) AND parent_id > 0;

-- 第三步：清理角色-菜单关联表中的孤儿记录
DELETE FROM system_role_menu WHERE menu_id NOT IN (SELECT id FROM system_menu);

-- 第四步：清理不需要的字典数据
-- 支付模块
DELETE FROM system_dict_type WHERE type LIKE 'pay_%';
DELETE FROM system_dict_data WHERE dict_type LIKE 'pay_%';
-- 会员模块
DELETE FROM system_dict_type WHERE type LIKE 'member_%';
DELETE FROM system_dict_data WHERE dict_type LIKE 'member_%';
-- 商城模块
DELETE FROM system_dict_type WHERE type LIKE 'product_%';
DELETE FROM system_dict_data WHERE dict_type LIKE 'product_%';
DELETE FROM system_dict_type WHERE type LIKE 'trade_%';
DELETE FROM system_dict_data WHERE dict_type LIKE 'trade_%';
DELETE FROM system_dict_type WHERE type LIKE 'promotion_%';
DELETE FROM system_dict_data WHERE dict_type LIKE 'promotion_%';
DELETE FROM system_dict_type WHERE type LIKE 'brokerage_%';
DELETE FROM system_dict_data WHERE dict_type LIKE 'brokerage_%';
-- ERP模块
DELETE FROM system_dict_type WHERE type LIKE 'erp_%';
DELETE FROM system_dict_data WHERE dict_type LIKE 'erp_%';
-- CRM模块
DELETE FROM system_dict_type WHERE type LIKE 'crm_%';
DELETE FROM system_dict_data WHERE dict_type LIKE 'crm_%';
-- 公众号模块
DELETE FROM system_dict_type WHERE type LIKE 'mp_%';
DELETE FROM system_dict_data WHERE dict_type LIKE 'mp_%';
-- 大屏报表模块
DELETE FROM system_dict_type WHERE type LIKE 'report_%';
DELETE FROM system_dict_data WHERE dict_type LIKE 'report_%';
-- AI模块
DELETE FROM system_dict_type WHERE type LIKE 'ai_%';
DELETE FROM system_dict_data WHERE dict_type LIKE 'ai_%';
-- IoT模块
DELETE FROM system_dict_type WHERE type LIKE 'iot_%';
DELETE FROM system_dict_data WHERE dict_type LIKE 'iot_%';
-- BPM模块
DELETE FROM system_dict_type WHERE type LIKE 'bpm_%';
DELETE FROM system_dict_data WHERE dict_type LIKE 'bpm_%';

-- 第五步：查看清理后剩余的顶级菜单
SELECT id, name, parent_id FROM system_menu WHERE parent_id = 0 ORDER BY id;
