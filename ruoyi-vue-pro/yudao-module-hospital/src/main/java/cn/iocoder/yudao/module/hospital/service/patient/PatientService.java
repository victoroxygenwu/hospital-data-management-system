package cn.iocoder.yudao.module.hospital.service.patient;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.hospital.controller.admin.patient.vo.PatientPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.patient.vo.PatientSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PatientDO;

public interface PatientService {
    Long createPatient(PatientSaveReqVO createReqVO);
    void updatePatient(PatientSaveReqVO updateReqVO);
    void deletePatient(Long id);
    PatientDO getPatient(Long id);
    PageResult<PatientDO> getPatientPage(PatientPageReqVO pageReqVO);
}
