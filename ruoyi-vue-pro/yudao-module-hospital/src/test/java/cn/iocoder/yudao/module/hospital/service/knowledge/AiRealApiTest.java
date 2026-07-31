package cn.iocoder.yudao.module.hospital.service.knowledge;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * AI 真实调用（DeepSeek）集成测试。
 *
 * <p>默认情况下本测试会被 {@link Assumptions} 跳过，不会消耗 API 额度、也不会发起真实网络请求。
 * 需要在本地手动开启：</p>
 * <pre>
 *   mvn test -DAI_REAL_API=true -Dtest=AiRealApiTest
 * </pre>
 * <p>或在 IDE 的 Run/Debug 配置里给 VM options 加上 {@code -DAI_REAL_API=true}。
 * API Key 解析优先级：系统属性 {@code spring.ai.deepseek.api-key} → 环境变量 {@code DEEPSEEK_API_KEY}
 * → 模块相对路径下的 {@code application-local.yaml}（项目已自带，演示后注销）。</p>
 *
 * <p>仅 DeepSeekClient 为真实实例（真正请求 api.deepseek.com），其余 mapper / 知识图谱服务均用 mock 隔离，
 * 因此无需本地数据库即可验证 AI 症状提取、辅助诊断与处方审核的真实返回。</p>
 */
@DisplayName("AI 真实调用 DeepSeek 集成测试（需 -DAI_REAL_API=true 才执行）")
class AiRealApiTest {

    private AiServiceImpl aiService;

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
        // 未显式开启真实调用则跳过，避免每次 mvn test 都消耗额度 / 联网
        boolean enabled = Boolean.parseBoolean(System.getProperty("AI_REAL_API", "false"))
                || "true".equalsIgnoreCase(System.getenv("AI_REAL_API"));
        Assumptions.assumeTrue(enabled,
                "跳过真实 AI 调用测试：未开启 AI_REAL_API（运行：mvn test -DAI_REAL_API=true -Dtest=AiRealApiTest）");

        String apiKey = resolveApiKey();
        Assumptions.assumeTrue(StrUtil.isNotBlank(apiKey),
                "跳过真实 AI 调用测试：未解析到 DeepSeek API Key（请配置 -Dspring.ai.deepseek.api-key=... 或环境变量 DEEPSEEK_API_KEY，或确保 application-local.yaml 含 api-key）");

        MockitoAnnotations.openMocks(this);

        // 构造真实 DeepSeekClient，注入 key 并放宽超时（真实网络首 token 可能 > 5s）
        DeepSeekClient realClient = new DeepSeekClient();
        ReflectionTestUtils.setField(realClient, "apiKey", apiKey);
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(60000);
        ReflectionTestUtils.setField(realClient, "restTemplate", new RestTemplate(factory));

        aiService = new AiServiceImpl();
        ReflectionTestUtils.setField(aiService, "deepSeekClient", realClient);
        // 其余协作者全部 mock，隔离数据库依赖
        ReflectionTestUtils.setField(aiService, "knowledgeGraphService", knowledgeGraphService);
        ReflectionTestUtils.setField(aiService, "prescriptionMapper", prescriptionMapper);
        ReflectionTestUtils.setField(aiService, "prescriptionItemMapper", prescriptionItemMapper);
        ReflectionTestUtils.setField(aiService, "visitMapper", visitMapper);
        ReflectionTestUtils.setField(aiService, "medicineMapper", medicineMapper);
        ReflectionTestUtils.setField(aiService, "departmentMapper", departmentMapper);
        ReflectionTestUtils.setField(aiService, "symptomMapper", symptomMapper);
        ReflectionTestUtils.setField(aiService, "securityContext", securityContext);

