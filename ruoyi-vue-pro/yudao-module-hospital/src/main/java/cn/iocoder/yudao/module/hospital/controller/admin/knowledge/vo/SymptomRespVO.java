package cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 症状信息 Response VO")
@Data
public class SymptomRespVO {
    @Schema(description = "症状 ID")
    private Long id;
    @Schema(description = "症状名称")
    private String name;
    @Schema(description = "部位")
    private String location;
    @Schema(description = "症状类型")
    private String type;
    @Schema(description = "症状描述")
    private String description;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
