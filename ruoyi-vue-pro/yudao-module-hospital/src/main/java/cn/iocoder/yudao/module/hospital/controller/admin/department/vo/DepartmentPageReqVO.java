package cn.iocoder.yudao.module.hospital.controller.admin.department.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 科室分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DepartmentPageReqVO extends PageParam {
    @Schema(description = "科室名称，模糊匹配", example = "内科")
    private String deptName;
    @Schema(description = "科室联系电话")
    private String phone;
}
