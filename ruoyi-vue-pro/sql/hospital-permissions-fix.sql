-- =============================================================
-- 医院模块：权限码补齐 + 测试账号与业务档案关联
-- =============================================================
-- 目的：
--   1) 让「医生」角色能创建/修改 就诊、处方（修复"医生无法添加处方"）
--   2) 让「患者」角色能支付账单（修复 payBill 一直 403）
--   3) 把测试登录账号关联到业务档案，打通"仅自己"数据隔离
--
-- 执行时机：在 §4.1 基础库初始化、以及（可选）§4.1.1 测试数据导入之后执行。
--   本脚本只动框架表(system_menu / system_role_menu)与业务表的 user_id 列，
--   重复执行安全（幂等）。
--
-- ⚠️ 环境相关：末尾两段 UPDATE 的 145/146 是本项目库里 doctor001/patient001
--   的 system_users.id，导入到别的库时请按实际账号 id 调整。
-- =============================================================

SET NAMES utf8mb4;
USE hospital;

-- -------------------------------------------------------------
-- 0) 补「账单支付」按钮权限码
--    BillController.payBill 要求 @PreAuthorize("...hospital:bill:pay")，
--    但原菜单表缺少这行，导致除超管外任何人支付都被 403。
--    type=3 按钮；parent_id=5180(收费管理)；status=0 启用。
-- -------------------------------------------------------------
INSERT IGNORE INTO system_menu
  (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, updater, deleted)
VALUES
  (5185, '账单支付', 'hospital:bill:pay', 3, 5185, 5180, '', '#', NULL, NULL, 0, b'1', b'1', b'1', 'system', 'system', b'0');

-- -------------------------------------------------------------
-- 1) 医生角色(role_id=3) 补写权限：就诊/处方的 create + update
--    用 NOT EXISTS 保证可重复执行、不产生重复授权行。
-- -------------------------------------------------------------
-- ⚠️ system_role_menu 有 tenant_id 列且默认 0；yudao 按登录租户(=1)过滤授权，
--    故这里必须显式写 tenant_id=1，否则授权行查不到 → 403。
INSERT INTO system_role_menu (role_id, menu_id, tenant_id)
SELECT 3, 5152, 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id=3 AND menu_id=5152 AND tenant_id=1); -- hospital:visit:create
INSERT INTO system_role_menu (role_id, menu_id, tenant_id)
SELECT 3, 5153, 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id=3 AND menu_id=5153 AND tenant_id=1); -- hospital:visit:update
INSERT INTO system_role_menu (role_id, menu_id, tenant_id)
SELECT 3, 5172, 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id=3 AND menu_id=5172 AND tenant_id=1); -- hospital:prescription:create
INSERT INTO system_role_menu (role_id, menu_id, tenant_id)
SELECT 3, 5173, 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id=3 AND menu_id=5173 AND tenant_id=1); -- hospital:prescription:update

-- -------------------------------------------------------------
-- 2) 患者角色(role_id=4) 补支付权限
-- -------------------------------------------------------------
INSERT INTO system_role_menu (role_id, menu_id, tenant_id)
SELECT 4, 5185, 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id=4 AND menu_id=5185 AND tenant_id=1); -- hospital:bill:pay

-- -------------------------------------------------------------
-- 3) 关联测试账号与业务档案（打通 getCurrentDoctorId / getCurrentPatientId）
--    doctor001(uid=145) => hospital_doctor.id=1（张伟）
--    patient001(uid=146) => hospital_patient.id=1（张磊）
--    ★ 环境相关：别的库请按实际 system_users.id 调整。
-- -------------------------------------------------------------
UPDATE hospital_doctor SET user_id = 145 WHERE id = 1;
UPDATE hospital_patient SET user_id = 146 WHERE id = 1;

-- -------------------------------------------------------------
-- 4) 幂等兜底：把上述授权行里 tenant_id=0 的旧数据归正为 1
--    （早期版本漏写 tenant_id 导致授权不生效；重跑本脚本可自愈）
-- -------------------------------------------------------------
UPDATE system_role_menu SET tenant_id = 1
WHERE tenant_id = 0 AND menu_id IN (5152, 5153, 5172, 5173, 5185)
AND role_id IN (3, 4);

