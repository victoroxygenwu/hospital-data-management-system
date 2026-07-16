package cn.iocoder.yudao.module.hospital.service.stats;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.hospital.controller.admin.stats.vo.VisitTrendVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.MedicineDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.VisitDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.WardDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.MedicineMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.VisitMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.WardMapper;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("统计 Service 单元测试（覆盖非管理员权限拦截）")
class StatsServiceImplTest {

    private StatsServiceImpl statsService;

    @Mock
    private HospitalSecurityContext securityContext;
    @Mock
    private VisitMapper visitMapper;
    @Mock
    private WardMapper wardMapper;
    @Mock
    private MedicineMapper medicineMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), VisitDO.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), WardDO.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), MedicineDO.class);
        statsService = new StatsServiceImpl();
        ReflectionTestUtils.setField(statsService, "securityContext", securityContext);
        ReflectionTestUtils.setField(statsService, "visitMapper", visitMapper);
        ReflectionTestUtils.setField(statsService, "wardMapper", wardMapper);
        ReflectionTestUtils.setField(statsService, "medicineMapper", medicineMapper);
    }

    @Test
    @DisplayName("就诊趋势-非管理员抛 HOSPITAL_DATA_ACCESS_DENIED")
    void getVisitTrend_nonAdmin_throws() {
        when(securityContext.isAdmin()).thenReturn(false);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> statsService.getVisitTrend("2026-01-01", "2026-01-31"));
        assertEquals(ErrorCodeConstants.HOSPITAL_DATA_ACCESS_DENIED.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("病房使用率-非管理员抛 HOSPITAL_DATA_ACCESS_DENIED")
    void getWardUsage_nonAdmin_throws() {
        when(securityContext.isAdmin()).thenReturn(false);
        ServiceException ex = assertThrows(ServiceException.class, () -> statsService.getWardUsage());
        assertEquals(ErrorCodeConstants.HOSPITAL_DATA_ACCESS_DENIED.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("药品库存-非管理员抛 HOSPITAL_DATA_ACCESS_DENIED")
    void getMedicineStock_nonAdmin_throws() {
        when(securityContext.isAdmin()).thenReturn(false);
        ServiceException ex = assertThrows(ServiceException.class, () -> statsService.getMedicineStock());
        assertEquals(ErrorCodeConstants.HOSPITAL_DATA_ACCESS_DENIED.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("就诊趋势-管理员返回聚合结果")
    void getVisitTrend_admin_returnsAggregated() {
        when(securityContext.isAdmin()).thenReturn(true);
        VisitDO v = new VisitDO();
        v.setVisitDate(java.time.LocalDateTime.now());
        when(visitMapper.selectList(any())).thenReturn(java.util.Collections.singletonList(v));

        List<VisitTrendVO> result = statsService.getVisitTrend(null, null);
        assertEquals(1, result.size());
    }
}
