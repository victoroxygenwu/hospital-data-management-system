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
     *
     * 说明：yudao 后台账号（system_users）登录后 LoginUser.userType 统一为 ADMIN(2)，
     * 无法据此区分医生/患者/管理员。因此这里改为按业务身份判定——
     * 只有「既未关联医生档案、也未关联患者档案」的后台账号才视为管理员。
     */
    public boolean isAdmin() {
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) return false;
        return getCurrentDoctorId() == null && getCurrentPatientId() == null;
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

    /**
     * 解析"患者可查看范围"：返回当前登录患者 ID；管理员或身份未知时返回 null。
     * null 语义 = 可见全部数据。用于分页查询时强制过滤，
     * 消除各 Service 中重复的 isAdmin()+getCurrentPatientId() 分支。
     */
    public Long resolvePatientScope() {
        return isAdmin() ? null : getCurrentPatientId();
    }

    /**
     * 解析"医生可查看范围"：返回当前登录医生 ID；管理员或身份未知时返回 null。
     * null 语义 = 可见全部数据。用于分页查询时强制过滤。
     */
    public Long resolveDoctorScope() {
        return isAdmin() ? null : getCurrentDoctorId();
    }
}
