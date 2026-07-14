package cn.iocoder.yudao.module.hospital.enums;

import lombok.Getter;

/**
 * 账单支付状态枚举（单一权威来源）
 *
 * 流程：未支付(0) → 已支付(1)
 */
@Getter
public enum BillStatusEnum {

    UNPAID(0, "未支付"),
    PAID(1, "已支付");

    private final Integer status;
    private final String desc;

    BillStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static BillStatusEnum fromStatus(Integer status) {
        if (status == null) {
            return null;
        }
        for (BillStatusEnum e : values()) {
            if (e.getStatus().equals(status)) {
                return e;
            }
        }
        return null;
    }
}
