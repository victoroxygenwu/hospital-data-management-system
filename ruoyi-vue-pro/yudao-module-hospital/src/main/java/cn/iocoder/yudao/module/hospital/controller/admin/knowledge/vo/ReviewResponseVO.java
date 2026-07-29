package cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - AI 处方审核 Response VO")
@Data
public class ReviewResponseVO {

    @Schema(description = "风险等级：LOW/MEDIUM/HIGH")
    private String riskLevel;
    @Schema(description = "药品相互作用列表")
    private List<InteractionVO> interactions;
    @Schema(description = "审核建议")
    private String advice;
    @Schema(description = "是否离线降级（AI 不可用时为 true）")
    private Boolean offline;

    @Schema(description = "药品相互作用")
    @Data
    public static class InteractionVO {
        @Schema(description = "药品 A")
        private String medicineA;
        @Schema(description = "药品 B")
        private String medicineB;
        @Schema(description = "说明")
        private String note;
    }

}
