package cn.iocoder.yudao.module.hospital.service.bed;

import cn.iocoder.yudao.module.hospital.dal.dataobject.BedDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.BedMapper;
import cn.iocoder.yudao.module.hospital.enums.BedStatusEnum;
import cn.iocoder.yudao.module.hospital.service.ward.WardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
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
        BedDO bed = BedDO.builder().id(1L).wardId(10L).status(BedStatusEnum.FREE.getCode()).build();
        when(bedMapper.selectById(1L)).thenReturn(bed);
        when(bedMapper.assignBed(1L, 100L)).thenReturn(1);

        bedService.assignBed(1L, 100L);

        verify(bedMapper).assignBed(1L, 100L);
        verify(wardService).incrementUsedBeds(10L);
    }

    @Test
    void assignBed_alreadyOccupied_shouldThrow() {
        BedDO bed = BedDO.builder().id(1L).wardId(10L).status(BedStatusEnum.OCCUPIED.getCode()).build();
        when(bedMapper.selectById(1L)).thenReturn(bed);
        when(bedMapper.assignBed(1L, 100L)).thenReturn(0);

        assertThrows(Exception.class, () -> bedService.assignBed(1L, 100L));

        verify(wardService, never()).incrementUsedBeds(anyLong());
    }

    // ==================== releaseBed ====================

    @Test
    void releaseBed_shouldClearAndDecrementWard() {
        BedDO bed = BedDO.builder().id(1L).wardId(10L).status(BedStatusEnum.OCCUPIED.getCode())
                .patientId(100L).build();
        when(bedMapper.selectById(1L)).thenReturn(bed);
        when(bedMapper.releaseBed(1L)).thenReturn(1);

        bedService.releaseBed(1L);

        verify(bedMapper).releaseBed(1L);
        verify(wardService).decrementUsedBeds(10L);
    }

    @Test
    void releaseBed_notOccupied_shouldThrow() {
        BedDO bed = BedDO.builder().id(1L).wardId(10L).status(BedStatusEnum.FREE.getCode()).build();
        when(bedMapper.selectById(1L)).thenReturn(bed);
        when(bedMapper.releaseBed(1L)).thenReturn(0);

        assertThrows(Exception.class, () -> bedService.releaseBed(1L));

        verify(wardService, never()).decrementUsedBeds(anyLong());
    }
}
