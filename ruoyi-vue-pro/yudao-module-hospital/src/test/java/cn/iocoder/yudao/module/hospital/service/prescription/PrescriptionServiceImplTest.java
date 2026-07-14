package cn.iocoder.yudao.module.hospital.service.prescription;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BillDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionItemDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.VisitDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.BillMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.PrescriptionItemMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.PrescriptionMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.VisitMapper;
import cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import cn.iocoder.yudao.module.hospital.service.medicine.MedicineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("处方 Service 单元测试（覆盖发药幂等与账单生成）")
class PrescriptionServiceImplTest {

    private PrescriptionServiceImpl prescriptionService;

    @Mock
    private PrescriptionMapper prescriptionMapper;
    @Mock
    private PrescriptionItemMapper prescriptionItemMapper;
    @Mock
    private VisitMapper visitMapper;
    @Mock
    private MedicineService medicineService;
    @Mock
    private BillMapper billMapper;
    @Mock
    private HospitalSecurityContext securityContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        prescriptionService = new PrescriptionServiceImpl();
        ReflectionTestUtils.setField(prescriptionService, "prescriptionMapper", prescriptionMapper);
        ReflectionTestUtils.setField(prescriptionService, "prescriptionItemMapper", prescriptionItemMapper);
        ReflectionTestUtils.setField(prescriptionService, "visitMapper", visitMapper);
        ReflectionTestUtils.setField(prescriptionService, "medicineService", medicineService);
        ReflectionTestUtils.setField(prescriptionService, "billMapper", billMapper);
        ReflectionTestUtils.setField(prescriptionService, "securityContext", securityContext);
    }

    @Test
    @DisplayName("发药-成功：扣减库存并生成账单")
    void dispensePrescription_success() {
        PrescriptionDO p = new PrescriptionDO();
        p.setId(1L);
        p.setVisitId(100L);
        when(prescriptionMapper.selectById(1L)).thenReturn(p);
        when(prescriptionMapper.dispense(1L)).thenReturn(1);

        PrescriptionItemDO item = PrescriptionItemDO.builder()
                .medicineId(7L)
                .quantity(2)
                .price(new BigDecimal("5"))
                .build();
        when(prescriptionItemMapper.selectListByPrescriptionId(1L)).thenReturn(Arrays.asList(item));

        VisitDO visit = new VisitDO();
        visit.setId(100L);
        visit.setPatientId(3L);
        when(visitMapper.selectById(100L)).thenReturn(visit);

        prescriptionService.dispensePrescription(1L);

        verify(medicineService).decrementStock(7L, 2);
        verify(billMapper).insert(any(BillDO.class));
    }

    @Test
    @DisplayName("发药-重复发药幂等：影响行数=0 时不扣库存、不重复建账单")
    void dispensePrescription_idempotent_noOp() {
        PrescriptionDO p = new PrescriptionDO();
        p.setId(1L);
        p.setVisitId(100L);
        when(prescriptionMapper.selectById(1L)).thenReturn(p);
        // 模拟已发药：条件更新影响行数=0
        when(prescriptionMapper.dispense(1L)).thenReturn(0);

        prescriptionService.dispensePrescription(1L);

        verify(medicineService, never()).decrementStock(anyLong(), anyInt());
        verify(billMapper, never()).insert(any(BillDO.class));
    }
}
