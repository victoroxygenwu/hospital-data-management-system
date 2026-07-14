package cn.iocoder.yudao.module.hospital.service.medicine;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.hospital.dal.dataobject.MedicineDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.MedicineMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.PrescriptionItemMapper;
import cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("药品 Service 单元测试（覆盖删除引用守卫）")
class MedicineServiceImplTest {

    private MedicineServiceImpl medicineService;

    @Mock
    private MedicineMapper medicineMapper;
    @Mock
    private PrescriptionItemMapper prescriptionItemMapper;
    @Mock
    private HospitalSecurityContext securityContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        medicineService = new MedicineServiceImpl();
        ReflectionTestUtils.setField(medicineService, "medicineMapper", medicineMapper);
        ReflectionTestUtils.setField(medicineService, "prescriptionItemMapper", prescriptionItemMapper);
        ReflectionTestUtils.setField(medicineService, "securityContext", securityContext);
    }

    @Test
    @DisplayName("删除药品-存在处方引用被拒：不删除，抛 MEDICINE_HAS_PRESCRIPTION")
    void deleteMedicine_referenced_blocked() {
        when(medicineMapper.selectById(1L)).thenReturn(new MedicineDO());
        when(prescriptionItemMapper.countByMedicineId(1L)).thenReturn(1L);

        ServiceException ex = assertThrows(ServiceException.class, () -> medicineService.deleteMedicine(1L));
        assertEquals(ErrorCodeConstants.MEDICINE_HAS_PRESCRIPTION.getCode(), ex.getCode());
        verify(medicineMapper, never()).deleteById(any());
    }

    @Test
    @DisplayName("删除药品-无处方引用正常删除")
    void deleteMedicine_noReference_deleted() {
        when(medicineMapper.selectById(1L)).thenReturn(new MedicineDO());
        when(prescriptionItemMapper.countByMedicineId(1L)).thenReturn(0L);

        medicineService.deleteMedicine(1L);
        verify(medicineMapper).deleteById(1L);
    }
}
