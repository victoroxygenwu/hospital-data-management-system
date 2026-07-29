package cn.iocoder.yudao.module.hospital.controller.admin.visual.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 药品联合使用 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineCooccurrenceVO {

    @Schema(description = "药品 A")
    private String medicineA;
    @Schema(description = "药品 B")
    private String medicineB;
    @Schema(description = "共现次数")
    private Long coCount;
}
