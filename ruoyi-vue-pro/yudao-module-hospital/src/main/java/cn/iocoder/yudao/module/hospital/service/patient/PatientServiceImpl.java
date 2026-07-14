package cn.iocoder.yudao.module.hospital.service.patient;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.patient.vo.PatientPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.patient.vo.PatientSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PatientDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.PatientMapper;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.PATIENT_NOT_EXISTS;

/**
 * 患者 Service 实现类
 */
@Service
public class PatientServiceImpl implements PatientService {

    @Resource
    private PatientMapper patientMapper; // 患者数据访问
    @Resource
    private HospitalSecurityContext securityContext; // 角色权限上下文

    /**
     * 创建患者
     * @param createReqVO 创建请求
     * @return 新患者ID
     */
    @Override
    public Long createPatient(PatientSaveReqVO createReqVO) {
        PatientDO patient = BeanUtils.toBean(createReqVO, PatientDO.class);
        patientMapper.insert(patient);
        return patient.getId();
    }

    /**
     * 更新患者
     * @param updateReqVO 更新请求
     */
    @Override
    public void updatePatient(PatientSaveReqVO updateReqVO) {
        validatePatientExists(updateReqVO.getId());
        PatientDO updateObj = BeanUtils.toBean(updateReqVO, PatientDO.class);
        patientMapper.updateById(updateObj);
    }

    /**
     * 删除患者
     * @param id 患者ID
     */
    @Override
    public void deletePatient(Long id) {
        validatePatientExists(id);
        patientMapper.deleteById(id);
    }

    /**
     * 查询患者
     * @param id 患者ID
     * @return 患者信息
     */
    @Override
    public PatientDO getPatient(Long id) {
        return patientMapper.selectById(id);
    }

    /**
     * 分页查询患者（角色数据隔离：患者只能看到自己的档案）
     * @param pageReqVO 分页请求
     * @return 患者分页结果
     */
    @Override
    public PageResult<PatientDO> getPatientPage(PatientPageReqVO pageReqVO) {
        // 角色数据隔离：患者只能看到自己的档案，走正常分页
        if (!securityContext.isAdmin()) {
            Long patientId = securityContext.getCurrentPatientId();
            if (patientId != null) {
                pageReqVO.setId(patientId);
            }
        }
        return patientMapper.selectPage(pageReqVO);
    }

    private void validatePatientExists(Long id) {
        if (id == null) return;
        if (patientMapper.selectById(id) == null) {
            throw exception(PATIENT_NOT_EXISTS);
        }
    }
}
