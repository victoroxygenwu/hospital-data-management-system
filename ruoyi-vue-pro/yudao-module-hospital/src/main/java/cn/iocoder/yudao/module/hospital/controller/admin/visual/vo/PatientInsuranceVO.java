package cn.iocoder.yudao.module.hospital.controller.admin.visual.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 患者医保占比 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientInsuranceVO {

    @Schema(description = "是否有医保")
    private Boolean hasInsurance;
    @Schema(description = "人数")
    private Long count;
}
