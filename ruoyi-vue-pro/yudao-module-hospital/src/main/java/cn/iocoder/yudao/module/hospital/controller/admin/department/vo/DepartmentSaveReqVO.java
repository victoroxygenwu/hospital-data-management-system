package cn.iocoder.yudao.module.hospital.controller.admin.department.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Schema(description = "管理后台 - 科室创建/修改 Request VO")
@Data
public class DepartmentSaveReqVO {
    @Schema(description = "科室ID", example = "1")
    private Long id;
    @Schema(description = "科室名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "内科")
    @NotBlank(message = "科室名称不能为空")
    private String deptName;
    @Schema(description = "科室联系电话", example = "010-12345678")
    private String phone;
    @Schema(description = "科室主任", example = "张主任")
    private String manager;
    @Schema(description = "科室位置", example = "1号楼3层")
    private String location;
    @Schema(description = "科室描述")
    private String description;
}
