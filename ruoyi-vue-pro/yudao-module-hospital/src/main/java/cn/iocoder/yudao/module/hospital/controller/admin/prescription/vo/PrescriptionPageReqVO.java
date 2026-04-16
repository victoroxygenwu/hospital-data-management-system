package cn.iocoder.yudao.module.hospital.controller.admin.prescription.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 处方分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class PrescriptionPageReqVO extends PageParam {
    @Schema(description = "关联的就诊记录ID")
    private Long visitId;
    @Schema(description = "开方医生ID")
    private Long doctorId;
    @Schema(description = "状态")
    private String status;
}
