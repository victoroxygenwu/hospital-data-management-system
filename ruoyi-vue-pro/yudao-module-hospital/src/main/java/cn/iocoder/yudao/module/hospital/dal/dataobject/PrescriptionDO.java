package cn.iocoder.yudao.module.hospital.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("hospital_prescription")
@KeySequence("hospital_prescription_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
/** 处方 DO */
public class PrescriptionDO extends BaseDO {
    /** 主键ID */
    @TableId
    private Long id;
    /** 就诊ID */
    private Long visitId;
    /** 开方医生ID */
    private Long doctorId;
    /** 处方状态: 0-待发药 1-已发药 */
    private String status;
    private String notes;
}
