package cn.iocoder.yudao.module.hospital.service.patient;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.patient.vo.PatientPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.patient.vo.PatientSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PatientDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.PatientMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.PATIENT_NOT_EXISTS;

@Service
public class PatientServiceImpl implements PatientService {

    @Resource
    private PatientMapper patientMapper;

    @Override
    public Long createPatient(PatientSaveReqVO createReqVO) {
        PatientDO patient = BeanUtils.toBean(createReqVO, PatientDO.class);
        patientMapper.insert(patient);
        return patient.getId();
    }

    @Override
    public void updatePatient(PatientSaveReqVO updateReqVO) {
        validatePatientExists(updateReqVO.getId());
        PatientDO updateObj = BeanUtils.toBean(updateReqVO, PatientDO.class);
        patientMapper.updateById(updateObj);
    }

    @Override
    public void deletePatient(Long id) {
        validatePatientExists(id);
        patientMapper.deleteById(id);
    }

    @Override
    public PatientDO getPatient(Long id) {
        return patientMapper.selectById(id);
    }

    @Override
    public PageResult<PatientDO> getPatientPage(PatientPageReqVO pageReqVO) {
        return patientMapper.selectPage(pageReqVO);
    }

    private void validatePatientExists(Long id) {
        if (id == null) return;
        if (patientMapper.selectById(id) == null) {
            throw exception(PATIENT_NOT_EXISTS);
        }
    }
}
