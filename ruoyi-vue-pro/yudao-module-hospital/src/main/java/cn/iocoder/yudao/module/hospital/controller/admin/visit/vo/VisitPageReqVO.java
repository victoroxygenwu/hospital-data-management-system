package cn.iocoder.yudao.module.hospital.controller.admin.visit.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 就诊记录分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class VisitPageReqVO extends PageParam {
    @Schema(description = "患者ID")
    private Long patientId;
    @Schema(description = "主治医生ID")
    private Long doctorId;
    @Schema(description = "就诊科室ID")
    private Long deptId;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "就诊时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] visitDate;
}
