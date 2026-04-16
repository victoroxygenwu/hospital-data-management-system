package cn.iocoder.yudao.module.hospital.controller.admin.bill.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 账单创建/修改 Request VO")
@Data
public class BillSaveReqVO {
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
    @Schema(description = "支付方式：现金/医保/微信")
    private String payMethod;
    @Schema(description = "状态：未支付/已支付/已退费")
    private String status;
}
