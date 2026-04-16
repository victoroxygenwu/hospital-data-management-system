package cn.iocoder.yudao.module.hospital.controller.admin.prescription.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Schema(description = "管理后台 - 处方创建/修改 Request VO")
@Data
public class PrescriptionSaveReqVO {
    @Schema(description = "处方ID")
    private Long id;
    @Schema(description = "关联的就诊记录ID")
    private Long visitId;
    @Schema(description = "开方医生ID")
    private Long doctorId;
    @Schema(description = "状态：未发药/已发药/已过期")
    private String status;
    @Schema(description = "处方备注")
    private String notes;
    @Schema(description = "处方明细列表")
    private List<PrescriptionItemSaveVO> items;

    @Data
    public static class PrescriptionItemSaveVO {
        @Schema(description = "明细ID（修改时传入）")
        private Long id;
        @Schema(description = "药品ID")
        private Long medicineId;
        @Schema(description = "数量")
        private Integer quantity;
        @Schema(description = "用法说明")
        private String instructions;
    }
}
