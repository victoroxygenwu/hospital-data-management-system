$baseDir = "c:\Users\17404\Desktop\数据库原理\hospital-data-management-system\ruoyi-vue-pro\yudao-module-hospital\src\main\java\cn\iocoder\yudao\module\hospital"
$dirs = @("dal\dataobject","dal\mysql","enums","controller\admin\department\vo","controller\admin\doctor\vo","controller\admin\patient\vo","controller\admin\ward\vo","controller\admin\bed\vo","controller\admin\visit\vo","controller\admin\medicine\vo","controller\admin\prescription\vo","controller\admin\bill\vo","controller\admin\stats","service\department","service\doctor","service\patient","service\ward","service\bed","service\visit","service\medicine","service\prescription","service\bill","service\stats")
foreach ($d in $dirs) { New-Item -ItemType Directory -Force -Path "$baseDir\$d" | Out-Null }

# ===== package-info.java =====
"package cn.iocoder.yudao.module.hospital;" | Set-Content -Path "$baseDir\package-info.java" -Encoding UTF8

# ===== ErrorCodeConstants =====
@"
package cn.iocoder.yudao.module.hospital.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {
    // ========== 科室 1-100-001-000 ==========
    ErrorCode DEPARTMENT_NOT_EXISTS = new ErrorCode(1_100_001_000, "科室不存在");
    ErrorCode DEPARTMENT_NAME_DUPLICATE = new ErrorCode(1_100_001_001, "已存在该名称的科室");

    // ========== 医生 1-100-002-000 ==========
    ErrorCode DOCTOR_NOT_EXISTS = new ErrorCode(1_100_002_000, "医生不存在");

    // ========== 病人 1-100-003-000 ==========
    ErrorCode PATIENT_NOT_EXISTS = new ErrorCode(1_100_003_000, "病人不存在");

    // ========== 病房 1-100-004-000 ==========
    ErrorCode WARD_NOT_EXISTS = new ErrorCode(1_100_004_000, "病房不存在");
    ErrorCode WARD_NO_DUPLICATE = new ErrorCode(1_100_004_001, "已存在该编号的病房");

    // ========== 床位 1-100-005-000 ==========
    ErrorCode BED_NOT_EXISTS = new ErrorCode(1_100_005_000, "床位不存在");
    ErrorCode BED_ALREADY_OCCUPIED = new ErrorCode(1_100_005_001, "床位已被占用");
    ErrorCode BED_NOT_OCCUPIED = new ErrorCode(1_100_005_002, "床位未被占用");

    // ========== 就诊 1-100-006-000 ==========
    ErrorCode VISIT_NOT_EXISTS = new ErrorCode(1_100_006_000, "就诊记录不存在");

    // ========== 药品 1-100-007-000 ==========
    ErrorCode MEDICINE_NOT_EXISTS = new ErrorCode(1_100_007_000, "药品不存在");
    ErrorCode MEDICINE_STOCK_NOT_ENOUGH = new ErrorCode(1_100_007_001, "药品库存不足");

    // ========== 处方 1-100-008-000 ==========
    ErrorCode PRESCRIPTION_NOT_EXISTS = new ErrorCode(1_100_008_000, "处方不存在");
    ErrorCode PRESCRIPTION_ITEM_NOT_EXISTS = new ErrorCode(1_100_008_001, "处方明细不存在");

    // ========== 账单 1-100-009-000 ==========
    ErrorCode BILL_NOT_EXISTS = new ErrorCode(1_100_009_000, "账单不存在");
    ErrorCode BILL_ALREADY_PAID = new ErrorCode(1_100_009_001, "账单已支付");
}
"@ | Set-Content -Path "$baseDir\enums\ErrorCodeConstants.java" -Encoding UTF8

# ===== DO Classes =====
# DepartmentDO
@"
package cn.iocoder.yudao.module.hospital.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("hospital_department")
@KeySequence("hospital_department_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDO extends BaseDO {
    @TableId
    private Long id;
    private String deptName;
    private String phone;
    private String manager;
    private String location;
    private String description;
}
"@ | Set-Content -Path "$baseDir\dal\dataobject\DepartmentDO.java" -Encoding UTF8

