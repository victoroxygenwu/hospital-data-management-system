package cn.iocoder.yudao.module.hospital.enums;

import lombok.Getter;

/**
 * 床位占用状态枚举（单一权威来源）
 *
 * 流程：空闲(0) → 占用(1)
 */
@Getter
public enum BedStatusEnum {

    FREE(0, "空闲"),
    OCCUPIED(1, "占用");

    private final Integer status;
    private final String desc;

    BedStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static BedStatusEnum fromStatus(Integer status) {
        if (status == null) {
            return null;
        }
        for (BedStatusEnum e : values()) {
            if (e.getStatus().equals(status)) {
                return e;
            }
        }
        return null;
    }
}
