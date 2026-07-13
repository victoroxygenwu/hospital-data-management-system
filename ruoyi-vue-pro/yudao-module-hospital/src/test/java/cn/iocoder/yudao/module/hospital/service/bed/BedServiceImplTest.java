package cn.iocoder.yudao.module.hospital.service.bed;

import cn.iocoder.yudao.module.hospital.dal.dataobject.BedDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.BedMapper;
import cn.iocoder.yudao.module.hospital.service.ward.WardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BedServiceImplTest {

    @Mock
    private BedMapper bedMapper;
    @Mock
    private WardService wardService;

    @InjectMocks
    private BedServiceImpl bedService;

    // ==================== assignBed ====================

    @Test
    void assignBed_shouldSetOccupiedAndIncrementWard() {
        BedDO bed = BedDO.builder().id(1L).wardId(10L).status("空闲").build();
        when(bedMapper.selectById(1L)).thenReturn(bed);

        bedService.assignBed(1L, 100L);

        // 床位状态应更新为"已占用"并记录患者
        ArgumentCaptor<BedDO> captor = ArgumentCaptor.forClass(BedDO.class);
        verify(bedMapper).updateById(captor.capture());
        BedDO updated = captor.getValue();
        assertEquals(1L, updated.getId());
        assertEquals("已占用", updated.getStatus());
        assertEquals(100L, updated.getPatientId());
        assertNotNull(updated.getAdmissionTime());

        // 应调用 WardService 增加已用床位数
        verify(wardService).incrementUsedBeds(10L);
    }

    @Test
    void assignBed_alreadyOccupied_shouldThrow() {
        BedDO bed = BedDO.builder().id(1L).wardId(10L).status("已占用").build();
        when(bedMapper.selectById(1L)).thenReturn(bed);

        assertThrows(Exception.class, () -> bedService.assignBed(1L, 100L));

        // 不应该更新任何数据
        verify(bedMapper, never()).updateById(any(BedDO.class));
        verify(wardService, never()).incrementUsedBeds(anyLong());
    }

    // ==================== releaseBed ====================

    @Test
    void releaseBed_shouldClearAndDecrementWard() {
        BedDO bed = BedDO.builder().id(1L).wardId(10L).status("已占用")
                .patientId(100L).build();
        when(bedMapper.selectById(1L)).thenReturn(bed);

        bedService.releaseBed(1L);

        verify(bedMapper).releaseBed(1L);
        verify(wardService).decrementUsedBeds(10L);
    }

    @Test
    void releaseBed_notOccupied_shouldThrow() {
        BedDO bed = BedDO.builder().id(1L).wardId(10L).status("空闲").build();
        when(bedMapper.selectById(1L)).thenReturn(bed);

        assertThrows(Exception.class, () -> bedService.releaseBed(1L));

        verify(bedMapper, never()).releaseBed(anyLong());
        verify(wardService, never()).decrementUsedBeds(anyLong());
    }
}
