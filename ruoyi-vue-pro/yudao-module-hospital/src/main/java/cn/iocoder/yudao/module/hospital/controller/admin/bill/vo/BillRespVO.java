package cn.iocoder.yudao.module.hospital.controller.admin.bill.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 账单信息 Response VO")
@Data
public class BillRespVO {
    @Schema(description = "账单ID")
    private Long id;
    @Schema(description = "关联的就诊记录ID")
    private Long visitId;
    @Schema(description = "患者ID")
    private Long patientId;
    @Schema(description = "总金额")
    private BigDecimal totalAmount;
    @Schema(description = "已支付金额")
    private BigDecimal payAmount;
    @Schema(description = "支付时间")
    private LocalDateTime payTime;
    @Schema(description = "支付方式")
    private String payMethod;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
