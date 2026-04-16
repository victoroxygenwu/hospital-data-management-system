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
public class DoctorDO extends BaseDO {
    @TableId
    private Long id;
    private Long userId;
    private Long deptId;
    private String name;
    private String gender;
    private Integer age;
    private String title;
    private String licenseNo;
    private String phone;
    private String email;
}
