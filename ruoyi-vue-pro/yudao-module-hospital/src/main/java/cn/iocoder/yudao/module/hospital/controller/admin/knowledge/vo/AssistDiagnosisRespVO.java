package cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - AI 辅助诊断 Response VO")
@Data
public class AssistDiagnosisRespVO {

    @Schema(description = "提取的症状列表")
    private List<String> extractedSymptoms;
    @Schema(description = "匹配的疾病列表")
    private List<MatchedDiseaseVO> matchedDiseases;
    @Schema(description = "建议就诊科室")
    private String referenceDept;
    @Schema(description = "紧急程度")
    private String urgency;
    @Schema(description = "建议")
    private String advice;
    @Schema(description = "是否离线降级（AI 不可用时为 true）")
    private Boolean offline;

    @Schema(description = "匹配疾病")
    @Data
    public static class MatchedDiseaseVO {
        @Schema(description = "疾病名称")
        private String name;
        @Schema(description = "ICD 编码")
        private String icdCode;
        @Schema(description = "匹配症状数")
        private Integer matchCount;
        @Schema(description = "总输入症状数")
        private Integer totalSymptoms;
    }

}
