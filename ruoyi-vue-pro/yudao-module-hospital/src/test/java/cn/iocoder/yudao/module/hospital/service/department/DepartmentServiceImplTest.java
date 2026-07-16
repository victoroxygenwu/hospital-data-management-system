package cn.iocoder.yudao.module.hospital.service.department;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.hospital.controller.admin.department.vo.DepartmentSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DepartmentDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DoctorDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.WardDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.DepartmentMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.DoctorMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.WardMapper;
import cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants;
import cn.iocoder.yudao.module.hospital.service.ward.WardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("科室 Service 单元测试（覆盖 CRUD 与级联删除）")
class DepartmentServiceImplTest {

    private DepartmentServiceImpl departmentService;

    @Mock
    private DepartmentMapper departmentMapper;
    @Mock
    private DoctorMapper doctorMapper;
    @Mock
    private WardMapper wardMapper;
    @Mock
    private WardService wardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        departmentService = new DepartmentServiceImpl();
        ReflectionTestUtils.setField(departmentService, "departmentMapper", departmentMapper);
        ReflectionTestUtils.setField(departmentService, "doctorMapper", doctorMapper);
        ReflectionTestUtils.setField(departmentService, "wardMapper", wardMapper);
        ReflectionTestUtils.setField(departmentService, "wardService", wardService);
    }

    @Test
    @DisplayName("创建科室-成功：调用 insert")
    void createDepartment_success() {
        departmentService.createDepartment(new DepartmentSaveReqVO());
        verify(departmentMapper).insert(any(DepartmentDO.class));
    }

    @Test
    @DisplayName("更新科室-不存在被拒：抛 DEPARTMENT_NOT_EXISTS，不执行更新")
    void updateDepartment_notExists_rejected() {
        when(departmentMapper.selectById(1L)).thenReturn(null);

        DepartmentSaveReqVO req = new DepartmentSaveReqVO();
        req.setId(1L);
        ServiceException ex = assertThrows(ServiceException.class, () -> departmentService.updateDepartment(req));
        assertEquals(ErrorCodeConstants.DEPARTMENT_NOT_EXISTS.getCode(), ex.getCode());
        verify(departmentMapper, never()).updateById(any(DepartmentDO.class));
    }

    @Test
    @DisplayName("删除科室-成功：级联删除下属医生与病房")
    void deleteDepartment_success() {
        when(departmentMapper.selectById(1L)).thenReturn(new DepartmentDO());
        WardDO ward = new WardDO();
        ward.setId(5L);
        // 第一个实参转型锁定 SFunction 重载，第二个用 eq(1L) 锁定 Long，排除所有 Collection 重载，避免 selectList 5 个重载的歧义
        when(wardMapper.selectList((SFunction<WardDO, Long>) any(), eq(1L)))
                .thenReturn(java.util.Collections.singletonList(ward));

        departmentService.deleteDepartment(1L);

        verify(doctorMapper).delete(any(SFunction.class), any());
        // deleteWard(5L) 被调用即证明 wardMapper.selectList 返回了该病房、级联发生
        verify(wardService).deleteWard(any(Long.class));
        verify(departmentMapper).deleteById(1L);
    }
}
