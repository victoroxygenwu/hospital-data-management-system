package cn.iocoder.yudao.module.hospital.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("hospital_medicine")
@KeySequence("hospital_medicine_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
/** 药品 DO */
public class MedicineDO extends BaseDO {
    /** 主键ID */
    @TableId
    private Long id;
    /** 药品名称 */
    private String name;
    private String specification;
    private String unit;
    private BigDecimal price;
    private Integer stock;
    private String manufacturer;
    private LocalDate expiryDate;
}
