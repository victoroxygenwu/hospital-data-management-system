package cn.iocoder.yudao.module.hospital.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("hospital_department")
@KeySequence("hospital_department_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
/** 科室 DO */
public class DepartmentDO extends BaseDO {
    /** 主键ID */
    @TableId
    private Long id;
    /** 科室名称 */
    private String deptName;
    private String phone;
    private String manager;
    private String location;
    private String description;
}
