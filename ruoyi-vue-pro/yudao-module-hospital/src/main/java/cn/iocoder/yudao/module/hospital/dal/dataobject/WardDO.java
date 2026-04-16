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
public class WardDO extends BaseDO {
    @TableId
    private Long id;
    private Long deptId;
    private String wardNo;
    private String type;
    private Integer capacity;
    private Integer usedBeds;
    private Integer status;
    private String description;
}
