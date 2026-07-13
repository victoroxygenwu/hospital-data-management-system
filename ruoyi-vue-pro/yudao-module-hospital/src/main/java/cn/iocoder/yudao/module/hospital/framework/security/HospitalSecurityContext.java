package cn.iocoder.yudao.module.hospital.framework.security;

import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DoctorDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PatientDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.DoctorMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.PatientMapper;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.HOSPITAL_PERMISSION_DENIED;

/**
 * 医院模块安全上下文 —— 获取当前登录用户对应的医生/患者身份
 */
@Component
public class HospitalSecurityContext {

    @Resource
    private DoctorMapper doctorMapper;
    @Resource
    private PatientMapper patientMapper;

    /**
     * 当前用户是否为管理员（管理员可以看到全部数据）
     */
    public boolean isAdmin() {
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) return false;
        return loginUser.getUserType() != null && loginUser.getUserType() <= 2;
    }

    /**
     * 要求当前用户是管理员，否则抛出权限异常
     */
    public void requireAdmin() {
        if (!isAdmin()) {
            throw exception(HOSPITAL_PERMISSION_DENIED);
        }
    }

    /**
     * 获取当前用户对应的医生 ID（如果不是医生则返回 null）
     */
    public Long getCurrentDoctorId() {
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) return null;
        DoctorDO doctor = doctorMapper.selectOne(
                new LambdaQueryWrapperX<DoctorDO>().eq(DoctorDO::getUserId, loginUser.getId()));
        return doctor != null ? doctor.getId() : null;
    }

    /**
     * 获取当前用户对应的患者 ID（如果不是患者则返回 null）
     */
    public Long getCurrentPatientId() {
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) return null;
        PatientDO patient = patientMapper.selectOne(
                new LambdaQueryWrapperX<PatientDO>().eq(PatientDO::getUserId, loginUser.getId()));
        return patient != null ? patient.getId() : null;
    }

    /**
     * 获取当前用户的登录 ID
     */
    public Long getCurrentUserId() {
        return SecurityFrameworkUtils.getLoginUserId();
    }
}
