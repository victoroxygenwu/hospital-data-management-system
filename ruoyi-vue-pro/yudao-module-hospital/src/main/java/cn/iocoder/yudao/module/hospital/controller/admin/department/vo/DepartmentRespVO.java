package cn.iocoder.yudao.module.hospital.controller.admin.department.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 科室信息 Response VO")
@Data
public class DepartmentRespVO {
    @Schema(description = "科室ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;
    @Schema(description = "科室名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "内科")
    private String deptName;
    @Schema(description = "科室联系电话")
    private String phone;
    @Schema(description = "科室主任")
    private String manager;
    @Schema(description = "科室位置")
    private String location;
    @Schema(description = "科室描述")
    private String description;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
