package cn.iocoder.yudao.module.hospital.controller.admin.bed.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Schema(description = "管理后台 - 床位创建/修改 Request VO")
@Data
public class BedSaveReqVO {
    @Schema(description = "床位ID")
    private Long id;
    @Schema(description = "所属病房ID")
    private Long wardId;
    @Schema(description = "床位号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "床位号不能为空")
    private String bedNo;
    @Schema(description = "状态：空闲/已占用/维护中")
    private String status;
    @Schema(description = "当前入住的患者ID")
    private Long patientId;
}
