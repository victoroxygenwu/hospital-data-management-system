package cn.iocoder.yudao.module.hospital.service.prescription;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.hospital.controller.admin.prescription.vo.PrescriptionPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.prescription.vo.PrescriptionSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.MedicineDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionItemDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.PrescriptionItemMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.PrescriptionMapper;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import cn.iocoder.yudao.module.hospital.service.medicine.MedicineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceImplTest {

    @Mock
    private PrescriptionMapper prescriptionMapper;
    @Mock
    private PrescriptionItemMapper prescriptionItemMapper;
    @Mock
    private MedicineService medicineService;
    @Mock
    private HospitalSecurityContext securityContext;

    @InjectMocks
    private PrescriptionServiceImpl prescriptionService;

    private MedicineDO medicine;

    @BeforeEach
    void setUp() {
        medicine = MedicineDO.builder()
                .id(1L).name("阿莫西林").price(new BigDecimal("25.50")).stock(100).build();
    }

    // ==================== createPrescription ====================

    @Test
    void createPrescription_shouldInsertParentAndItems() {
        PrescriptionSaveReqVO reqVO = new PrescriptionSaveReqVO();
        reqVO.setVisitId(10L);
        reqVO.setDoctorId(1L);

        PrescriptionSaveReqVO.PrescriptionItemSaveVO itemVO = new PrescriptionSaveReqVO.PrescriptionItemSaveVO();
        itemVO.setMedicineId(1L);
        itemVO.setQuantity(2);
        itemVO.setInstructions("每日三次");
        reqVO.setItems(Collections.singletonList(itemVO));

        when(medicineService.getMedicineListByIds(any())).thenReturn(Collections.singletonList(medicine));
        // 模拟 MyBatis-Plus 自动回填主键
        doAnswer(inv -> { inv.getArgument(0, PrescriptionDO.class).setId(100L); return 1; })
                .when(prescriptionMapper).insert(any(PrescriptionDO.class));

        Long id = prescriptionService.createPrescription(reqVO);

        assertNotNull(id);
        assertEquals(100L, id);
        verify(prescriptionMapper).insert(any(PrescriptionDO.class));
        verify(medicineService).getMedicineListByIds(any());
        verify(prescriptionItemMapper).insertBatch(any());
    }

    @Test
    void createPrescription_withNullItems_shouldNotInsertItems() {
        PrescriptionSaveReqVO reqVO = new PrescriptionSaveReqVO();
        reqVO.setVisitId(10L);
        reqVO.setItems(null);

        when(prescriptionMapper.insert(any(PrescriptionDO.class))).thenReturn(1);

        prescriptionService.createPrescription(reqVO);

        verify(prescriptionItemMapper, never()).insertBatch(any());
    }

    // ==================== updatePrescription (merge logic) ====================

    @Test
    void updatePrescription_shouldMergeItems() {
        Long prescriptionId = 100L;
        PrescriptionDO existingPrescription = PrescriptionDO.builder().id(prescriptionId).build();
        when(prescriptionMapper.selectById(prescriptionId)).thenReturn(existingPrescription);

        // 旧明细：药品1、药品2
        PrescriptionItemDO oldItem1 = PrescriptionItemDO.builder().id(1L).medicineId(1L).prescriptionId(prescriptionId).build();
        PrescriptionItemDO oldItem2 = PrescriptionItemDO.builder().id(2L).medicineId(2L).prescriptionId(prescriptionId).build();
        when(prescriptionItemMapper.selectListByPrescriptionId(prescriptionId))
                .thenReturn(Arrays.asList(oldItem1, oldItem2));

        MedicineDO medicine2 = MedicineDO.builder().id(2L).name("布洛芬").price(new BigDecimal("10.00")).stock(50).build();
        MedicineDO medicine3 = MedicineDO.builder().id(3L).name("青霉素").price(new BigDecimal("15.00")).stock(200).build();
        when(medicineService.getMedicineListByIds(any()))
                .thenReturn(Arrays.asList(medicine, medicine2, medicine3));

        // 新明细：药品1（更新）、药品2（保留）、药品3（新增）
        PrescriptionSaveReqVO reqVO = new PrescriptionSaveReqVO();
        reqVO.setId(prescriptionId);

        PrescriptionSaveReqVO.PrescriptionItemSaveVO item1 = new PrescriptionSaveReqVO.PrescriptionItemSaveVO();
        item1.setMedicineId(1L); item1.setQuantity(5); item1.setInstructions("更新后的用法");

        PrescriptionSaveReqVO.PrescriptionItemSaveVO item2 = new PrescriptionSaveReqVO.PrescriptionItemSaveVO();
        item2.setMedicineId(2L); item2.setQuantity(1); item2.setInstructions("不变");

        PrescriptionSaveReqVO.PrescriptionItemSaveVO item3 = new PrescriptionSaveReqVO.PrescriptionItemSaveVO();
        item3.setMedicineId(3L); item3.setQuantity(3); item3.setInstructions("新药");

        reqVO.setItems(Arrays.asList(item1, item2, item3));

        prescriptionService.updatePrescription(reqVO);

        // 药品1和药品2都已存在，应各被 updateById 一次
        verify(prescriptionItemMapper, times(2)).updateById(any(PrescriptionItemDO.class));
        // 药品3：应该 insertBatch（新增）
        verify(prescriptionItemMapper).insertBatch(any());
        // 不应有 delete（所有旧药品都保留）
        verify(prescriptionItemMapper, never()).deleteById(anyLong());
    }

    @Test
    void updatePrescription_shouldDeleteRemovedItems() {
        Long prescriptionId = 100L;
        PrescriptionDO existingPrescription = PrescriptionDO.builder().id(prescriptionId).build();
        when(prescriptionMapper.selectById(prescriptionId)).thenReturn(existingPrescription);

        PrescriptionItemDO oldItem1 = PrescriptionItemDO.builder().id(1L).medicineId(1L).prescriptionId(prescriptionId).build();
        PrescriptionItemDO oldItem2 = PrescriptionItemDO.builder().id(2L).medicineId(2L).prescriptionId(prescriptionId).build();
        when(prescriptionItemMapper.selectListByPrescriptionId(prescriptionId))
                .thenReturn(Arrays.asList(oldItem1, oldItem2));

        when(medicineService.getMedicineListByIds(any())).thenReturn(Collections.singletonList(medicine));

        // 新明细只有药品1，药品2应被删除
        PrescriptionSaveReqVO reqVO = new PrescriptionSaveReqVO();
        reqVO.setId(prescriptionId);
        PrescriptionSaveReqVO.PrescriptionItemSaveVO item1 = new PrescriptionSaveReqVO.PrescriptionItemSaveVO();
        item1.setMedicineId(1L); item1.setQuantity(3); item1.setInstructions("保留");
        reqVO.setItems(Collections.singletonList(item1));

        prescriptionService.updatePrescription(reqVO);

        // 药品2应该被删除
        verify(prescriptionItemMapper).deleteById(2L);
        // 药品1应该被更新
        verify(prescriptionItemMapper).updateById(any(PrescriptionItemDO.class));
    }

    // ==================== dispensePrescription (critical transaction) ====================

    @Test
    void dispensePrescription_shouldDecrementStockAndSetStatus() {
        Long prescriptionId = 100L;
        PrescriptionDO prescription = PrescriptionDO.builder().id(prescriptionId).status("待发药").build();
        when(prescriptionMapper.selectById(prescriptionId)).thenReturn(prescription);

        PrescriptionItemDO item1 = PrescriptionItemDO.builder().id(1L).medicineId(1L).quantity(3).build();
        PrescriptionItemDO item2 = PrescriptionItemDO.builder().id(2L).medicineId(2L).quantity(1).build();
        when(prescriptionItemMapper.selectListByPrescriptionId(prescriptionId))
                .thenReturn(Arrays.asList(item1, item2));

        prescriptionService.dispensePrescription(prescriptionId);

        // 两个药品都应扣减库存
        verify(medicineService).decrementStock(1L, 3);
        verify(medicineService).decrementStock(2L, 1);
        // 处方状态应改为"已发药"
        ArgumentCaptor<PrescriptionDO> captor = ArgumentCaptor.forClass(PrescriptionDO.class);
        verify(prescriptionMapper).updateById(captor.capture());
        assertEquals("已发药", captor.getValue().getStatus());
    }

    @Test
    void dispensePrescription_alreadyDispensed_shouldSkip() {
        Long prescriptionId = 100L;
        PrescriptionDO prescription = PrescriptionDO.builder().id(prescriptionId).status("已发药").build();
        when(prescriptionMapper.selectById(prescriptionId)).thenReturn(prescription);

        prescriptionService.dispensePrescription(prescriptionId);

        // 不应该扣库存
        verify(medicineService, never()).decrementStock(anyLong(), anyInt());
        // 不应该更新状态
        verify(prescriptionMapper, never()).updateById(any(PrescriptionDO.class));
    }

    // ==================== getPrescriptionPage (role isolation) ====================

    @Test
    void getPrescriptionPage_admin_shouldNotFilter() {
        PrescriptionPageReqVO pageReqVO = new PrescriptionPageReqVO();
        when(securityContext.isAdmin()).thenReturn(true);

        prescriptionService.getPrescriptionPage(pageReqVO);

        assertNull(pageReqVO.getDoctorId());
        verify(prescriptionMapper).selectPage(pageReqVO);
    }

    @Test
    void getPrescriptionPage_doctor_shouldFilterByDoctorId() {
        PrescriptionPageReqVO pageReqVO = new PrescriptionPageReqVO();
        when(securityContext.isAdmin()).thenReturn(false);
        when(securityContext.getCurrentDoctorId()).thenReturn(5L);

        prescriptionService.getPrescriptionPage(pageReqVO);

        assertEquals(5L, pageReqVO.getDoctorId());
        verify(prescriptionMapper).selectPage(pageReqVO);
    }

    @Test
    void getPrescriptionPage_nonDoctorNonAdmin_shouldNotFilter() {
        PrescriptionPageReqVO pageReqVO = new PrescriptionPageReqVO();
        when(securityContext.isAdmin()).thenReturn(false);
        when(securityContext.getCurrentDoctorId()).thenReturn(null);

        prescriptionService.getPrescriptionPage(pageReqVO);

        assertNull(pageReqVO.getDoctorId());
        verify(prescriptionMapper).selectPage(pageReqVO);
    }
}
