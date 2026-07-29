package cn.iocoder.yudao.module.hospital.controller.admin.visual.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "管理后台 - 患者画像聚合 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientProfileRespVO {

    @Schema(description = "年龄金字塔")
    private List<PatientAgeVO> ageList;
    @Schema(description = "地区 TOP10")
    private List<PatientRegionVO> regionList;
    @Schema(description = "医保占比")
    private List<PatientInsuranceVO> insuranceList;
}
