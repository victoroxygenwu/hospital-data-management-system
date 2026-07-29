package cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - AI 处方审核 Request VO")
@Data
public class PrescriptionReviewReqVO {
    @Schema(description = "处方 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "处方 ID 不能为空")
    private Long prescriptionId;
}
