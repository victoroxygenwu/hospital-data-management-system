package cn.iocoder.yudao.module.hospital.controller.admin.stats.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 就诊趋势统计 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitTrendVO {

    @Schema(description = "日期", example = "2026-07-13")
    private String date;

    @Schema(description = "就诊数量", example = "25")
    private Long count;
}
