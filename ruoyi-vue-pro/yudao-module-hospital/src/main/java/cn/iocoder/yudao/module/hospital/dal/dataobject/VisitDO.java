package cn.iocoder.yudao.module.hospital.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.time.LocalDateTime;

@TableName("hospital_visit")
@KeySequence("hospital_visit_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
/** 就诊记录 DO */
public class VisitDO extends BaseDO {
    /** 主键ID */
    @TableId
    private Long id;
    /** 患者ID */
    private Long patientId;
    /** 就诊医生ID */
    private Long doctorId;
    /** 就诊科室ID */
    private Long deptId;
    private LocalDateTime visitDate;
    private String reason;
    private String diagnosis;
    private String notes;
    /** 就诊状态 */
    private Integer status;
}
