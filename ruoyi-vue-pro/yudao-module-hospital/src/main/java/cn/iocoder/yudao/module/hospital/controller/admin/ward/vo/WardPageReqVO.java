package cn.iocoder.yudao.module.hospital.controller.admin.ward.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 病房分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class WardPageReqVO extends PageParam {
    @Schema(description = "所属科室ID")
    private Long deptId;
    @Schema(description = "病房编号")
    private String wardNo;
    @Schema(description = "病房类型")
    private String type;
    @Schema(description = "病房状态")
    private Integer status;
}
