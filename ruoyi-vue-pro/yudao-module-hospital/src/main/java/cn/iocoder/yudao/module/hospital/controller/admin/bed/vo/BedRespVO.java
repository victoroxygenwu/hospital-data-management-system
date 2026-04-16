package cn.iocoder.yudao.module.hospital.controller.admin.bed.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 床位信息 Response VO")
@Data
public class BedRespVO {
    @Schema(description = "床位ID")
    private Long id;
    @Schema(description = "所属病房ID")
    private Long wardId;
    @Schema(description = "床位号")
    private String bedNo;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "当前入住的患者ID")
    private Long patientId;
    @Schema(description = "入住时间")
    private LocalDateTime admissionTime;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
