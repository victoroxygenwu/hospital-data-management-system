package cn.iocoder.yudao.module.hospital.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.time.LocalDateTime;

@TableName("hospital_bed")
@KeySequence("hospital_bed_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
/** 床位 DO */
public class BedDO extends BaseDO {
    /** 主键ID */
    @TableId
    private Long id;
    /** 所属病区ID */
    private Long wardId;
    private String bedNo;
    /** 床位状态: 0-空闲 1-占用 */
    private String status;
    /** 入住患者ID */
    private Long patientId;
    private LocalDateTime admissionTime;
}
