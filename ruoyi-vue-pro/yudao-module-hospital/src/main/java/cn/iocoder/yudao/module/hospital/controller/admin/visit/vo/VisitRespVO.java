package cn.iocoder.yudao.module.hospital.controller.admin.visit.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 就诊记录 Response VO")
@Data
public class VisitRespVO {
    @Schema(description = "就诊ID")
    private Long id;
    @Schema(description = "患者ID")
    private Long patientId;
    @Schema(description = "主治医生ID")
    private Long doctorId;
    @Schema(description = "就诊科室ID")
    private Long deptId;
    @Schema(description = "就诊时间")
    private LocalDateTime visitDate;
    @Schema(description = "就诊原因")
    private String reason;
    @Schema(description = "诊断结果")
    private String diagnosis;
    @Schema(description = "医生备注")
    private String notes;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
