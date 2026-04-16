package cn.iocoder.yudao.module.hospital.controller.admin.doctor.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 医生分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DoctorPageReqVO extends PageParam {
    @Schema(description = "医生姓名，模糊匹配")
    private String name;
    @Schema(description = "所属科室ID")
    private Long deptId;
    @Schema(description = "职称")
    private String title;
}
