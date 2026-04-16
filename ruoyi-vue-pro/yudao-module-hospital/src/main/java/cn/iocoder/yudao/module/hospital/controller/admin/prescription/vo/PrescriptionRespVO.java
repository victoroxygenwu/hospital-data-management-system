package cn.iocoder.yudao.module.hospital.controller.admin.prescription.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 处方信息 Response VO")
@Data
public class PrescriptionRespVO {
    @Schema(description = "处方ID")
    private Long id;
    @Schema(description = "关联的就诊记录ID")
    private Long visitId;
    @Schema(description = "开方医生ID")
    private Long doctorId;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "处方备注")
    private String notes;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "处方明细列表")
    private List<PrescriptionItemRespVO> items;

    @Data
    public static class PrescriptionItemRespVO {
        @Schema(description = "明细ID")
        private Long id;
        @Schema(description = "药品ID")
        private Long medicineId;
        @Schema(description = "数量")
        private Integer quantity;
        @Schema(description = "当时的单价")
        private BigDecimal price;
        @Schema(description = "用法说明")
        private String instructions;
    }
}
