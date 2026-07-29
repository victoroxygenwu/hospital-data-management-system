package cn.iocoder.yudao.module.hospital.controller.admin.visual.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 接诊热力图 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeatmapVO {

    @Schema(description = "科室名称")
    private String deptName;
    @Schema(description = "小时 0-23")
    private Integer hour;
    @Schema(description = "接诊数量")
    private Long count;
}
