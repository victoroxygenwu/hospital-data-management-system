package cn.iocoder.yudao.module.hospital.controller.admin.stats.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 药品库存统计 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineStockVO {

    @Schema(description = "药品ID", example = "1")
    private Long id;

    @Schema(description = "药品名称", example = "阿莫西林")
    private String name;

    @Schema(description = "规格", example = "0.25g×24粒")
    private String specification;

    @Schema(description = "单位", example = "盒")
    private String unit;

    @Schema(description = "库存数量", example = "50")
    private Integer stock;

    @Schema(description = "有效期", example = "2027-06-30")
    private String expiryDate;

    @Schema(description = "库存预警（库存<10时预警）", example = "false")
    private Boolean stockWarning;
}
