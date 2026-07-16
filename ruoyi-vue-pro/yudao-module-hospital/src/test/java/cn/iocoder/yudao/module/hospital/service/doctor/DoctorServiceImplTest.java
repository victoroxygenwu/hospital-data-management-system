package cn.iocoder.yudao.module.hospital.service.doctor;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.hospital.controller.admin.doctor.vo.DoctorSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DoctorDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.VisitDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.DoctorMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.VisitMapper;
import cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("医生 Service 单元测试（覆盖管理员守卫与级联删除保护）")
class DoctorServiceImplTest {

    private DoctorServiceImpl doctorService;

    @Mock
    private DoctorMapper doctorMapper;
    @Mock
    private VisitMapper visitMapper;
    @Mock
    private HospitalSecurityContext securityContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // deleteDoctor 内 new LambdaQueryWrapper<VisitDO>()，需注册 lambda 元数据
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), VisitDO.class);
        doctorService = new DoctorServiceImpl();
        ReflectionTestUtils.setField(doctorService, "doctorMapper", doctorMapper);
        ReflectionTestUtils.setField(doctorService, "visitMapper", visitMapper);
        ReflectionTestUtils.setField(doctorService, "securityContext", securityContext);
    }

    @Test
    @DisplayName("创建医生-非管理员被拒：requireAdmin 抛权限异常")
    void createDoctor_nonAdmin_rejected() {
        doThrow(new ServiceException(ErrorCodeConstants.HOSPITAL_PERMISSION_DENIED))
                .when(securityContext).requireAdmin();

        ServiceException ex = assertThrows(ServiceException.class,
                () -> doctorService.createDoctor(new DoctorSaveReqVO()));
        assertEquals(ErrorCodeConstants.HOSPITAL_PERMISSION_DENIED.getCode(), ex.getCode());
        verify(doctorMapper, never()).insert(any(DoctorDO.class));
    }

    @Test
    @DisplayName("删除医生-存在关联就诊被拒：抛 DOCTOR_HAS_VISITS")
    void deleteDoctor_hasVisits_rejected() {
        when(doctorMapper.selectById(1L)).thenReturn(new DoctorDO());
        when(visitMapper.selectCount(any())).thenReturn(1L);

        ServiceException ex = assertThrows(ServiceException.class, () -> doctorService.deleteDoctor(1L));
        assertEquals(ErrorCodeConstants.DOCTOR_HAS_VISITS.getCode(), ex.getCode());
        verify(doctorMapper, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("删除医生-无关联就诊成功：执行删除")
    void deleteDoctor_noVisits_success() {
        when(doctorMapper.selectById(1L)).thenReturn(new DoctorDO());
        when(visitMapper.selectCount(any())).thenReturn(0L);

        doctorService.deleteDoctor(1L);
        verify(doctorMapper).deleteById(1L);
    }
}
