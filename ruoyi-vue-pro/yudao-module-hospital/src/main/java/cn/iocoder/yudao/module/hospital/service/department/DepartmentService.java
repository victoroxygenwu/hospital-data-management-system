package cn.iocoder.yudao.module.hospital.service.department;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.hospital.controller.admin.department.vo.DepartmentPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.department.vo.DepartmentSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DepartmentDO;
import java.util.List;

public interface DepartmentService {
    Long createDepartment(DepartmentSaveReqVO createReqVO);
    void updateDepartment(DepartmentSaveReqVO updateReqVO);
    void deleteDepartment(Long id);
    DepartmentDO getDepartment(Long id);
    PageResult<DepartmentDO> getDepartmentPage(DepartmentPageReqVO pageReqVO);
    List<DepartmentDO> getDepartmentList();
}
