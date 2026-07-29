package cn.iocoder.yudao.module.hospital.controller.admin.visual.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 疾病季节性趋势 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseSeasonalVO {

    @Schema(description = "月份 1-12")
    private Integer month;
    @Schema(description = "诊断（方案 a 填 ALL）")
    private String diagnosis;
    @Schema(description = "数量")
    private Long count;
}
