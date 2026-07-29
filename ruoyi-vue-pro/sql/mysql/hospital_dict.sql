-- 医院管理系统数据字典
-- 执行方式: mysql -u root -p123456 hospital < hospital_dict.sql
DELETE FROM system_dict_data WHERE dict_type LIKE 'hospital_%';
DELETE FROM system_dict_type WHERE type LIKE 'hospital_%';

-- ============ 字典类型 ============
INSERT IGNORE INTO `system_dict_type` (`id`, `name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `deleted_time`) VALUES
(2001, '病房类型',        'hospital_ward_type',            0, '病房类型',        'admin', NOW(), 'admin', NOW(), b'0', NULL),
(2002, '床位状态',        'hospital_bed_status',           0, '床位状态',        'admin', NOW(), 'admin', NOW(), b'0', NULL),
(2003, '病房状态',        'hospital_ward_status',          0, '病房状态',        'admin', NOW(), 'admin', NOW(), b'0', NULL),
(2004, '就诊状态',        'hospital_visit_status',         0, '就诊状态',        'admin', NOW(), 'admin', NOW(), b'0', NULL),
(2005, '处方状态',        'hospital_prescription_status',  0, '处方状态',        'admin', NOW(), 'admin', NOW(), b'0', NULL),
(2006, '支付状态',        'hospital_bill_pay_status',      0, '账单支付状态',    'admin', NOW(), 'admin', NOW(), b'0', NULL),
(2007, '支付方式',        'hospital_bill_pay_method',      0, '账单支付方式',    'admin', NOW(), 'admin', NOW(), b'0', NULL),
(2008, '患者性别',        'hospital_patient_gender',       0, '患者性别',        'admin', NOW(), 'admin', NOW(), b'0', NULL),
(2009, '医生职称',        'hospital_doctor_title',         0, '医生职称',        'admin', NOW(), 'admin', NOW(), b'0', NULL);

-- ============ 字典数据 ============

-- hospital_ward_type: 病房类型 (string value, used by getDictOptions)
INSERT IGNORE INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES
(20001, 1, '普通病房', '普通病房', 'hospital_ward_type', 0, 'primary', '', '普通病房',       'admin', NOW(), 'admin', NOW(), b'0'),
(20002, 2, 'ICU',      'ICU',     'hospital_ward_type', 0, 'danger',  '', '重症监护病房',   'admin', NOW(), 'admin', NOW(), b'0'),
(20003, 3, 'VIP病房',  'VIP病房', 'hospital_ward_type', 0, 'warning', '', 'VIP病房',        'admin', NOW(), 'admin', NOW(), b'0');

-- hospital_ward_status: 病房状态 (int value)
INSERT IGNORE INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES
(20011, 1, '正常',   '0', 'hospital_ward_status', 0, 'success', '', '病房正常使用',  'admin', NOW(), 'admin', NOW(), b'0'),
(20012, 2, '已满',   '1', 'hospital_ward_status', 0, 'warning', '', '病房床位已满',  'admin', NOW(), 'admin', NOW(), b'0'),
(20013, 3, '维修中', '2', 'hospital_ward_status', 0, 'info',    '', '病房维修中',    'admin', NOW(), 'admin', NOW(), b'0');

-- hospital_bed_status: 床位状态 (int value)
INSERT IGNORE INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES
(20021, 1, '空闲',   '0', 'hospital_bed_status', 0, 'success', '', '床位空闲可用',  'admin', NOW(), 'admin', NOW(), b'0'),
(20022, 2, '已占用', '1', 'hospital_bed_status', 0, 'danger',  '', '床位已占用',    'admin', NOW(), 'admin', NOW(), b'0'),
(20023, 3, '维修中', '2', 'hospital_bed_status', 0, 'warning', '', '床位维修中',    'admin', NOW(), 'admin', NOW(), b'0');

-- hospital_visit_status: 就诊状态 (int value)
INSERT IGNORE INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES
(20031, 1, '待就诊', '0', 'hospital_visit_status', 0, 'info',    '', '待就诊',     'admin', NOW(), 'admin', NOW(), b'0'),
(20032, 2, '就诊中', '1', 'hospital_visit_status', 0, 'warning', '', '就诊中',     'admin', NOW(), 'admin', NOW(), b'0'),
(20033, 3, '已完成', '2', 'hospital_visit_status', 0, 'success', '', '已完成就诊', 'admin', NOW(), 'admin', NOW(), b'0'),
(20034, 4, '已取消', '3', 'hospital_visit_status', 0, 'danger',  '', '已取消',     'admin', NOW(), 'admin', NOW(), b'0');

-- hospital_prescription_status: 处方状态 (int value)
INSERT IGNORE INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES
(20041, 1, '待发药', '0', 'hospital_prescription_status', 0, 'warning', '', '处方待发药', 'admin', NOW(), 'admin', NOW(), b'0'),
(20042, 2, '已发药', '1', 'hospital_prescription_status', 0, 'success', '', '处方已发药', 'admin', NOW(), 'admin', NOW(), b'0');

-- hospital_bill_pay_status: 支付状态 (int value)
INSERT IGNORE INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES
(20051, 1, '未支付', '0', 'hospital_bill_pay_status', 0, 'danger',  '', '账单未支付', 'admin', NOW(), 'admin', NOW(), b'0'),
(20052, 2, '已支付', '1', 'hospital_bill_pay_status', 0, 'success', '', '账单已支付', 'admin', NOW(), 'admin', NOW(), b'0'),
(20053, 3, '已退费', '2', 'hospital_bill_pay_status', 0, 'info',    '', '账单已退费', 'admin', NOW(), 'admin', NOW(), b'0');

-- hospital_bill_pay_method: 支付方式 (int value, used by getIntDictOptions)
INSERT IGNORE INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES
(20061, 1, '现金',   '0', 'hospital_bill_pay_method', 0, 'info',    '', '现金支付',   'admin', NOW(), 'admin', NOW(), b'0'),
(20062, 2, '微信',   '1', 'hospital_bill_pay_method', 0, 'primary', '', '微信支付',   'admin', NOW(), 'admin', NOW(), b'0'),
(20063, 3, '支付宝', '2', 'hospital_bill_pay_method', 0, 'primary', '', '支付宝支付', 'admin', NOW(), 'admin', NOW(), b'0'),
(20064, 4, '医保',   '3', 'hospital_bill_pay_method', 0, 'success', '', '医保支付',   'admin', NOW(), 'admin', NOW(), b'0');

-- hospital_patient_gender: 患者性别 (int value)
INSERT IGNORE INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES
(20071, 1, '男', '0', 'hospital_patient_gender', 0, 'primary', '', '男', 'admin', NOW(), 'admin', NOW(), b'0'),
(20072, 2, '女', '1', 'hospital_patient_gender', 0, 'danger',  '', '女', 'admin', NOW(), 'admin', NOW(), b'0');

-- hospital_doctor_title: 医生职称 (int value)
INSERT IGNORE INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES
(20081, 1, '住院医师',   '0', 'hospital_doctor_title', 0, 'info',    '', '住院医师',   'admin', NOW(), 'admin', NOW(), b'0'),
(20082, 2, '主治医师',   '1', 'hospital_doctor_title', 0, 'primary', '', '主治医师',   'admin', NOW(), 'admin', NOW(), b'0'),
(20083, 3, '副主任医师', '2', 'hospital_doctor_title', 0, 'success', '', '副主任医师', 'admin', NOW(), 'admin', NOW(), b'0'),
(20084, 4, '主任医师',   '3', 'hospital_doctor_title', 0, 'danger',  '', '主任医师',   'admin', NOW(), 'admin', NOW(), b'0');
