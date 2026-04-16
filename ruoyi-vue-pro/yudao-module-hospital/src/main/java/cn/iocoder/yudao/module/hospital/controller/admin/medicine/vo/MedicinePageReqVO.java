package cn.iocoder.yudao.module.hospital.controller.admin.medicine.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 药品分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class MedicinePageReqVO extends PageParam {
    @Schema(description = "药品名称，模糊匹配")
    private String name;
    @Schema(description = "生产厂家")
    private String manufacturer;
}
