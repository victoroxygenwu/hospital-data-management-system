package cn.iocoder.yudao.module.hospital.controller.admin.visual.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 患者医保类型分布 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientInsuranceVO {

    @Schema(description = "医保类型：职工医保/居民医保/新农合/商业保险/自费")
    private String insuranceType;
    @Schema(description = "人数")
    private Long count;
}
