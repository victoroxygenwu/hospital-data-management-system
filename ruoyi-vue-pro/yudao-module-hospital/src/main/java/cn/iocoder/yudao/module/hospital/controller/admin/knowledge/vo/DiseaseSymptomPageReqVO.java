package cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 疾病-症状关联分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DiseaseSymptomPageReqVO extends PageParam {
    @Schema(description = "疾病 ID")
    private Long diseaseId;
    @Schema(description = "症状 ID")
    private Long symptomId;
    @Schema(description = "关联强度：1主要 2次要 3偶见")
    private Integer strength;
}
