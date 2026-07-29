package cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Schema(description = "管理后台 - 疾病创建/修改 Request VO")
@Data
public class DiseaseSaveReqVO {
    @Schema(description = "疾病 ID")
    private Long id;
    @Schema(description = "疾病名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "疾病名称不能为空")
    private String name;
    @Schema(description = "ICD 编码")
    private String icdCode;
    @Schema(description = "疾病分类")
    private String category;
    @Schema(description = "关联科室 ID")
    private Long deptId;
    @Schema(description = "疾病描述")
    private String description;
    @Schema(description = "典型症状描述")
    private String typicalSymptoms;
    @Schema(description = "是否常见：0否 1是")
    private Integer isCommon;
}