-- -------------------------------------------------------------
-- 5) 补齐"下拉选项"所需的查询权限（消除打开页面时的 403 弹窗）
--    前端就诊/处方页面在 mounted 时会并发拉取下拉选项
--      (患者/医生/科室/药品)，若当前角色缺少对应 :query 权限，
--     每个接口都返回 403 → 前端弹"没有该操作权限"。
--    这里只授权 type=3 的"查询按钮"，只给 @PreAuthorize 用的
--    权限字符串，不会在侧边栏新增任何菜单（type=2 的菜单才会出现在菜单里）。
--    ⚠️ 故意【不】给"患者"角色授予 hospital:patient:query：
--       PatientRespVO 包含身份证号/电话/住址/医保卡号/既往病史等 PII，
--       授权后患者可调 /hospital/patient/page 看到全部患者隐私。
--       前端已改为按 checkPermi 判断，缺 patient:query 时直接跳过该下拉。
-- -------------------------------------------------------------
-- 医生(3)：补 医生查询(5111)，其余 department/patient/medicine/visit/prescription 查询权限医生已有
INSERT INTO system_role_menu (role_id, menu_id, tenant_id)
SELECT 3, 5111, 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id=3 AND menu_id=5111 AND tenant_id=1); -- hospital:doctor:query

-- 患者(4)：补 医生查询(5111) / 科室查询(5102) / 药品查询(5161)
INSERT INTO system_role_menu (role_id, menu_id, tenant_id)
SELECT 4, 5111, 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id=4 AND menu_id=5111 AND tenant_id=1); -- hospital:doctor:query
INSERT INTO system_role_menu (role_id, menu_id, tenant_id)
SELECT 4, 5102, 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id=4 AND menu_id=5102 AND tenant_id=1); -- hospital:department:query
INSERT INTO system_role_menu (role_id, menu_id, tenant_id)
SELECT 4, 5161, 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id=4 AND menu_id=5161 AND tenant_id=1); -- hospital:medicine:query

-- -------------------------------------------------------------
-- 6) 对医生隐藏"数据统计"菜单
--    侧边栏完全由后端 getRouters 按 system_role_menu 生成；
--    移除医生(3)对 数据统计(5190) 及其查询按钮(5191) 的授权后，
--    医生的菜单里就不再出现"数据统计"。
-- -------------------------------------------------------------
DELETE FROM system_role_menu WHERE role_id = 3 AND menu_id IN (5190, 5191) AND tenant_id = 1;

-- -------------------------------------------------------------
-- 7) 补「发药」按钮权限码并授予医生
--    PrescriptionController.dispensePrescription 要求
--      @PreAuthorize("...hospital:prescription:dispense")，
--    但原菜单表缺这行 → 医生点「发药」一律 403。
--    type=3 按钮；parent_id=5170(处方管理)；status=0 启用。
--    授予医生(3)，让患者(4)看不到该按钮（患者也不应发药）。
-- -------------------------------------------------------------
INSERT IGNORE INTO system_menu
  (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, updater, deleted)
VALUES
  (5175, '处方发药', 'hospital:prescription:dispense', 3, 5175, 5170, '', '#', NULL, NULL, 0, b'1', b'1', b'1', 'system', 'system', b'0');

INSERT INTO system_role_menu (role_id, menu_id, tenant_id)
SELECT 3, 5175, 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id=3 AND menu_id=5175 AND tenant_id=1); -- hospital:prescription:dispense

-- 把 5175 纳入幂等归正（防止早期 tenant_id=0 的旧数据）
UPDATE system_role_menu SET tenant_id = 1
WHERE tenant_id = 0 AND menu_id = 5175 AND role_id = 3;

-- -------------------------------------------------------------
-- 8) 补「床位分配 / 释放」按钮权限码并授予医生
--    BedController.assignBed / releaseBed 要求
--      @PreAuthorize("...hospital:bed:assign" / "...hospital:bed:release")，
--    但原菜单表缺这两行 → 医生点「分配/释放」一律 403。
--    type=3 按钮；parent_id=5140(床位管理)；status=0 启用。
--    授予医生(3)：医生负责患者入院/出院，理应能分配与释放床位。
-- -------------------------------------------------------------
INSERT IGNORE INTO system_menu
  (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, updater, deleted)
VALUES
  (5146, '床位分配', 'hospital:bed:assign', 3, 5146, 5140, '', '#', NULL, NULL, 0, b'1', b'1', b'1', 'system', 'system', b'0'),
  (5147, '床位释放', 'hospital:bed:release', 3, 5147, 5140, '', '#', NULL, NULL, 0, b'1', b'1', b'1', 'system', 'system', b'0');

INSERT INTO system_role_menu (role_id, menu_id, tenant_id)
SELECT 3, 5146, 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id=3 AND menu_id=5146 AND tenant_id=1); -- hospital:bed:assign
INSERT INTO system_role_menu (role_id, menu_id, tenant_id)
SELECT 3, 5147, 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_role_menu WHERE role_id=3 AND menu_id=5147 AND tenant_id=1); -- hospital:bed:release

-- 把 5146/5147 纳入幂等归正
UPDATE system_role_menu SET tenant_id = 1
WHERE tenant_id = 0 AND menu_id IN (5146, 5147) AND role_id = 3;

