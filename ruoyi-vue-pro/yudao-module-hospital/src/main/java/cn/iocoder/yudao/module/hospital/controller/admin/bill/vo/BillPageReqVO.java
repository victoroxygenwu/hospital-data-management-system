package cn.iocoder.yudao.module.hospital.controller.admin.bill.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 账单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class BillPageReqVO extends PageParam {
    @Schema(description = "关联的就诊记录ID")
    private Long visitId;
    @Schema(description = "患者ID")
    private Long patientId;
    @Schema(description = "状态")
    private String status;
}
