package cn.iocoder.yudao.module.hospital.controller.admin.visual.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 患者地区分布 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientRegionVO {

    @Schema(description = "地区（address 前缀近似）")
    private String region;
    @Schema(description = "人数")
    private Long count;
}
