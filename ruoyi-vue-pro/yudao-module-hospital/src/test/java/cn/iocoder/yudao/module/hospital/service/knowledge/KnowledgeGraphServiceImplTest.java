package cn.iocoder.yudao.module.hospital.service.knowledge;

import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseaseNetworkRespVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DiseaseDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DiseaseSymptomDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DiseaseMedicineDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.MedicineDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.SymptomDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.DiseaseMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.DiseaseMedicineMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.DiseaseSymptomMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.MedicineMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.SymptomMapper;
import cn.iocoder.yudao.module.hospital.service.knowledge.dto.DiseaseMatchDTO;
import cn.iocoder.yudao.module.hospital.service.knowledge.dto.NetworkEdgeDTO;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("知识图谱 Service 单元测试")
class KnowledgeGraphServiceImplTest {

    private KnowledgeGraphServiceImpl knowledgeService;

    @Mock private DiseaseMapper diseaseMapper;
    @Mock private SymptomMapper symptomMapper;
    @Mock private DiseaseSymptomMapper diseaseSymptomMapper;
    @Mock private DiseaseMedicineMapper diseaseMedicineMapper;
    @Mock private MedicineMapper medicineMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        initTableInfo(DiseaseDO.class);
        initTableInfo(SymptomDO.class);
        initTableInfo(DiseaseSymptomDO.class);
        initTableInfo(DiseaseMedicineDO.class);
        initTableInfo(MedicineDO.class);
        knowledgeService = new KnowledgeGraphServiceImpl();
        ReflectionTestUtils.setField(knowledgeService, "diseaseMapper", diseaseMapper);
        ReflectionTestUtils.setField(knowledgeService, "symptomMapper", symptomMapper);
        ReflectionTestUtils.setField(knowledgeService, "diseaseSymptomMapper", diseaseSymptomMapper);
        ReflectionTestUtils.setField(knowledgeService, "diseaseMedicineMapper", diseaseMedicineMapper);
        ReflectionTestUtils.setField(knowledgeService, "medicineMapper", medicineMapper);
    }

    private void initTableInfo(Class<?> clazz) {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), clazz);
    }

    @Test
    @DisplayName("getDiseaseNetwork-节点与边编码正确")
    void getDiseaseNetwork_buildsGraph() {
        DiseaseDO disease = new DiseaseDO();
        disease.setId(10L);
        disease.setName("感冒");
        when(diseaseMapper.selectById(10L)).thenReturn(disease);

        NetworkEdgeDTO symptomEdge = new NetworkEdgeDTO();
        symptomEdge.setType("symptom");
        symptomEdge.setId(5L);
        symptomEdge.setName("发热");
        symptomEdge.setEdgeValue(1);
        when(diseaseSymptomMapper.selectSymptomEdgesByDiseaseId(10L))
                .thenReturn(Collections.singletonList(symptomEdge));

        NetworkEdgeDTO medicineEdge = new NetworkEdgeDTO();
        medicineEdge.setType("medicine");
        medicineEdge.setId(7L);
        medicineEdge.setName("布洛芬");
        medicineEdge.setEdgeValue(1);
        when(diseaseMedicineMapper.selectMedicineEdgesByDiseaseId(10L))
                .thenReturn(Collections.singletonList(medicineEdge));

        DiseaseNetworkRespVO resp = knowledgeService.getDiseaseNetwork(10L);
        assertEquals(3, resp.getNodes().size());
        assertEquals(2, resp.getLinks().size());

        // 症状节点 id 编码为 -symptomId，药品节点 id 编码为 1_000_000 + medicineId
        DiseaseNetworkRespVO.Node symptomNode = resp.getNodes().stream()
                .filter(n -> n.getCategory() == 1).findFirst().orElse(null);
        DiseaseNetworkRespVO.Node medicineNode = resp.getNodes().stream()
                .filter(n -> n.getCategory() == 2).findFirst().orElse(null);
        assertNotNull(symptomNode);
        assertNotNull(medicineNode);
        assertEquals(-5L, symptomNode.getId().longValue());
        assertEquals(1_000_000L + 7L, medicineNode.getId().longValue());
    }

    @Test
    @DisplayName("getDiseaseNetwork-疾病不存在抛异常")
    void getDiseaseNetwork_notFound() {
        when(diseaseMapper.selectById(99L)).thenReturn(null);
        assertThrows(ServiceException.class, () -> knowledgeService.getDiseaseNetwork(99L));
    }

    @Test
    @DisplayName("matchSymptoms-按名称模糊匹配返回去重ID")
    void matchSymptoms_matches() {
        SymptomDO s1 = new SymptomDO();
        s1.setId(1L);
        s1.setName("发热");
        SymptomDO s2 = new SymptomDO();
        s2.setId(2L);
        s2.setName("高热"); // 同样匹配 "发热" 子串
        when(symptomMapper.selectListByNameLike("发热")).thenReturn(Arrays.asList(s1, s2));

        List<Long> result = knowledgeService.matchSymptoms(Collections.singletonList("发热"));
        assertEquals(2, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(2L));
    }

    @Test
    @DisplayName("matchSymptoms-空输入返回空")
    void matchSymptoms_empty() {
        assertTrue(knowledgeService.matchSymptoms(Collections.emptyList()).isEmpty());
        assertTrue(knowledgeService.matchSymptoms(null).isEmpty());
    }

    @Test
    @DisplayName("findDiseasesBySymptoms-按症状ID匹配疾病")
    void findDiseasesBySymptoms_matches() {
        DiseaseMatchDTO dto = new DiseaseMatchDTO();
        dto.setId(10L);
        dto.setName("感冒");
        dto.setIcdCode("J11");
        dto.setMatchCount(2);
        dto.setDeptId(1L);
        when(diseaseSymptomMapper.selectDiseasesBySymptomIds(any())).thenReturn(Collections.singletonList(dto));

        List<DiseaseMatchDTO> result = knowledgeService.findDiseasesBySymptoms(Collections.singletonList(1L));
        assertEquals(1, result.size());
        assertEquals("感冒", result.get(0).getName());
    }

    @Test
    @DisplayName("getDiseaseMedicineMapping-空诊断返回空")
    void getDiseaseMedicineMapping_emptyDiagnosis() {
        assertTrue(knowledgeService.getDiseaseMedicineMapping("").isEmpty());
        assertTrue(knowledgeService.getDiseaseMedicineMapping(null).isEmpty());
    }

    @Test
    @DisplayName("getDiseaseMedicineMapping-正常返回疾病-药品映射")
    void getDiseaseMedicineMapping_maps() {
        DiseaseDO disease = new DiseaseDO();
        disease.setId(10L);
        disease.setName("感冒");
        when(diseaseMapper.selectListByDiagnosisLike("感冒")).thenReturn(Collections.singletonList(disease));

        DiseaseMedicineDO relation = new DiseaseMedicineDO();
        relation.setDiseaseId(10L);
        relation.setMedicineId(7L);
        when(diseaseMedicineMapper.selectListByDiseaseId(10L)).thenReturn(Collections.singletonList(relation));

        MedicineDO medicine = new MedicineDO();
        medicine.setId(7L);
        medicine.setName("布洛芬");
        when(medicineMapper.selectListByMedicineIds(any())).thenReturn(Collections.singletonList(medicine));

        Map<String, List<MedicineDO>> result = knowledgeService.getDiseaseMedicineMapping("感冒");
        assertEquals(1, result.size());
        assertTrue(result.containsKey("感冒"));
        assertEquals(1, result.get("感冒").size());
        assertEquals("布洛芬", result.get("感冒").get(0).getName());
    }
}
