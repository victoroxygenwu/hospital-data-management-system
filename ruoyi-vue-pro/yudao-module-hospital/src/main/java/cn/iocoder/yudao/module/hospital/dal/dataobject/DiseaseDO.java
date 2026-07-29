package cn.iocoder.yudao.module.hospital.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("hospital_disease")
@KeySequence("hospital_disease_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseDO extends BaseDO {
    @TableId
    private Long id;
    private String name;
    private String icdCode;
    private String category;
    private Long deptId;
    private String description;
    private String typicalSymptoms;
    private Integer isCommon;
}