# DoctorDO
@"
package cn.iocoder.yudao.module.hospital.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("hospital_doctor")
@KeySequence("hospital_doctor_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDO extends BaseDO {
    @TableId
    private Long id;
    private Long userId;
    private Long deptId;
    private String name;
    private String gender;
    private Integer age;
    private String title;
    private String licenseNo;
    private String phone;
    private String email;
}
"@ | Set-Content -Path "$baseDir\dal\dataobject\DoctorDO.java" -Encoding UTF8

# PatientDO
@"
package cn.iocoder.yudao.module.hospital.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.time.LocalDate;

@TableName("hospital_patient")
@KeySequence("hospital_patient_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDO extends BaseDO {
    @TableId
    private Long id;
    private Long userId;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String idCard;
    private String phone;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;
    private String insuranceNo;
    private String medicalHistory;
    private LocalDate admissionDate;
}
"@ | Set-Content -Path "$baseDir\dal\dataobject\PatientDO.java" -Encoding UTF8

# WardDO
@"
package cn.iocoder.yudao.module.hospital.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("hospital_ward")
@KeySequence("hospital_ward_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WardDO extends BaseDO {
    @TableId
    private Long id;
    private Long deptId;
    private String wardNo;
    private String type;
    private Integer capacity;
    private Integer usedBeds;
    private Integer status;
    private String description;
}
"@ | Set-Content -Path "$baseDir\dal\dataobject\WardDO.java" -Encoding UTF8

# BedDO
@"
package cn.iocoder.yudao.module.hospital.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.time.LocalDateTime;

@TableName("hospital_bed")
@KeySequence("hospital_bed_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BedDO extends BaseDO {
    @TableId
    private Long id;
    private Long wardId;
    private String bedNo;
    private String status;
    private Long patientId;
    private LocalDateTime admissionTime;
}
"@ | Set-Content -Path "$baseDir\dal\dataobject\BedDO.java" -Encoding UTF8

# VisitDO
@"
package cn.iocoder.yudao.module.hospital.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.time.LocalDateTime;

@TableName("hospital_visit")
@KeySequence("hospital_visit_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitDO extends BaseDO {
    @TableId
    private Long id;
    private Long patientId;
    private Long doctorId;
    private Long deptId;
    private LocalDateTime visitDate;
    private String reason;
    private String diagnosis;
    private String notes;
    private String status;
}
"@ | Set-Content -Path "$baseDir\dal\dataobject\VisitDO.java" -Encoding UTF8

# MedicineDO
@"
package cn.iocoder.yudao.module.hospital.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("hospital_medicine")
@KeySequence("hospital_medicine_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineDO extends BaseDO {
    @TableId
    private Long id;
    private String name;
    private String specification;
    private String unit;
    private BigDecimal price;
    private Integer stock;
    private String manufacturer;
    private LocalDate expiryDate;
}
"@ | Set-Content -Path "$baseDir\dal\dataobject\MedicineDO.java" -Encoding UTF8

# PrescriptionDO
@"
package cn.iocoder.yudao.module.hospital.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("hospital_prescription")
@KeySequence("hospital_prescription_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDO extends BaseDO {
    @TableId
    private Long id;
    private Long visitId;
    private Long doctorId;
    private String status;
    private String notes;
}
"@ | Set-Content -Path "$baseDir\dal\dataobject\PrescriptionDO.java" -Encoding UTF8

# PrescriptionItemDO
@"
package cn.iocoder.yudao.module.hospital.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.math.BigDecimal;

@TableName("hospital_prescription_item")
@KeySequence("hospital_prescription_item_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionItemDO extends BaseDO {
    @TableId
    private Long id;
    private Long prescriptionId;
    private Long medicineId;
    private Integer quantity;
    private BigDecimal price;
    private String instructions;
}
"@ | Set-Content -Path "$baseDir\dal\dataobject\PrescriptionItemDO.java" -Encoding UTF8

# BillDO
@"
package cn.iocoder.yudao.module.hospital.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("hospital_bill")
@KeySequence("hospital_bill_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillDO extends BaseDO {
    @TableId
    private Long id;
    private Long visitId;
    private Long patientId;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private LocalDateTime payTime;
    private String payMethod;
    private String status;
}
"@ | Set-Content -Path "$baseDir\dal\dataobject\BillDO.java" -Encoding UTF8

Write-Host "Step 1: DO classes and ErrorCodeConstants created"
