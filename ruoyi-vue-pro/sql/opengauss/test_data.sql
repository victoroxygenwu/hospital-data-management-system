-- =============================================
-- 医院数据管理系统 - 测试数据
-- 用于功能测试，覆盖所有10张业务表
-- 执行方式: docker exec opengauss bash -c "export LD_LIBRARY_PATH=/usr/local/opengauss/lib; /usr/local/opengauss/bin/gsql -U gaussdb -W Hospital@2026 -d ruoyi_vue_pro -f /tmp/test_data.sql"
-- =============================================

-- 1. 科室 (department) - 已有2条，补充3条
INSERT INTO hospital_department (id, dept_name, phone, manager, location, description, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(3, '外科', '010-65432100', '王建国', '3号楼5层', '负责外科手术及术后护理', 1, NOW(), 1, NOW(), 0, 1),
(4, '儿科', '010-76543210', '李红梅', '2号楼3层', '负责儿童疾病诊治', 1, NOW(), 1, NOW(), 0, 1),
(5, '急诊科', '010-87654321', '张强', '1号楼1层', '24小时急诊服务', 1, NOW(), 1, NOW(), 0, 1);

-- 2. 病房 (ward) - 新增6条（对应已有3个科室）
INSERT INTO hospital_ward (id, dept_id, ward_no, type, capacity, used_beds, status, description, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1, 1, 'N-101', '普通病房', 6, 2, 1, '内科一病区', 1, NOW(), 1, NOW(), 0, 1),
(2, 1, 'N-102', '普通病房', 4, 1, 1, '内科二病区', 1, NOW(), 1, NOW(), 0, 1),
(3, 3, 'S-201', '外科病房', 8, 3, 1, '外科一病区', 1, NOW(), 1, NOW(), 0, 1),
(4, 4, 'P-301', '儿科病房', 10, 5, 1, '儿科一病区', 1, NOW(), 1, NOW(), 0, 1),
(5, 5, 'E-001', '抢救室', 4, 2, 1, '急诊抢救室', 1, NOW(), 1, NOW(), 0, 1),
(6, 5, 'E-002', '观察室', 6, 0, 1, '急诊观察室', 1, NOW(), 1, NOW(), 0, 1);

-- 3. 床位 (bed) - 每个病房配床位
INSERT INTO hospital_bed (id, ward_id, bed_no, status, patient_id, admission_time, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
-- ward 1 的床位
(1, 1, 'N-101-01', 'OCCUPIED', 1, NOW() - INTERVAL '5 DAY', 1, NOW(), 1, NOW(), 0, 1),
(2, 1, 'N-101-02', 'OCCUPIED', 2, NOW() - INTERVAL '3 DAY', 1, NOW(), 1, NOW(), 0, 1),
(3, 1, 'N-101-03', 'AVAILABLE', NULL, NULL, 1, NOW(), 1, NOW(), 0, 1),
(4, 1, 'N-101-04', 'AVAILABLE', NULL, NULL, 1, NOW(), 1, NOW(), 0, 1),
-- ward 2 的床位
(5, 2, 'N-102-01', 'OCCUPIED', 1, NOW() - INTERVAL '1 DAY', 1, NOW(), 1, NOW(), 0, 1),
(6, 2, 'N-102-02', 'AVAILABLE', NULL, NULL, 1, NOW(), 1, NOW(), 0, 1),
(7, 2, 'N-102-03', 'AVAILABLE', NULL, NULL, 1, NOW(), 1, NOW(), 0, 1),
-- ward 3 的床位
(8, 3, 'S-201-01', 'OCCUPIED', 2, NOW() - INTERVAL '2 DAY', 1, NOW(), 1, NOW(), 0, 1),
(9, 3, 'S-201-02', 'OCCUPIED', 1, NOW() - INTERVAL '4 DAY', 1, NOW(), 1, NOW(), 0, 1),
(10, 3, 'S-201-03', 'OCCUPIED', 2, NOW() - INTERVAL '1 DAY', 1, NOW(), 1, NOW(), 0, 1),
(11, 3, 'S-201-04', 'AVAILABLE', NULL, NULL, 1, NOW(), 1, NOW(), 0, 1),
(12, 3, 'S-201-05', 'AVAILABLE', NULL, NULL, 1, NOW(), 1, NOW(), 0, 1),
-- ward 4 的床位 (儿科)
(13, 4, 'P-301-01', 'OCCUPIED', 1, NOW() - INTERVAL '2 DAY', 1, NOW(), 1, NOW(), 0, 1),
(14, 4, 'P-301-02', 'OCCUPIED', 2, NOW() - INTERVAL '1 DAY', 1, NOW(), 1, NOW(), 0, 1),
(15, 4, 'P-301-03', 'AVAILABLE', NULL, NULL, 1, NOW(), 1, NOW(), 0, 1),
(16, 4, 'P-301-04', 'AVAILABLE', NULL, NULL, 1, NOW(), 1, NOW(), 0, 1),
(17, 4, 'P-301-05', 'AVAILABLE', NULL, NULL, 1, NOW(), 1, NOW(), 0, 1),
-- ward 5 的床位 (急诊抢救室)
(18, 5, 'E-001-01', 'OCCUPIED', 2, NOW() - INTERVAL '1 DAY', 1, NOW(), 1, NOW(), 0, 1),
(19, 5, 'E-001-02', 'OCCUPIED', 1, NOW() - INTERVAL '6 HOUR', 1, NOW(), 1, NOW(), 0, 1),
(20, 5, 'E-001-03', 'AVAILABLE', NULL, NULL, 1, NOW(), 1, NOW(), 0, 1),
(21, 5, 'E-001-04', 'AVAILABLE', NULL, NULL, 1, NOW(), 1, NOW(), 0, 1),
-- ward 6 的床位 (急诊观察室，全空)
(22, 6, 'E-002-01', 'AVAILABLE', NULL, NULL, 1, NOW(), 1, NOW(), 0, 1),
(23, 6, 'E-002-02', 'AVAILABLE', NULL, NULL, 1, NOW(), 1, NOW(), 0, 1);

-- 4. 患者 (patient) - 已有1条，补充4条
INSERT INTO hospital_patient (id, user_id, name, gender, birth_date, id_card, phone, address, emergency_contact, emergency_phone, insurance_no, medical_history, admission_date, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(2, 0, '张小明', '男', '1985-06-15', '110101198506151234', '13800138002', '朝阳区建国路88号', '张小红', '13900139002', 'BJ123456789', '高血压病史', '2026-04-13', 1, NOW(), 1, NOW(), 0, 1),
(3, 0, '李婷婷', '女', '1990-03-22', '310101199003221567', '13800138003', '海淀区中关村大街1号', '李强', '13900139003', 'SH987654321', '无', '2026-04-14', 1, NOW(), 1, NOW(), 0, 1),
(4, 0, '王小杰', '男', '2015-09-08', '440101201509081234', '13800138004', '天河区天河路100号', '王芳', '13900139004', 'GZ456789123', '过敏性鼻炎', '2026-04-15', 1, NOW(), 1, NOW(), 0, 1),
(5, 0, '刘大爷', '男', '1960-11-30', '510101196011301890', '13800138005', '武侯区人民南路4段', '刘小兵', '13900139005', 'CD789123456', '糖尿病、冠心病', '2026-04-16', 1, NOW(), 1, NOW(), 0, 1);

-- 5. 就诊记录 (visit) - 新增5条
INSERT INTO hospital_visit (id, patient_id, doctor_id, dept_id, visit_date, reason, diagnosis, notes, status, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1, 1, 1, 1, NOW() - INTERVAL '2 DAY', '常规检查', '轻微感冒', '多喝水休息', 'COMPLETED', 1, NOW(), 1, NOW(), 0, 1),
(2, 2, 1, 1, NOW() - INTERVAL '1 DAY', '头晕乏力', '疑似贫血', '建议进一步检查血常规', 'COMPLETED', 1, NOW(), 1, NOW(), 0, 1),
(3, 3, 1, 1, NOW() - INTERVAL '3 DAY', '腹部不适', '肠胃炎', '清淡饮食，服药观察', 'COMPLETED', 1, NOW(), 1, NOW(), 0, 1),
(4, 4, 1, 1, NOW() - INTERVAL '1 DAY', '高热39度', '上呼吸道感染', '物理降温+退热药，密切观察', 'COMPLETED', 1, NOW(), 1, NOW(), 0, 1),
(5, 5, 1, 1, NOW() - INTERVAL '6 HOUR', '胸闷气短', '待查（疑似心绞痛）', '心电图检查中', 'IN_PROGRESS', 1, NOW(), 1, NOW(), 0, 1);

-- 6. 药品 (medicine) - 已有1条，补充6条
INSERT INTO hospital_medicine (id, name, specification, unit, price, stock, manufacturer, expiry_date, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(2, '阿莫西林胶囊', '0.25g*24粒/盒', '盒', 15.80, 500, '华北制药股份有限公司', '2028-12-31', 1, NOW(), 1, NOW(), 0, 1),
(3, '布洛芬缓释胶囊', '0.3g*20粒/盒', '盒', 18.50, 300, '扬子江药业集团有限公司', '2027-06-30', 1, NOW(), 1, NOW(), 0, 1),
(4, '复方氨酚烷胺片', '12片/盒', '盒', 8.50, 800, '感冒灵制药有限公司', '2027-09-15', 1, NOW(), 1, NOW(), 0, 1),
(5, '硫酸沙丁胺醇吸入气雾剂', '100μg*200揿/支', '支', 35.60, 100, '葛兰素史克制药有限公司', '2027-03-20', 1, NOW(), 1, NOW(), 0, 1),
(6, '硝苯地平缓释片', '20mg*30片/盒', '盒', 22.30, 200, '拜耳医药保健有限公司', '2028-08-10', 1, NOW(), 1, NOW(), 0, 1),
(7, '二甲双胍片', '0.5g*48片/盒', '盒', 28.00, 150, '中美上海施贵宝制药有限公司', '2029-01-15', 1, NOW(), 1, NOW(), 0, 1),
(8, '硝酸甘油片', '0.5mg*100片/瓶', '瓶', 12.00, 80, '青岛黄海制药有限责任公司', '2026-06-30', 1, NOW(), 1, NOW(), 0, 1);

-- 7. 处方 (prescription) - 新增5条
INSERT INTO hospital_prescription (id, visit_id, doctor_id, status, notes, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1, 1, 1, 'DISPENSED', '感冒处方', 1, NOW(), 1, NOW(), 0, 1),
(2, 2, 1, 'DISPENSED', '贫血调理处方', 1, NOW(), 1, NOW(), 0, 1),
(3, 3, 1, 'DISPENSED', '肠胃炎处方', 1, NOW(), 1, NOW(), 0, 1),
(4, 4, 1, 'DISPENSED', '退热消炎处方', 1, NOW(), 1, NOW(), 0, 1),
(5, 5, 1, 'PENDING', '心绞痛急救处方（待发药）', 1, NOW(), 1, NOW(), 0, 1);

-- 8. 处方明细 (prescription_item) - 每个处方2-3条药品
INSERT INTO hospital_prescription_item (id, prescription_id, medicine_id, quantity, price, instructions, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
-- 处方1（visit_id=1, 感冒）
(1, 1, 1, 2, 25.00, '每日3次，每次2片，饭后服用', 1, NOW(), 1, NOW(), 0, 1),
(2, 1, 4, 1, 8.50, '每日2次，每次1片', 1, NOW(), 1, NOW(), 0, 1),
-- 处方2（visit_id=2, 贫血）
(3, 2, 2, 3, 15.80, '每日3次，每次1粒，餐后服用', 1, NOW(), 1, NOW(), 0, 1),
(4, 2, 3, 2, 18.50, '疼痛时服用，每次1粒', 1, NOW(), 1, NOW(), 0, 1),
-- 处方3（visit_id=3, 肠胃炎）
(5, 3, 2, 2, 15.80, '每日3次，每次1粒', 1, NOW(), 1, NOW(), 0, 1),
(6, 3, 4, 1, 8.50, '每日2次，每次1片', 1, NOW(), 1, NOW(), 0, 1),
-- 处方4（visit_id=4, 上呼吸道感染）
(7, 4, 3, 2, 18.50, '发热时服用，每次1粒，间隔6小时', 1, NOW(), 1, NOW(), 0, 1),
(8, 4, 4, 2, 8.50, '每日3次，每次1片', 1, NOW(), 1, NOW(), 0, 1),
(9, 4, 1, 1, 25.00, '每日3次，每次2片', 1, NOW(), 1, NOW(), 0, 1),
-- 处方5（visit_id=5, 心绞痛急救）
(10, 5, 6, 3, 22.30, '每日2次，每次1片，控制血压', 1, NOW(), 1, NOW(), 0, 1),
(11, 5, 7, 2, 28.00, '每日3次，每次1片，餐后服用', 1, NOW(), 1, NOW(), 0, 1),
(12, 5, 8, 1, 12.00, '心绞痛发作时舌下含服1片', 1, NOW(), 1, NOW(), 0, 1);

-- 9. 账单 (bill) - 新增5条（对应5次就诊）
INSERT INTO hospital_bill (id, visit_id, patient_id, total_amount, pay_amount, pay_time, pay_method, status, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1, 1, 1, 58.50, 58.50, NOW() - INTERVAL '2 DAY', 'WECHAT', 'PAID', 1, NOW(), 1, NOW(), 0, 1),
(2, 2, 2, 49.10, 49.10, NOW() - INTERVAL '1 DAY', 'ALIPAY', 'PAID', 1, NOW(), 1, NOW(), 0, 1),
(3, 3, 3, 40.10, 40.10, NOW() - INTERVAL '3 DAY', 'CARD', 'PAID', 1, NOW(), 1, NOW(), 0, 1),
(4, 4, 4, 73.50, 73.50, NOW() - INTERVAL '1 DAY', 'WECHAT', 'PAID', 1, NOW(), 1, NOW(), 0, 1),
(5, 5, 5, 114.90, 0.00, NULL, NULL, 'UNPAID', 1, NOW(), 1, NOW(), 0, 1);
