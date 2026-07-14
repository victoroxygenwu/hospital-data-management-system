package cn.iocoder.yudao.module.hospital.service.doctor;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.doctor.vo.DoctorPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.doctor.vo.DoctorSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DoctorDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.DoctorMapper;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.*;

/**
 * 医生 Service 实现类
 */
@Service
public class DoctorServiceImpl implements DoctorService {

    @Resource
    private DoctorMapper doctorMapper; // 医生数据访问
    @Resource
    private HospitalSecurityContext securityContext; // 角色权限上下文

    /**
     * 创建医生
     * @param createReqVO 创建请求
     * @return 新医生ID
     */
    @Override
    public Long createDoctor(DoctorSaveReqVO createReqVO) {
        securityContext.requireAdmin();
        DoctorDO doctor = BeanUtils.toBean(createReqVO, DoctorDO.class);
        doctorMapper.insert(doctor);
        return doctor.getId();
    }

    /**
     * 更新医生
     * @param updateReqVO 更新请求
     */
    @Override
    public void updateDoctor(DoctorSaveReqVO updateReqVO) {
        securityContext.requireAdmin();
        validateDoctorExists(updateReqVO.getId());
        DoctorDO updateObj = BeanUtils.toBean(updateReqVO, DoctorDO.class);
        doctorMapper.updateById(updateObj);
    }

    /**
     * 删除医生
     * @param id 医生ID
     */
    @Override
    public void deleteDoctor(Long id) {
        securityContext.requireAdmin();
        validateDoctorExists(id);
        doctorMapper.deleteById(id);
    }

    /**
     * 查询医生
     * @param id 医生ID
     * @return 医生信息
     */
    @Override
    public DoctorDO getDoctor(Long id) {
        return doctorMapper.selectById(id);
    }

    /**
     * 分页查询医生
     * @param pageReqVO 分页请求
     * @return 医生分页结果
     */
    @Override
    public PageResult<DoctorDO> getDoctorPage(DoctorPageReqVO pageReqVO) {
        return doctorMapper.selectPage(pageReqVO);
    }

    /**
     * 按科室ID查询医生列表
     * @param deptId 科室ID
     * @return 医生列表
     */
    @Override
    public List<DoctorDO> getDoctorListByDeptId(Long deptId) {
        return doctorMapper.selectListByDeptId(deptId);
    }

    private void validateDoctorExists(Long id) {
        if (id == null) return;
        if (doctorMapper.selectById(id) == null) {
            throw exception(DOCTOR_NOT_EXISTS);
        }
    }
}