        // 以管理员身份绕过处方归属校验
        when(securityContext.isAdmin()).thenReturn(true);
        when(securityContext.getCurrentDoctorId()).thenReturn(null);
        // 离线症状提取兜底用（AI 提取成功则不会用到）
        when(symptomMapper.selectList()).thenReturn(Collections.emptyList());
    }

    /**
     * 解析 DeepSeek API Key：系统属性 → 环境变量 → application-local.yaml
     */
    private String resolveApiKey() {
        String key = System.getProperty("spring.ai.deepseek.api-key");
        if (StrUtil.isNotBlank(key)) {
            return key;
        }
        key = System.getenv("DEEPSEEK_API_KEY");
        if (StrUtil.isNotBlank(key)) {
            return key;
        }
        // 从 application-local.yaml 读取（项目自带，含真实 key）
        String[] candidates = {
                "../yudao-server/src/main/resources/application-local.yaml",
                "yudao-server/src/main/resources/application-local.yaml",
                "../../yudao-server/src/main/resources/application-local.yaml"
        };
        for (String candidate : candidates) {
            Path path = Paths.get(candidate);
            if (Files.exists(path)) {
                try {
                    for (String line : Files.readAllLines(path)) {
                        String trimmed = line.trim();
                        if (trimmed.startsWith("api-key:")) {
                            String value = trimmed.substring("api-key:".length()).trim();
                            value = StrUtil.strip(value, "\"", "\"");
                            value = StrUtil.strip(value, "'", "'");
                            return value;
                        }
                    }
                } catch (IOException ignored) {
                    // 读取失败则尝试下一个候选路径
                }
            }
        }
        return "";
    }

    @Test
    @DisplayName("assistDiagnosis-真实调用 DeepSeek API（症状提取 + 辅助诊断）")
    void assistDiagnosis_realApi() {
        // 知识图谱匹配隔离：不依赖数据库，直接返回固定匹配结果供 prompt 使用
        when(knowledgeGraphService.matchSymptoms(any())).thenReturn(List.of(1L, 2L));
        DiseaseMatchDTO match = new DiseaseMatchDTO();
        match.setId(10L);
        match.setName("急性上呼吸道感染");
        match.setIcdCode("J06.9");
        match.setMatchCount(2);
        match.setDeptId(1L);
        when(knowledgeGraphService.findDiseasesBySymptoms(any())).thenReturn(List.of(match));
        DepartmentDO dept = new DepartmentDO();
        dept.setId(1L);
        dept.setDeptName("呼吸内科");
        when(departmentMapper.selectById(1L)).thenReturn(dept);

        AssistDiagnosisRespVO resp = aiService.assistDiagnosis(
                "患者男，32岁，发热38.5℃伴咳嗽、咽痛两天，无呼吸困难，精神尚可");

        assertNotNull(resp);
        assertFalse(resp.getOffline(), "AI 调用应成功（offline=false）；若为 true 说明真实 API 未返回有效 JSON（网络/格式问题）");
        // 真实 AI 至少应给出建议或就诊科室
        assertTrue(StrUtil.isNotBlank(resp.getAdvice()) || StrUtil.isNotBlank(resp.getReferenceDept()),
                "AI 返回内容为空，请检查 prompt 与模型配置");
        System.out.println("[AI辅助诊断-真实返回] " + JsonUtils.toJsonString(resp));
    }

    @Test
    @DisplayName("prescriptionReview-真实调用 DeepSeek API（处方审核）")
    void prescriptionReview_realApi() {
        PrescriptionDO prescription = new PrescriptionDO();
        prescription.setId(1L);
        prescription.setVisitId(100L);
        prescription.setDoctorId(50L);
        prescription.setStatus(0);
        prescription.setCreateTime(LocalDateTime.now());
        when(prescriptionMapper.selectById(1L)).thenReturn(prescription);

        PrescriptionItemDO item1 = new PrescriptionItemDO();
        item1.setMedicineId(7L);
        item1.setQuantity(1);
        item1.setPrice(new BigDecimal("35.00"));
        item1.setInstructions("口服，每日三次，每次一片");
        PrescriptionItemDO item2 = new PrescriptionItemDO();
        item2.setMedicineId(8L);
        item2.setQuantity(1);
        item2.setPrice(new BigDecimal("22.50"));
        item2.setInstructions("口服，每日两次");
        when(prescriptionItemMapper.selectListByPrescriptionId(1L)).thenReturn(List.of(item1, item2));

        MedicineDO m7 = new MedicineDO();
        m7.setId(7L);
        m7.setName("阿莫西林胶囊");
        m7.setSpecification("0.25g*24粒");
        MedicineDO m8 = new MedicineDO();
        m8.setId(8L);
        m8.setName("复方甘草片");
        m8.setSpecification("100片");
        when(medicineMapper.selectListByMedicineIds(any())).thenReturn(List.of(m7, m8));

        VisitDO visit = new VisitDO();
        visit.setDiagnosis("急性支气管炎");
        when(visitMapper.selectById(100L)).thenReturn(visit);

        // 标准方案留空，让 AI 自行结合诊断与药品给出审核意见
        when(knowledgeGraphService.getDiseaseMedicineMapping("急性支气管炎")).thenReturn(Collections.emptyMap());

        ReviewResponseVO resp = aiService.prescriptionReview(1L);

        assertNotNull(resp);
        assertFalse(resp.getOffline(), "AI 处方审核应成功（offline=false）；若为 true 说明真实 API 未返回有效 JSON");
        assertTrue(List.of("LOW", "MEDIUM", "HIGH").contains(resp.getRiskLevel()),
                "riskLevel 应为 LOW/MEDIUM/HIGH，实际=" + resp.getRiskLevel());
        assertNotNull(resp.getPrescription());
        assertFalse(resp.getPrescription().getItems().isEmpty());
        System.out.println("[AI处方审核-真实返回] " + JsonUtils.toJsonString(resp));
    }
}
