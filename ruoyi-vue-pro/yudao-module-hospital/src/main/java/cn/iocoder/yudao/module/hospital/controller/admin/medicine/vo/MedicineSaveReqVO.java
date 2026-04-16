package cn.iocoder.yudao.module.hospital.controller.admin.medicine.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "管理后台 - 药品创建/修改 Request VO")
@Data
public class MedicineSaveReqVO {
    @Schema(description = "药品ID")
    private Long id;
    @Schema(description = "药品名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "药品名称不能为空")
    private String name;
    @Schema(description = "规格")
    private String specification;
    @Schema(description = "单位：盒/片/瓶")
    private String unit;
    @Schema(description = "单价", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "单价不能为空")
    private BigDecimal price;
    @Schema(description = "库存数量")
    private Integer stock;
    @Schema(description = "生产厂家")
    private String manufacturer;
    @Schema(description = "有效期")
    private LocalDate expiryDate;
}
