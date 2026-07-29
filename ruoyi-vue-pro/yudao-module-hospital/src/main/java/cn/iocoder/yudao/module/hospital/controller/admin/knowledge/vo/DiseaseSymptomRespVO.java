package cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 疾病-症状关联 Response VO")
@Data
public class DiseaseSymptomRespVO {
    @Schema(description = "关联 ID")
    private Long id;
    @Schema(description = "疾病 ID")
    private Long diseaseId;
    @Schema(description = "疾病名称")
    private String diseaseName;
    @Schema(description = "症状 ID")
    private Long symptomId;
    @Schema(description = "症状名称")
    private String symptomName;
    @Schema(description = "关联强度：1主要 2次要 3偶见")
    private Integer strength;
    @Schema(description = "文献/指南依据")
    private String reference;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
