package cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 疾病-药品关联 Response VO")
@Data
public class DiseaseMedicineRespVO {
    @Schema(description = "关联 ID")
    private Long id;
    @Schema(description = "疾病 ID")
    private Long diseaseId;
    @Schema(description = "疾病名称")
    private String diseaseName;
    @Schema(description = "药品 ID")
    private Long medicineId;
    @Schema(description = "药品名称")
    private String medicineName;
    @Schema(description = "用药类型：1首选 2备选 3辅助")
    private Integer usageType;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
