package cn.iocoder.yudao.module.hospital.controller.admin.patient.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 病人分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class PatientPageReqVO extends PageParam {
    @Schema(description = "患者姓名，模糊匹配")
    private String name;
    @Schema(description = "联系电话")
    private String phone;
    @Schema(description = "身份证号")
    private String idCard;
}
