package cn.iocoder.yudao.module.hospital.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.math.BigDecimal;

@TableName("hospital_prescription_item")
@KeySequence("hospital_prescription_item_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
/** 处方明细 DO */
public class PrescriptionItemDO extends BaseDO {
    /** 主键ID */
    @TableId
    private Long id;
    /** 处方ID */
    private Long prescriptionId;
    /** 药品ID */
    private Long medicineId;
    private Integer quantity;
    private BigDecimal price;
    private String instructions;
}
