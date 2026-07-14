package cn.iocoder.yudao.module.hospital.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("hospital_ward")
@KeySequence("hospital_ward_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
/** 病区 DO */
public class WardDO extends BaseDO {
    /** 主键ID */
    @TableId
    private Long id;
    /** 所属科室ID */
    private Long deptId;
    private String wardNo;
    private String type;
    private Integer capacity;
    private Integer usedBeds;
    /** 病区状态 */
    private Integer status;
    private String description;
}
