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
public class VisitDO extends BaseDO {
    @TableId
    private Long id;
    private Long patientId;
    private Long doctorId;
    private Long deptId;
    private LocalDateTime visitDate;
    private String reason;
    private String diagnosis;
    private String notes;
    private String status;
}
