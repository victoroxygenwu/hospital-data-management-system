package cn.iocoder.yudao.module.hospital.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("hospital_doctor")
@KeySequence("hospital_doctor_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
/** 医生 DO */
public class DoctorDO extends BaseDO {
    /** 主键ID */
    @TableId
    private Long id;
    /** 系统用户ID */
    private Long userId;
    /** 所属科室ID */
    private Long deptId;
    /** 医生姓名 */
    private String name;
    private String gender;
    private Integer age;
    private String title;
    private String licenseNo;
    private String phone;
    private String email;
}
