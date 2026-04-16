package cn.iocoder.yudao.module.hospital.service.department;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.department.vo.DepartmentPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.department.vo.DepartmentSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DepartmentDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.DepartmentMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.DEPARTMENT_NOT_EXISTS;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Resource
    private DepartmentMapper departmentMapper;

    @Override
    public Long createDepartment(DepartmentSaveReqVO createReqVO) {
        DepartmentDO department = BeanUtils.toBean(createReqVO, DepartmentDO.class);
        departmentMapper.insert(department);
        return department.getId();
    }

    @Override
    public void updateDepartment(DepartmentSaveReqVO updateReqVO) {
        validateDepartmentExists(updateReqVO.getId());
        DepartmentDO updateObj = BeanUtils.toBean(updateReqVO, DepartmentDO.class);
        departmentMapper.updateById(updateObj);
    }

    @Override
    public void deleteDepartment(Long id) {
        validateDepartmentExists(id);
        departmentMapper.deleteById(id);
    }

    @Override
    public DepartmentDO getDepartment(Long id) {
        return departmentMapper.selectById(id);
    }

    @Override
    public PageResult<DepartmentDO> getDepartmentPage(DepartmentPageReqVO pageReqVO) {
        return departmentMapper.selectPage(pageReqVO);
    }

    @Override
    public List<DepartmentDO> getDepartmentList() {
        return departmentMapper.selectList();
    }

    private void validateDepartmentExists(Long id) {
        if (id == null) return;
        if (departmentMapper.selectById(id) == null) {
            throw exception(DEPARTMENT_NOT_EXISTS);
        }
    }
}
