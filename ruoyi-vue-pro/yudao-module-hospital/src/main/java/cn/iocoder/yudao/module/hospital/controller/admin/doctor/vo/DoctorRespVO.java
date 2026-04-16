package cn.iocoder.yudao.module.hospital.controller.admin.doctor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 医生信息 Response VO")
@Data
public class DoctorRespVO {
    @Schema(description = "医生ID")
    private Long id;
    @Schema(description = "关联系统用户ID")
    private Long userId;
    @Schema(description = "所属科室ID")
    private Long deptId;
    @Schema(description = "医生姓名")
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
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
