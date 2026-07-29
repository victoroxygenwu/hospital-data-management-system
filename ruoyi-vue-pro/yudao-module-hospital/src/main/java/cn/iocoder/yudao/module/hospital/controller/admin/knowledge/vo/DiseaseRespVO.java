package cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 疾病信息 Response VO")
@Data
public class DiseaseRespVO {
    @Schema(description = "疾病 ID")
    private Long id;
    @Schema(description = "疾病名称")
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
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
