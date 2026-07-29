package cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Schema(description = "管理后台 - 症状创建/修改 Request VO")
@Data
public class SymptomSaveReqVO {
    @Schema(description = "症状 ID")
    private Long id;
    @Schema(description = "症状名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "症状名称不能为空")
    private String name;
    @Schema(description = "部位")
    private String location;
    @Schema(description = "症状类型")
    private String type;
    @Schema(description = "症状描述")
    private String description;
}
