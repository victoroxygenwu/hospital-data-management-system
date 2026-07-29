package cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 疾病分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DiseasePageReqVO extends PageParam {
    @Schema(description = "疾病名称，模糊匹配")
    private String name;
    @Schema(description = "ICD 编码")
    private String icdCode;
    @Schema(description = "疾病分类")
    private String category;
    @Schema(description = "关联科室 ID")
    private Long deptId;
    @Schema(description = "是否常见：0否 1是")
    private Integer isCommon;
}
