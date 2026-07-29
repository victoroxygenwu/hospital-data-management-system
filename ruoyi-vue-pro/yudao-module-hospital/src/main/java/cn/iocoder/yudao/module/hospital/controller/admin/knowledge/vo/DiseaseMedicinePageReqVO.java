package cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 疾病-药品关联分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DiseaseMedicinePageReqVO extends PageParam {
    @Schema(description = "疾病 ID")
    private Long diseaseId;
    @Schema(description = "药品 ID")
    private Long medicineId;
    @Schema(description = "用药类型：1首选 2备选 3辅助")
    private Integer usageType;
}
