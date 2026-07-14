package cn.iocoder.yudao.module.hospital.enums;

import lombok.Getter;

/**
 * 就诊状态枚举（单一权威来源）
 *
 * 业务流程：待就诊(0) → 就诊中(1) → 已完成(2)
 * 终态：已完成(2) / 已取消(3)，终态后状态不可再变更
 */
@Getter
public enum VisitStatusEnum {

    PENDING(0, "待就诊"),
    IN_PROGRESS(1, "就诊中"),
    COMPLETED(2, "已完成"),
    CANCELLED(3, "已取消");

    private final Integer status;
    private final String desc;

    VisitStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static VisitStatusEnum fromStatus(Integer status) {
        if (status == null) {
            return null;
        }
        for (VisitStatusEnum e : values()) {
            if (e.getStatus().equals(status)) {
                return e;
            }
        }
        return null;
    }

    /** 是否终态：已完成/已取消后状态不可再变更 */
    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED;
    }
}
