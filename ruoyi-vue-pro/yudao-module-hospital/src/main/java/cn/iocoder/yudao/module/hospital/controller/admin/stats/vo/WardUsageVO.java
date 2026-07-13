package cn.iocoder.yudao.module.hospital.controller.admin.stats.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 床位使用率统计 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WardUsageVO {

    @Schema(description = "病房ID", example = "1")
    private Long wardId;

    @Schema(description = "病房号", example = "301")
    private String wardNo;

    @Schema(description = "总床位数", example = "6")
    private Integer capacity;

    @Schema(description = "已用床位数", example = "4")
    private Integer usedBeds;

    @Schema(description = "使用率", example = "66.7%")
    private String usageRate;
}
