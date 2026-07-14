package cn.iocoder.yudao.module.hospital.enums;

import lombok.Getter;

/**
 * 处方发药状态枚举（单一权威来源）
 *
 * 流程：待发药(0) → 已发药(1)
 */
@Getter
public enum PrescriptionStatusEnum {

    UNDISPENSED(0, "待发药"),
    DISPENSED(1, "已发药");

    private final Integer status;
    private final String desc;

    PrescriptionStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static PrescriptionStatusEnum fromStatus(Integer status) {
        if (status == null) {
            return null;
        }
        for (PrescriptionStatusEnum e : values()) {
            if (e.getStatus().equals(status)) {
                return e;
            }
        }
        return null;
    }
}
