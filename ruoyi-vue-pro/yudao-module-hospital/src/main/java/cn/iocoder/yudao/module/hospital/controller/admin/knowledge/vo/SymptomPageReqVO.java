package cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 症状分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class SymptomPageReqVO extends PageParam {
    @Schema(description = "症状名称，模糊匹配")
    private String name;
    @Schema(description = "部位")
    private String location;
    @Schema(description = "症状类型")
    private String type;
}
