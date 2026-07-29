package cn.iocoder.yudao.module.hospital.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("hospital_disease_symptom")
@KeySequence("hospital_disease_symptom_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseSymptomDO extends BaseDO {
    @TableId
    private Long id;
    private Long diseaseId;
    private Long symptomId;
    /** 1=主要 2=次要 3=偶见 */
    private Integer strength;
    private String reference;
}
