package cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 疾病-症状关联创建 Request VO")
@Data
public class DiseaseSymptomSaveReqVO {
    @Schema(description = "关联 ID")
    private Long id;
    @Schema(description = "疾病 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "疾病 ID 不能为空")
    private Long diseaseId;
    @Schema(description = "症状 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "症状 ID 不能为空")
    private Long symptomId;
    @Schema(description = "关联强度：1主要 2次要 3偶见")
    private Integer strength;
    @Schema(description = "文献/指南依据")
    private String reference;
}
