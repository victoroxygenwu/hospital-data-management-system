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

    @Schema(description = "被审核的原始处方快照（便于查看审核依据）")
    private PrescriptionSnapshotVO prescription;

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

    @Schema(description = "原处方快照（审核对象）")
    @Data
    public static class PrescriptionSnapshotVO {
        @Schema(description = "处方ID")
        private Long id;
        @Schema(description = "关联就诊ID")
        private Long visitId;
        @Schema(description = "临床诊断")
        private String diagnosis;
        @Schema(description = "处方状态：0待发药 / 1已发药")
        private Integer status;
        @Schema(description = "开具时间")
        private java.time.LocalDateTime createTime;
        @Schema(description = "处方明细")
        private List<PrescriptionItemSnapshotVO> items;
    }

    @Schema(description = "原处方明细快照")
    @Data
    public static class PrescriptionItemSnapshotVO {
        @Schema(description = "药品名称")
        private String medicineName;
        @Schema(description = "规格")
        private String specification;
        @Schema(description = "数量")
        private Integer quantity;
        @Schema(description = "单价（元）")
        private java.math.BigDecimal price;
        @Schema(description = "用法用量")
        private String instructions;
    }

}
