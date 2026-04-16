package cn.iocoder.yudao.module.hospital.controller.admin.visit.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 就诊记录创建/修改 Request VO")
@Data
public class VisitSaveReqVO {
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
    @Schema(description = "状态：待就诊/就诊中/已完成/已取消")
    private String status;
}
