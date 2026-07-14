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
/** 患者 DO */
public class PatientDO extends BaseDO {
    /** 主键ID */
    @TableId
    private Long id;
    /** 系统用户ID */
    private Long userId;
    /** 患者姓名 */
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
