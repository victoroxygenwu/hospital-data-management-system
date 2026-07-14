package cn.iocoder.yudao.module.hospital.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {
    // ========== 科室 1-100-001-000 ==========
    ErrorCode DEPARTMENT_NOT_EXISTS = new ErrorCode(1_100_001_000, "科室不存在");

    // ========== 医生 1-100-002-000 ==========
    ErrorCode DOCTOR_NOT_EXISTS = new ErrorCode(1_100_002_000, "医生不存在");
    ErrorCode DOCTOR_HAS_VISITS = new ErrorCode(1_100_002_001, "该医生存在关联就诊记录，无法删除");

    // ========== 病人 1-100-003-000 ==========
    ErrorCode PATIENT_NOT_EXISTS = new ErrorCode(1_100_003_000, "病人不存在");

    // ========== 病房 1-100-004-000 ==========
    ErrorCode WARD_NOT_EXISTS = new ErrorCode(1_100_004_000, "病房不存在");
    ErrorCode WARD_CAPACITY_FULL = new ErrorCode(1_100_004_001, "病房床位已满");
    ErrorCode WARD_NO_BED_TO_RELEASE = new ErrorCode(1_100_004_002, "无已占用床位可释放");
    ErrorCode WARD_BED_EXCEED_CAPACITY = new ErrorCode(1_100_004_003, "床位数量已超过病房总床位，无法继续添加");

    // ========== 床位 1-100-005-000 ==========
    ErrorCode BED_NOT_EXISTS = new ErrorCode(1_100_005_000, "床位不存在");
    ErrorCode BED_ALREADY_OCCUPIED = new ErrorCode(1_100_005_001, "床位已被占用");
    ErrorCode BED_NOT_OCCUPIED = new ErrorCode(1_100_005_002, "床位未被占用");

    // ========== 就诊 1-100-006-000 ==========
    ErrorCode VISIT_NOT_EXISTS = new ErrorCode(1_100_006_000, "就诊记录不存在");
    ErrorCode VISIT_STATUS_ILLEGAL = new ErrorCode(1_100_006_001, "就诊已完成或已取消，状态不可再变更");

    // ========== 药品 1-100-007-000 ==========
    ErrorCode MEDICINE_NOT_EXISTS = new ErrorCode(1_100_007_000, "药品不存在");
    ErrorCode MEDICINE_STOCK_NOT_ENOUGH = new ErrorCode(1_100_007_001, "药品库存不足");
    ErrorCode MEDICINE_HAS_PRESCRIPTION = new ErrorCode(1_100_007_002, "该药品存在关联处方明细，无法删除");

    // ========== 处方 1-100-008-000 ==========
    ErrorCode PRESCRIPTION_NOT_EXISTS = new ErrorCode(1_100_008_000, "处方不存在");

    // ========== 账单 1-100-009-000 ==========
    ErrorCode BILL_NOT_EXISTS = new ErrorCode(1_100_009_000, "账单不存在");
    ErrorCode BILL_ALREADY_PAID = new ErrorCode(1_100_009_001, "账单已支付");

    // ========== 通用 1-100-999-000 ==========
    ErrorCode HOSPITAL_PERMISSION_DENIED = new ErrorCode(1_100_999_000, "无权限执行此操作");
    ErrorCode HOSPITAL_DATA_ACCESS_DENIED = new ErrorCode(1_100_999_001, "无权访问该数据");
}
