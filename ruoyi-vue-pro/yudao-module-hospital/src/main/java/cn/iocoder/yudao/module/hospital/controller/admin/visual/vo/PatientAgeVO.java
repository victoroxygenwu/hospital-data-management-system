package cn.iocoder.yudao.module.hospital.controller.admin.visual.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 患者年龄分布 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientAgeVO {

    @Schema(description = "性别")
    private String gender;
    @Schema(description = "年龄段")
    private String ageGroup;
    @Schema(description = "人数")
    private Long count;
}
