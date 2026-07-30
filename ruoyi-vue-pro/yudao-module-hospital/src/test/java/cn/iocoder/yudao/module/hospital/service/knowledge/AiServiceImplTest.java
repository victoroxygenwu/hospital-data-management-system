package cn.iocoder.yudao.module.hospital.service.knowledge;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.AssistDiagnosisRespVO;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.ReviewResponseVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DepartmentDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.MedicineDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionItemDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.SymptomDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.VisitDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.DepartmentMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.MedicineMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.PrescriptionItemMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.PrescriptionMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.SymptomMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.VisitMapper;
import cn.iocoder.yudao.module.hospital.framework.ai.DeepSeekClient;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import cn.iocoder.yudao.module.hospital.service.knowledge.dto.DiseaseMatchDTO;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@DisplayName("AI 辅助诊断与处方审核 Service 单元测试")
class AiServiceImplTest {

    private AiServiceImpl aiService;

    @Mock private DeepSeekClient deepSeekClient;
    @Mock private KnowledgeGraphService knowledgeGraphService;
    @Mock private PrescriptionMapper prescriptionMapper;
    @Mock private PrescriptionItemMapper prescriptionItemMapper;
    @Mock private VisitMapper visitMapper;
    @Mock private MedicineMapper medicineMapper;
    @Mock private DepartmentMapper departmentMapper;
    @Mock private SymptomMapper symptomMapper;
    @Mock private HospitalSecurityContext securityContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        initTableInfo(PrescriptionDO.class);
        initTableInfo(PrescriptionItemDO.class);
        initTableInfo(VisitDO.class);
        initTableInfo(DepartmentDO.class);
        initTableInfo(MedicineDO.class);
        initTableInfo(SymptomDO.class);
        aiService = new AiServiceImpl();
        ReflectionTestUtils.setField(aiService, "deepSeekClient", deepSeekClient);
        ReflectionTestUtils.setField(aiService, "knowledgeGraphService", knowledgeGraphService);
        ReflectionTestUtils.setField(aiService, "prescriptionMapper", prescriptionMapper);
        ReflectionTestUtils.setField(aiService, "prescriptionItemMapper", prescriptionItemMapper);
        ReflectionTestUtils.setField(aiService, "visitMapper", visitMapper);
        ReflectionTestUtils.setField(aiService, "medicineMapper", medicineMapper);
        ReflectionTestUtils.setField(aiService, "departmentMapper", departmentMapper);
        ReflectionTestUtils.setField(aiService, "symptomMapper", symptomMapper);
        ReflectionTestUtils.setField(aiService, "securityContext", securityContext);
        // 单测聚焦审核/降级逻辑：以管理员身份绕过处方归属校验
        when(securityContext.isAdmin()).thenReturn(true);
        when(securityContext.getCurrentDoctorId()).thenReturn(null);
    }

    private void initTableInfo(Class<?> clazz) {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), clazz);
    }

    @Test
    @DisplayName("assistDiagnosis-AI不可用走离线降级")
    void assistDiagnosis_offline() {
        // AI 客户端不可用（无 key / 网络异常），chat 始终返回 null
        when(deepSeekClient.chat(anyString(), anyString())).thenReturn(null);

        // 离线症状提取：从文本中匹配已知症状名称
        SymptomDO fever = new SymptomDO();
        fever.setId(1L);
        fever.setName("发热");
        when(symptomMapper.selectList()).thenReturn(Collections.singletonList(fever));

        // 知识图谱匹配
        when(knowledgeGraphService.matchSymptoms(any())).thenReturn(List.of(1L));
        DiseaseMatchDTO match = new DiseaseMatchDTO();
        match.setId(10L);
        match.setName("感冒");
        match.setIcdCode("J11");
        match.setMatchCount(1);
        match.setDeptId(1L);
        when(knowledgeGraphService.findDiseasesBySymptoms(any())).thenReturn(List.of(match));

        DepartmentDO dept = new DepartmentDO();
        dept.setId(1L);
        dept.setDeptName("呼吸内科");
        when(departmentMapper.selectById(1L)).thenReturn(dept);

        AssistDiagnosisRespVO resp = aiService.assistDiagnosis("患者发热咳嗽");
        assertNotNull(resp);
        assertTrue(resp.getOffline());
        assertEquals("呼吸内科", resp.getReferenceDept());
        assertFalse(resp.getMatchedDiseases().isEmpty());
        assertEquals("感冒", resp.getMatchedDiseases().get(0).getName());
    }

    @Test
    @DisplayName("prescriptionReview-处方不存在抛异常")
    void prescriptionReview_notFound() {
        when(prescriptionMapper.selectById(999L)).thenReturn(null);
        assertThrows(ServiceException.class, () -> aiService.prescriptionReview(999L));
    }

    @Test
    @DisplayName("prescriptionReview-AI不可用走离线降级(无标准方案->MEDIUM)")
    void prescriptionReview_offline_noStandard() {
        when(deepSeekClient.chat(anyString(), anyString())).thenReturn(null);

        PrescriptionDO prescription = new PrescriptionDO();
        prescription.setId(1L);
        prescription.setVisitId(100L);
        when(prescriptionMapper.selectById(1L)).thenReturn(prescription);

        PrescriptionItemDO item = new PrescriptionItemDO();
        item.setMedicineId(7L);
        when(prescriptionItemMapper.selectListByPrescriptionId(1L)).thenReturn(Collections.singletonList(item));

        MedicineDO medicine = new MedicineDO();
        medicine.setId(7L);
        medicine.setName("布洛芬");
        when(medicineMapper.selectListByMedicineIds(any())).thenReturn(Collections.singletonList(medicine));

        VisitDO visit = new VisitDO();
        visit.setDiagnosis("普通感冒");
        when(visitMapper.selectById(100L)).thenReturn(visit);

        // 无标准用药方案
        when(knowledgeGraphService.getDiseaseMedicineMapping("普通感冒")).thenReturn(Collections.emptyMap());

        ReviewResponseVO resp = aiService.prescriptionReview(1L);
        assertNotNull(resp);
        assertTrue(resp.getOffline());
        assertEquals("MEDIUM", resp.getRiskLevel());
        assertNotNull(resp.getAdvice());
    }

    @Test
    @DisplayName("prescriptionReview-离线降级(标准方案一致->LOW)")
    void prescriptionReview_offline_matchStandard() {
        when(deepSeekClient.chat(anyString(), anyString())).thenReturn(null);

        PrescriptionDO prescription = new PrescriptionDO();
        prescription.setId(1L);
        prescription.setVisitId(100L);
        when(prescriptionMapper.selectById(1L)).thenReturn(prescription);

        PrescriptionItemDO item = new PrescriptionItemDO();
        item.setMedicineId(7L);
        when(prescriptionItemMapper.selectListByPrescriptionId(1L)).thenReturn(Collections.singletonList(item));

        MedicineDO medicine = new MedicineDO();
        medicine.setId(7L);
        medicine.setName("布洛芬");
        when(medicineMapper.selectListByMedicineIds(any())).thenReturn(Collections.singletonList(medicine));

        VisitDO visit = new VisitDO();
        visit.setDiagnosis("普通感冒");
        when(visitMapper.selectById(100L)).thenReturn(visit);

        MedicineDO std = new MedicineDO();
        std.setId(7L);
        std.setName("布洛芬");
        when(knowledgeGraphService.getDiseaseMedicineMapping("普通感冒"))
                .thenReturn(Collections.singletonMap("普通感冒", List.of(std)));

        ReviewResponseVO resp = aiService.prescriptionReview(1L);
        assertNotNull(resp);
        assertTrue(resp.getOffline());
        assertEquals("LOW", resp.getRiskLevel());
    }
}
