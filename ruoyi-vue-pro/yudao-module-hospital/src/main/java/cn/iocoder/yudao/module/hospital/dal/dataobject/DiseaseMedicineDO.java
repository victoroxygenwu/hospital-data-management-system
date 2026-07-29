package cn.iocoder.yudao.module.hospital.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("hospital_disease_medicine")
@KeySequence("hospital_disease_medicine_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseMedicineDO extends BaseDO {
    @TableId
    private Long id;
    private Long diseaseId;
    private Long medicineId;
    /** 1=首选 2=备选 3=辅助 */
    private Integer usageType;
}
