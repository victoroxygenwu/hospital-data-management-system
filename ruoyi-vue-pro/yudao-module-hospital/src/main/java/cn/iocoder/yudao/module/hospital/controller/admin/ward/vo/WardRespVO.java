package cn.iocoder.yudao.module.hospital.controller.admin.ward.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 病房信息 Response VO")
@Data
public class WardRespVO {
    @Schema(description = "病房ID")
    private Long id;
    @Schema(description = "所属科室ID")
    private Long deptId;
    @Schema(description = "病房编号")
    private String wardNo;
    @Schema(description = "病房类型")
    private String type;
    @Schema(description = "总床位数")
    private Integer capacity;
    @Schema(description = "已使用床位数")
    private Integer usedBeds;
    @Schema(description = "病房状态")
    private Integer status;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
