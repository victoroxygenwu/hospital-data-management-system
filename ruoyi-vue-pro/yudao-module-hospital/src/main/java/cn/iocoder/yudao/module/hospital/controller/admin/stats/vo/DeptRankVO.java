package cn.iocoder.yudao.module.hospital.controller.admin.stats.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 科室接诊排行 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeptRankVO {

    @Schema(description = "科室名称")
    private String deptName;
    @Schema(description = "接诊量")
    private Long visitCount;
}
