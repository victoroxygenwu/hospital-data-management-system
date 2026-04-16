package cn.iocoder.yudao.module.hospital.service.doctor;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.hospital.controller.admin.doctor.vo.DoctorPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.doctor.vo.DoctorSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DoctorDO;
import java.util.List;

public interface DoctorService {
    Long createDoctor(DoctorSaveReqVO createReqVO);
    void updateDoctor(DoctorSaveReqVO updateReqVO);
    void deleteDoctor(Long id);
    DoctorDO getDoctor(Long id);
    PageResult<DoctorDO> getDoctorPage(DoctorPageReqVO pageReqVO);
    List<DoctorDO> getDoctorListByDeptId(Long deptId);
}
