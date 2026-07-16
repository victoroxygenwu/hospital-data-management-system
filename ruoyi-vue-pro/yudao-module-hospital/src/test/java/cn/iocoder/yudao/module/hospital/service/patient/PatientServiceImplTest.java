package cn.iocoder.yudao.module.hospital.service.patient;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.hospital.controller.admin.patient.vo.PatientPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.patient.vo.PatientSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BedDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BillDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PatientDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionItemDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.VisitDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.BedMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.BillMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.PatientMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.PrescriptionItemMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.PrescriptionMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.VisitMapper;
import cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import cn.iocoder.yudao.module.hospital.service.ward.WardService;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

@DisplayName("患者 Service 单元测试（覆盖数据隔离与级联删除）")
class PatientServiceImplTest {

    private PatientServiceImpl patientService;

    @Mock
    private PatientMapper patientMapper;
    @Mock
    private VisitMapper visitMapper;
    @Mock
    private PrescriptionMapper prescriptionMapper;
    @Mock
    private PrescriptionItemMapper prescriptionItemMapper;
    @Mock
    private BillMapper billMapper;
    @Mock
    private BedMapper bedMapper;
    @Mock
    private WardService wardService;
    @Mock
    private HospitalSecurityContext securityContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // deletePatient 内 new LambdaQueryWrapper<VisitDO/PrescriptionDO/BedDO/BillDO>
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), VisitDO.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), PrescriptionDO.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), BedDO.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), BillDO.class);
        patientService = new PatientServiceImpl();
        ReflectionTestUtils.setField(patientService, "patientMapper", patientMapper);
        ReflectionTestUtils.setField(patientService, "visitMapper", visitMapper);
        ReflectionTestUtils.setField(patientService, "prescriptionMapper", prescriptionMapper);
        ReflectionTestUtils.setField(patientService, "prescriptionItemMapper", prescriptionItemMapper);
        ReflectionTestUtils.setField(patientService, "billMapper", billMapper);
        ReflectionTestUtils.setField(patientService, "bedMapper", bedMapper);
        ReflectionTestUtils.setField(patientService, "wardService", wardService);
        ReflectionTestUtils.setField(patientService, "securityContext", securityContext);
    }

    @Test
    @DisplayName("分页查询患者-非管理员强制只看自己：pageReqVO.id 被设为当前患者")
    void getPatientPage_patientSeesOnlySelf() {
        when(securityContext.resolvePatientScope()).thenReturn(100L);
        when(patientMapper.selectPage(any())).thenReturn(PageResult.empty());

        PatientPageReqVO req = new PatientPageReqVO();
        patientService.getPatientPage(req);

        assertEquals(100L, req.getId());
        verify(patientMapper).selectPage(any());
    }

    @Test
    @DisplayName("分页查询患者-管理员可见全部：pageReqVO.id 不被改写")
    void getPatientPage_adminSeesAll() {
        when(securityContext.resolvePatientScope()).thenReturn(null);

        PatientPageReqVO req = new PatientPageReqVO();
        req.setId(999L);
        patientService.getPatientPage(req);

        assertEquals(999L, req.getId());
    }

    @Test
    @DisplayName("删除患者-级联删除就诊/处方/明细/账单且释放床位")
    void deletePatient_cascadeDeletes() {
        when(patientMapper.selectById(1L)).thenReturn(new PatientDO());
        VisitDO visit = new VisitDO();
        visit.setId(10L);
        when(visitMapper.selectList(any())).thenReturn(java.util.Collections.singletonList(visit));
        when(prescriptionMapper.selectList(any())).thenReturn(java.util.Collections.emptyList());
        when(bedMapper.selectList(any())).thenReturn(java.util.Collections.emptyList());

        patientService.deletePatient(1L);

        verify(prescriptionItemMapper, never()).delete(any(SFunction.class), any());
        verify(prescriptionMapper, never()).deleteById(any(Long.class));
        // 两次账单删除：① 就诊下的账单(visitId) ② 患者直接关联的账单(patientId)
        verify(billMapper, times(2)).delete(any(SFunction.class), any());
        verify(visitMapper).deleteById(10L);
        verify(patientMapper).deleteById(1L);
        verify(wardService, never()).decrementUsedBeds(any(Long.class));
    }

    @Test
    @DisplayName("创建患者-非管理员被拒：抛 HOSPITAL_PERMISSION_DENIED，不插入")
    void createPatient_nonAdmin_rejected() {
        // requireAdmin() 是 void 方法，mock 不会执行真实抛错逻辑，需显式 stub 抛错（与生产抛同一错误码）
        doThrow(exception(ErrorCodeConstants.HOSPITAL_PERMISSION_DENIED))
                .when(securityContext).requireAdmin();
        PatientSaveReqVO req = new PatientSaveReqVO();
        ServiceException ex = assertThrows(ServiceException.class, () -> patientService.createPatient(req));
        assertEquals(ErrorCodeConstants.HOSPITAL_PERMISSION_DENIED.getCode(), ex.getCode());
        verify(patientMapper, never()).insert(any(PatientDO.class));
    }
}
