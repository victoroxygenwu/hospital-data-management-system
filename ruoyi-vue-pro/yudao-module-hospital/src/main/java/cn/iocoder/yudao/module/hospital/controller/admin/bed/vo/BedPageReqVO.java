package cn.iocoder.yudao.module.hospital.controller.admin.bed.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 床位分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class BedPageReqVO extends PageParam {
    @Schema(description = "所属病房ID")
    private Long wardId;
    @Schema(description = "床位号")
    private String bedNo;
    @Schema(description = "状态")
    private String status;
}
