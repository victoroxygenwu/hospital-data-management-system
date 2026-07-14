package cn.iocoder.yudao.module.hospital.service.bed;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.hospital.controller.admin.bed.vo.BedSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BedDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.WardDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.BedMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.WardMapper;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("床位 Service 单元测试（覆盖容量校验与并发条件更新）")
class BedServiceImplTest {

    private BedServiceImpl bedService;

    @Mock
    private BedMapper bedMapper;
    @Mock
    private WardMapper wardMapper;
    @Mock
    private WardService wardService;
    @Mock
    private HospitalSecurityContext securityContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 纯 Mockito 单元测试没有 Spring/MyBatis 上下文，手动注册 BedDO 的 lambda 元数据，
        // 否则 Service 内部 new LambdaUpdateWrapper<BedDO>().eq(BedDO::getStatus, ...)
        // 解析列名时会抛 MybatisPlusException: can not find lambda cache for this entity
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), BedDO.class);
        bedService = new BedServiceImpl();
        ReflectionTestUtils.setField(bedService, "bedMapper", bedMapper);
        ReflectionTestUtils.setField(bedService, "wardMapper", wardMapper);
        ReflectionTestUtils.setField(bedService, "wardService", wardService);
        ReflectionTestUtils.setField(bedService, "securityContext", securityContext);
    }

    @Test
    @DisplayName("分配床位-成功：条件更新影响行数=1，递增病房已用床位")
    void assignBed_success() {
        BedDO bed = new BedDO();
        bed.setId(1L);
        bed.setWardId(10L);
        bed.setStatus(0);
        when(bedMapper.selectById(1L)).thenReturn(bed);
        when(bedMapper.update(any(), any())).thenReturn(1);

        bedService.assignBed(1L, 99L);

        verify(wardService).incrementUsedBeds(10L);
    }

    @Test
    @DisplayName("分配床位-并发双占被拒：条件更新影响行数=0，不递增且不重复占用")
    void assignBed_concurrentDoubleOccupy_rejected() {
        BedDO bed = new BedDO();
        bed.setId(1L);
        bed.setWardId(10L);
        bed.setStatus(0);
        when(bedMapper.selectById(1L)).thenReturn(bed);
        // 模拟并发：另一请求已把状态改为占用(1)，本条 UPDATE ... WHERE status=0 影响 0 行
        when(bedMapper.update(any(), any())).thenReturn(0);

        ServiceException ex = assertThrows(ServiceException.class, () -> bedService.assignBed(1L, 99L));
        assertEquals(ErrorCodeConstants.BED_ALREADY_OCCUPIED.getCode(), ex.getCode());
        verify(wardService, never()).incrementUsedBeds(anyLong());
    }

    @Test
    @DisplayName("分配床位-已占用被拒：状态!=0 直接抛异常，不执行更新")
    void assignBed_alreadyOccupied_rejected() {
        BedDO bed = new BedDO();
        bed.setId(1L);
        bed.setWardId(10L);
        bed.setStatus(1);
        when(bedMapper.selectById(1L)).thenReturn(bed);

        ServiceException ex = assertThrows(ServiceException.class, () -> bedService.assignBed(1L, 99L));
        assertEquals(ErrorCodeConstants.BED_ALREADY_OCCUPIED.getCode(), ex.getCode());
        verify(bedMapper, never()).update(any(), any());
    }

    @Test
    @DisplayName("释放床位-成功：条件更新影响行数=1，递减病房已用床位")
    void releaseBed_success() {
        BedDO bed = new BedDO();
        bed.setId(1L);
        bed.setWardId(10L);
        bed.setStatus(1);
        when(bedMapper.selectById(1L)).thenReturn(bed);
        when(bedMapper.update(any(), any())).thenReturn(1);

        bedService.releaseBed(1L);

        verify(wardService).decrementUsedBeds(10L);
    }

    @Test
    @DisplayName("释放床位-重复释放被拒：条件更新影响行数=0")
    void releaseBed_doubleRelease_rejected() {
        BedDO bed = new BedDO();
        bed.setId(1L);
        bed.setWardId(10L);
        bed.setStatus(1);
        when(bedMapper.selectById(1L)).thenReturn(bed);
        when(bedMapper.update(any(), any())).thenReturn(0);

        ServiceException ex = assertThrows(ServiceException.class, () -> bedService.releaseBed(1L));
        assertEquals(ErrorCodeConstants.BED_NOT_OCCUPIED.getCode(), ex.getCode());
        verify(wardService, never()).decrementUsedBeds(anyLong());
    }

    @Test
    @DisplayName("创建床位-超过病房容量被拒：不插入床位")
    void createBed_capacityExceeded_rejected() {
        WardDO ward = new WardDO();
        ward.setId(5L);
        ward.setCapacity(2);
        when(wardMapper.selectById(5L)).thenReturn(ward);
        when(bedMapper.selectCount(any())).thenReturn(2L);

        BedSaveReqVO req = new BedSaveReqVO();
        req.setWardId(5L);
        req.setBedNo("B-999");

        ServiceException ex = assertThrows(ServiceException.class, () -> bedService.createBed(req));
        assertEquals(ErrorCodeConstants.WARD_BED_EXCEED_CAPACITY.getCode(), ex.getCode());
        verify(bedMapper, never()).insert(any(BedDO.class));
    }
}
