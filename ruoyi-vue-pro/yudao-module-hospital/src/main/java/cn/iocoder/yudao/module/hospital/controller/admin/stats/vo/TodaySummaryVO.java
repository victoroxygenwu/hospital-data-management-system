package cn.iocoder.yudao.module.hospital.controller.admin.stats.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 今日概览 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodaySummaryVO {

    @Schema(description = "今日门诊量")
    private Long outpatientCount;
    @Schema(description = "今日收入")
    private BigDecimal todayIncome;
    @Schema(description = "在院人数")
    private Long inpatientCount;
    @Schema(description = "待处理账单数")
    private Long pendingBills;
}
