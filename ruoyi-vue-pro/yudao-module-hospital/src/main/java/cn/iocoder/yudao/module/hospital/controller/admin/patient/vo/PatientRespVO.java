package cn.iocoder.yudao.module.hospital.controller.admin.patient.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 病人信息 Response VO")
@Data
public class PatientRespVO {
    @Schema(description = "病人ID")
    private Long id;
    @Schema(description = "关联系统用户ID")
    private Long userId;
    @Schema(description = "患者姓名")
    private String name;
    @Schema(description = "性别")
    private String gender;
    @Schema(description = "出生日期")
    private LocalDate birthDate;
    @Schema(description = "身份证号")
    private String idCard;
    @Schema(description = "联系电话")
    private String phone;
    @Schema(description = "家庭住址")
    private String address;
    @Schema(description = "紧急联系人姓名")
    private String emergencyContact;
    @Schema(description = "紧急联系人电话")
    private String emergencyPhone;
    @Schema(description = "医保卡号")
    private String insuranceNo;
    @Schema(description = "既往病史")
    private String medicalHistory;
    @Schema(description = "最近入院时间")
    private LocalDate admissionDate;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
