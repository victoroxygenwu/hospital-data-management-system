package cn.iocoder.yudao.module.hospital.controller.admin.medicine.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 药品信息 Response VO")
@Data
public class MedicineRespVO {
    @Schema(description = "药品ID")
    private Long id;
    @Schema(description = "药品名称")
    private String name;
    @Schema(description = "规格")
    private String specification;
    @Schema(description = "单位")
    private String unit;
    @Schema(description = "单价")
    private BigDecimal price;
    @Schema(description = "库存数量")
    private Integer stock;
    @Schema(description = "生产厂家")
    private String manufacturer;
    @Schema(description = "有效期")
    private LocalDate expiryDate;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
