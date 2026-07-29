package cn.iocoder.yudao.module.hospital.controller.admin.visual.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 科室雷达图 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeptRadarVO {

    @Schema(description = "科室名称")
    private String deptName;
    @Schema(description = "接诊量")
    private Long visitCount;
    @Schema(description = "治愈率 0-1")
    private Double cureRate;
    @Schema(description = "平均费用")
    private Double avgFee;
}
