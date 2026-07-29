package cn.iocoder.yudao.module.hospital.service.visual;

import cn.iocoder.yudao.module.hospital.controller.admin.visual.vo.*;
import cn.iocoder.yudao.module.hospital.dal.dataobject.*;
import cn.iocoder.yudao.module.hospital.dal.mysql.*;
import cn.iocoder.yudao.module.hospital.enums.VisitStatusEnum;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("可视化 Service 单元测试")
class VisualServiceImplTest {

    private VisualServiceImpl visualService;

    @Mock private HospitalSecurityContext securityContext;
    @Mock private VisitMapper visitMapper;
    @Mock private PatientMapper patientMapper;
    @Mock private PrescriptionItemMapper prescriptionItemMapper;
    @Mock private BillMapper billMapper;
    @Mock private DepartmentMapper departmentMapper;
    @Mock private MedicineMapper medicineMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        initTableInfo(VisitDO.class);
        initTableInfo(PatientDO.class);
        initTableInfo(PrescriptionItemDO.class);
        initTableInfo(BillDO.class);
        initTableInfo(DepartmentDO.class);
        initTableInfo(MedicineDO.class);
        visualService = new VisualServiceImpl();
        ReflectionTestUtils.setField(visualService, "securityContext", securityContext);
        ReflectionTestUtils.setField(visualService, "visitMapper", visitMapper);
        ReflectionTestUtils.setField(visualService, "patientMapper", patientMapper);
        ReflectionTestUtils.setField(visualService, "prescriptionItemMapper", prescriptionItemMapper);
        ReflectionTestUtils.setField(visualService, "billMapper", billMapper);
        ReflectionTestUtils.setField(visualService, "departmentMapper", departmentMapper);
        ReflectionTestUtils.setField(visualService, "medicineMapper", medicineMapper);
    }

    private void initTableInfo(Class<?> clazz) {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), clazz);
    }

    @Test
    @DisplayName("热力图-管理员聚合各时段")
    void getHeatmapData_admin() {
        when(securityContext.isAdmin()).thenReturn(true);
        DepartmentDO dept = new DepartmentDO();
        dept.setId(1L);
        dept.setDeptName("内科");
        when(departmentMapper.selectList(any())).thenReturn(Collections.singletonList(dept));

        VisitDO v1 = new VisitDO();
        v1.setDeptId(1L);
        v1.setVisitDate(LocalDateTime.of(2026, 1, 1, 9, 0));
        VisitDO v2 = new VisitDO();
        v2.setDeptId(1L);
        v2.setVisitDate(LocalDateTime.of(2026, 1, 1, 9, 30));
        VisitDO v3 = new VisitDO();
        v3.setDeptId(1L);
        v3.setVisitDate(LocalDateTime.of(2026, 1, 1, 10, 0));
        when(visitMapper.selectList(any())).thenReturn(Arrays.asList(v1, v2, v3));

        List<HeatmapVO> result = visualService.getHeatmapData();
        assertEquals(2, result.size());
        HeatmapVO hour9 = result.stream().filter(r -> r.getHour() == 9).findFirst().orElse(null);
        assertNotNull(hour9);
        assertEquals(2L, hour9.getCount());
    }

    @Test
    @DisplayName("雷达图-治愈率与平均费用")
    void getDeptRadarData_admin() {
        when(securityContext.isAdmin()).thenReturn(true);
        DepartmentDO dept = new DepartmentDO();
        dept.setId(1L);
        dept.setDeptName("内科");
        when(departmentMapper.selectList(any())).thenReturn(Collections.singletonList(dept));

        VisitDO v1 = visit(1L, 1L, VisitStatusEnum.COMPLETED.getStatus());
        VisitDO v2 = visit(2L, 1L, VisitStatusEnum.COMPLETED.getStatus());
        VisitDO v3 = visit(3L, 1L, VisitStatusEnum.COMPLETED.getStatus());
        VisitDO v4 = visit(4L, 1L, 1);
        when(visitMapper.selectList(any())).thenReturn(Arrays.asList(v1, v2, v3, v4));

        BillDO b1 = new BillDO();
        b1.setVisitId(1L);
        b1.setTotalAmount(new BigDecimal("100"));
        BillDO b2 = new BillDO();
        b2.setVisitId(2L);
        b2.setTotalAmount(new BigDecimal("200"));
        BillDO b3 = new BillDO();
        b3.setVisitId(3L);
        b3.setTotalAmount(new BigDecimal("300"));
        when(billMapper.selectList(any())).thenReturn(Arrays.asList(b1, b2, b3));

        List<DeptRadarVO> result = visualService.getDeptRadarData();
        assertEquals(1, result.size());
        assertEquals(4L, result.get(0).getVisitCount());
        assertEquals(0.75, result.get(0).getCureRate(), 0.001);
        assertEquals(200.0, result.get(0).getAvgFee(), 0.001);
    }

    @Test
    @DisplayName("患者画像-年龄/地区/医保")
    void getPatientProfile_admin() {
        when(securityContext.isAdmin()).thenReturn(true);
        PatientDO p1 = new PatientDO();
        p1.setGender("男");
        p1.setBirthDate(LocalDate.now().minusYears(20));
        p1.setAddress("北京市朝阳区");
        p1.setInsuranceNo("YB001");
        PatientDO p2 = new PatientDO();
        p2.setGender("女");
        p2.setBirthDate(LocalDate.now().minusYears(40));
        p2.setAddress("上海市浦东");
        when(patientMapper.selectList(any())).thenReturn(Arrays.asList(p1, p2));

        PatientProfileRespVO result = visualService.getPatientProfile();
        assertFalse(result.getAgeList().isEmpty());
        assertFalse(result.getRegionList().isEmpty());
        assertEquals(2, result.getInsuranceList().size());
    }

    @Test
    @DisplayName("疾病趋势-按月聚合")
    void getDiseaseSeasonal_admin() {
        when(securityContext.isAdmin()).thenReturn(true);
        VisitDO v1 = new VisitDO();
        v1.setVisitDate(LocalDateTime.of(2026, 1, 15, 10, 0));
        VisitDO v2 = new VisitDO();
        v2.setVisitDate(LocalDateTime.of(2026, 1, 20, 11, 0));
        VisitDO v3 = new VisitDO();
        v3.setVisitDate(LocalDateTime.of(2026, 2, 5, 9, 0));
        when(visitMapper.selectList(any())).thenReturn(Arrays.asList(v1, v2, v3));

        List<DiseaseSeasonalVO> result = visualService.getDiseaseSeasonal();
        assertEquals(2, result.size());
        assertEquals("ALL", result.get(0).getDiagnosis());
    }

    @Test
    @DisplayName("药品共现-去重且>=2次")
    void getMedicineCooccurrence_admin() {
        when(securityContext.isAdmin()).thenReturn(true);
        MedicineDO m1 = new MedicineDO();
        m1.setId(1L);
        m1.setName("阿莫西林");
        MedicineDO m2 = new MedicineDO();
        m2.setId(2L);
        m2.setName("布洛芬");
        when(medicineMapper.selectList(any())).thenReturn(Arrays.asList(m1, m2));

        PrescriptionItemDO i1 = item(1L, 1L, 1L);
        PrescriptionItemDO i2 = item(2L, 1L, 2L);
        PrescriptionItemDO i3 = item(3L, 2L, 1L);
        PrescriptionItemDO i4 = item(4L, 2L, 2L);
        when(prescriptionItemMapper.selectList(any())).thenReturn(Arrays.asList(i1, i2, i3, i4));

        List<MedicineCooccurrenceVO> result = visualService.getMedicineCooccurrence();
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getCoCount());
    }

    @Test
    @DisplayName("空数据-非管理员返回空")
    void nonAdmin_returnsEmpty() {
        when(securityContext.isAdmin()).thenReturn(false);
        assertTrue(visualService.getHeatmapData().isEmpty());
        assertTrue(visualService.getDeptRadarData().isEmpty());
        assertTrue(visualService.getDiseaseSeasonal().isEmpty());
        assertTrue(visualService.getMedicineCooccurrence().isEmpty());
        PatientProfileRespVO profile = visualService.getPatientProfile();
        assertTrue(profile.getAgeList().isEmpty());
    }

    @Test
    @DisplayName("空数据-管理员空列表不NPE")
    void admin_emptyList_noNpe() {
        when(securityContext.isAdmin()).thenReturn(true);
        when(departmentMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(visitMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(patientMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(prescriptionItemMapper.selectList(any())).thenReturn(Collections.emptyList());
        assertDoesNotThrow(() -> {
            visualService.getHeatmapData();
            visualService.getDeptRadarData();
            visualService.getPatientProfile();
            visualService.getDiseaseSeasonal();
            visualService.getMedicineCooccurrence();
        });
    }

    private VisitDO visit(Long id, Long deptId, Integer status) {
        VisitDO v = new VisitDO();
        v.setId(id);
        v.setDeptId(deptId);
        v.setStatus(status);
        return v;
    }

    private PrescriptionItemDO item(Long id, Long prescriptionId, Long medicineId) {
        PrescriptionItemDO item = new PrescriptionItemDO();
        item.setId(id);
        item.setPrescriptionId(prescriptionId);
        item.setMedicineId(medicineId);
        return item;
    }
}
