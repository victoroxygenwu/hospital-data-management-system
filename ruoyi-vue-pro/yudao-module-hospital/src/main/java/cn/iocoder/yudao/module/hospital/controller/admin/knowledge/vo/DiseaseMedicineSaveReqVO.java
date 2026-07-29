package cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 疾病-药品关联创建/修改 Request VO")
@Data
public class DiseaseMedicineSaveReqVO {
    @Schema(description = "关联 ID")
    private Long id;
    @Schema(description = "疾病 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "疾病 ID 不能为空")
    private Long diseaseId;
    @Schema(description = "药品 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "药品 ID 不能为空")
    private Long medicineId;
    @Schema(description = "用药类型：1首选 2备选 3辅助")
    private Integer usageType;
}
