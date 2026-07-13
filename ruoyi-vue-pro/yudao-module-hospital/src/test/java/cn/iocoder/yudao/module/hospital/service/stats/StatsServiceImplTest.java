package cn.iocoder.yudao.module.hospital.service.stats;

import cn.iocoder.yudao.module.hospital.controller.admin.stats.vo.MedicineStockVO;
import cn.iocoder.yudao.module.hospital.controller.admin.stats.vo.VisitTrendVO;
import cn.iocoder.yudao.module.hospital.controller.admin.stats.vo.WardUsageVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.MedicineDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.VisitDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.WardDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.MedicineMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.VisitMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.WardMapper;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceImplTest {

    @Mock
    private HospitalSecurityContext securityContext;
    @Mock
    private VisitMapper visitMapper;
    @Mock
    private WardMapper wardMapper;
    @Mock
    private MedicineMapper medicineMapper;

    @InjectMocks
    private StatsServiceImpl statsService;

    // ==================== admin role ====================

    @Test
    void getVisitTrend_admin_shouldReturnData() {
        when(securityContext.isAdmin()).thenReturn(true);
        VisitDO visit = VisitDO.builder()
                .visitDate(LocalDateTime.of(2026, 7, 13, 9, 0)).build();
        when(visitMapper.selectList(any()))
                .thenReturn(Collections.singletonList(visit));

        List<VisitTrendVO> result = statsService.getVisitTrend("2026-07-01", "2026-07-31");

        assertFalse(result.isEmpty());
        assertEquals("2026-07-13", result.get(0).getDate());
        assertEquals(1L, result.get(0).getCount());
    }

    @Test
    void getWardUsage_admin_shouldReturnData() {
        when(securityContext.isAdmin()).thenReturn(true);
        WardDO ward = WardDO.builder().id(1L).wardNo("301").capacity(6).usedBeds(4).build();
        when(wardMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ward));

        List<WardUsageVO> result = statsService.getWardUsage();

        assertFalse(result.isEmpty());
        assertEquals("301", result.get(0).getWardNo());
        assertEquals("66.7%", result.get(0).getUsageRate());
    }

    @Test
    void getMedicineStock_admin_shouldReturnData() {
        when(securityContext.isAdmin()).thenReturn(true);
        MedicineDO med = MedicineDO.builder().id(1L).name("阿莫西林")
                .stock(5).specification("0.25g").unit("盒").expiryDate(null).build();
        when(medicineMapper.selectList(any()))
                .thenReturn(Collections.singletonList(med));

        List<MedicineStockVO> result = statsService.getMedicineStock();

        assertFalse(result.isEmpty());
        assertEquals("阿莫西林", result.get(0).getName());
        assertEquals(5, result.get(0).getStock());
        assertTrue(result.get(0).getStockWarning()); // stock < 10
    }

    // ==================== non-admin role ====================

    @Test
    void getVisitTrend_nonAdmin_shouldReturnEmpty() {
        when(securityContext.isAdmin()).thenReturn(false);

        List<VisitTrendVO> result = statsService.getVisitTrend(null, null);

        assertTrue(result.isEmpty());
        verify(visitMapper, never()).selectList(any());
    }

    @Test
    void getWardUsage_nonAdmin_shouldReturnEmpty() {
        when(securityContext.isAdmin()).thenReturn(false);

        List<WardUsageVO> result = statsService.getWardUsage();

        assertTrue(result.isEmpty());
        verify(wardMapper, never()).selectList(any());
    }

    @Test
    void getMedicineStock_nonAdmin_shouldReturnEmpty() {
        when(securityContext.isAdmin()).thenReturn(false);

        List<MedicineStockVO> result = statsService.getMedicineStock();

        assertTrue(result.isEmpty());
        verify(medicineMapper, never()).selectList(any());
    }

    // ==================== edge cases ====================

    @Test
    void getWardUsage_zeroCapacity_shouldReturnZeroRate() {
        when(securityContext.isAdmin()).thenReturn(true);
        WardDO ward = WardDO.builder().id(1L).wardNo("401").capacity(0).usedBeds(0).build();
        when(wardMapper.selectList(any()))
                .thenReturn(Collections.singletonList(ward));

        List<WardUsageVO> result = statsService.getWardUsage();

        assertEquals("0%", result.get(0).getUsageRate());
    }

    @Test
    void getMedicineStock_highStock_shouldNotWarn() {
        when(securityContext.isAdmin()).thenReturn(true);
        MedicineDO med = MedicineDO.builder().id(1L).name("创可贴").stock(500).build();
        when(medicineMapper.selectList(any()))
                .thenReturn(Collections.singletonList(med));

        List<MedicineStockVO> result = statsService.getMedicineStock();

        assertFalse(result.get(0).getStockWarning());
    }
}
