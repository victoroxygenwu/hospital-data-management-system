package cn.iocoder.yudao.module.hospital.controller.admin.doctor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Schema(description = "管理后台 - 医生创建/修改 Request VO")
@Data
public class DoctorSaveReqVO {
    @Schema(description = "医生ID", example = "1")
    private Long id;
    @Schema(description = "关联系统用户ID")
    private Long userId;
    @Schema(description = "所属科室ID")
    private Long deptId;
    @Schema(description = "医生姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "李医生")
    @NotBlank(message = "医生姓名不能为空")
    private String name;
    @Schema(description = "性别")
    private String gender;
    @Schema(description = "年龄")
    private Integer age;
    @Schema(description = "职称")
    private String title;
    @Schema(description = "执业医师证编号")
    private String licenseNo;
    @Schema(description = "联系电话")
    private String phone;
    @Schema(description = "邮箱")
    private String email;
}
