package cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Schema(description = "管理后台 - AI 辅助诊断 Request VO")
@Data
public class AssistDiagnosisReqVO {
    @Schema(description = "症状自然语言描述", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "症状描述不能为空")
    private String symptomDescription;
}
