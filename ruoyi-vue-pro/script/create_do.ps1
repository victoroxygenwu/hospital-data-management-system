$baseDir = "c:\Users\17404\Desktop\数据库原理\hospital-data-management-system\ruoyi-vue-pro\yudao-module-hospital\src\main\java\cn\iocoder\yudao\module\hospital"

# package-info.java
@"
package cn.iocoder.yudao.module.hospital;
"@ | Set-Content -Path "$baseDir\package-info.java" -Encoding UTF8

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

Write-Host "All DO classes created successfully"
