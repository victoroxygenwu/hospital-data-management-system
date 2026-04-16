package cn.iocoder.yudao.module.hospital.controller.admin.ward.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 病房创建/修改 Request VO")
@Data
public class WardSaveReqVO {
    @Schema(description = "病房ID")
    private Long id;
    @Schema(description = "所属科室ID")
    private Long deptId;
    @Schema(description = "病房编号")
    private String wardNo;
    @Schema(description = "病房类型：普通/ICU/VIP")
    private String type;
    @Schema(description = "病房总床位数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "总床位数不能为空")
    private Integer capacity;
    @Schema(description = "已使用床位数")
    private Integer usedBeds;
    @Schema(description = "病房状态：1可用/0停用")
    private Integer status;
    @Schema(description = "描述")
    private String description;
}
