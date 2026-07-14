package cn.iocoder.yudao.module.hospital.service.department;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.department.vo.DepartmentPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.department.vo.DepartmentSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DepartmentDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DoctorDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.WardDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.DepartmentMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.DoctorMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.WardMapper;
import cn.iocoder.yudao.module.hospital.service.ward.WardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.DEPARTMENT_NOT_EXISTS;

/**
 * 科室 Service 实现类
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Resource
    private DepartmentMapper departmentMapper;
    @Resource
    private DoctorMapper doctorMapper;
    @Resource
    private WardMapper wardMapper;
    @Resource
    private WardService wardService;

    /**
     * 创建科室
     * @param createReqVO 创建请求
     * @return 新科室ID
     */
    @Override
    public Long createDepartment(DepartmentSaveReqVO createReqVO) {
        DepartmentDO department = BeanUtils.toBean(createReqVO, DepartmentDO.class);
        departmentMapper.insert(department);
        return department.getId();
    }

    /**
     * 更新科室
     * @param updateReqVO 更新请求
     */
    @Override
    public void updateDepartment(DepartmentSaveReqVO updateReqVO) {
        validateDepartmentExists(updateReqVO.getId());
        DepartmentDO updateObj = BeanUtils.toBean(updateReqVO, DepartmentDO.class);
        departmentMapper.updateById(updateObj);
    }

    /**
     * 删除科室（级联删除下属医生和病房，病房内床位由 WardService 级联处理）
     * @param id 科室ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDepartment(Long id) {
        validateDepartmentExists(id);
        // 1. 级联删除下属医生
        doctorMapper.delete(DoctorDO::getDeptId, id);
        // 2. 级联删除下属病房（WardService 内部级联删除床位）
        List<WardDO> wards = wardMapper.selectList(WardDO::getDeptId, id);
        for (WardDO ward : wards) {
            wardService.deleteWard(ward.getId());
        }
        // 3. 删除科室
        departmentMapper.deleteById(id);
    }

    /**
     * 查询科室
     * @param id 科室ID
     * @return 科室信息
     */
    @Override
    public DepartmentDO getDepartment(Long id) {
        return departmentMapper.selectById(id);
    }

    /**
     * 分页查询科室
     * @param pageReqVO 分页请求
     * @return 科室分页结果
     */
    @Override
    public PageResult<DepartmentDO> getDepartmentPage(DepartmentPageReqVO pageReqVO) {
        return departmentMapper.selectPage(pageReqVO);
    }

    /**
     * 查询所有科室列表
     * @return 科室列表
     */
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
