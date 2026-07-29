package cn.iocoder.yudao.module.hospital.service.knowledge;

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
import cn.iocoder.yudao.module.hospital.service.knowledge.dto.DiseaseMatchDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.PRESCRIPTION_NOT_EXISTS;

/**
 * AI 辅助诊断与处方审核 Service 实现类
 */
@Service
@Slf4j
public class AiServiceImpl implements AiService {

    private static final int MAX_JSON_RETRY = 3;

    private static final String EXTRACT_PROMPT =
            "你是医疗症状提取助手。从用户描述中提取症状实体，返回 JSON：{\"symptoms\":[\"症状1\",\"症状2\"]}。只返回 JSON。";

    private static final String DIAGNOSIS_PROMPT =
            "你是医疗辅助诊断助手。根据症状描述和知识图谱匹配结果给出诊断参考，返回 JSON：" +
            "{\"extractedSymptoms\":[],\"matchedDiseases\":[{\"name\":\"\",\"icdCode\":\"\",\"matchCount\":0,\"totalSymptoms\":0}]," +
            "\"referenceDept\":\"\",\"urgency\":\"\",\"advice\":\"\"}。只返回 JSON。";

    private static final String REVIEW_PROMPT =
            "你是处方审核助手。根据处方药品、诊断和标准用药方案给出审核意见，返回 JSON：" +
            "{\"riskLevel\":\"LOW|MEDIUM|HIGH\",\"interactions\":[{\"medicineA\":\"\",\"medicineB\":\"\",\"note\":\"\"}],\"advice\":\"\"}。只返回 JSON。";

    @Resource
    private DeepSeekClient deepSeekClient;
    @Resource
    private KnowledgeGraphService knowledgeGraphService;
    @Resource
    private PrescriptionMapper prescriptionMapper;
    @Resource
    private PrescriptionItemMapper prescriptionItemMapper;
    @Resource
    private VisitMapper visitMapper;
    @Resource
    private MedicineMapper medicineMapper;
    @Resource
    private DepartmentMapper departmentMapper;
    @Resource
    private SymptomMapper symptomMapper;

    @Override
    public AssistDiagnosisRespVO assistDiagnosis(String symptomDescription) {
        List<String> extractedSymptoms = extractSymptomsWithAi(symptomDescription);
        if (extractedSymptoms.isEmpty()) {
            extractedSymptoms = extractSymptomsOffline(symptomDescription);
        }

        List<Long> symptomIds = knowledgeGraphService.matchSymptoms(extractedSymptoms);
        List<DiseaseMatchDTO> matchedDiseases = knowledgeGraphService.findDiseasesBySymptoms(symptomIds);

        String ragPrompt = buildDiagnosisPrompt(symptomDescription, extractedSymptoms, matchedDiseases);
        AssistDiagnosisRespVO aiResult = callAiDiagnosis(ragPrompt);
        if (aiResult != null) {
            aiResult.setOffline(false);
            if (aiResult.getExtractedSymptoms() == null || aiResult.getExtractedSymptoms().isEmpty()) {
                aiResult.setExtractedSymptoms(extractedSymptoms);
            }
            return aiResult;
        }

        return buildOfflineDiagnosis(extractedSymptoms, matchedDiseases);
    }

    @Override
    public ReviewResponseVO prescriptionReview(Long prescriptionId) {
        PrescriptionDO prescription = prescriptionMapper.selectById(prescriptionId);
        if (prescription == null) {
            throw exception(PRESCRIPTION_NOT_EXISTS);
        }

        List<PrescriptionItemDO> items = prescriptionItemMapper.selectListByPrescriptionId(prescriptionId);
        Set<Long> medicineIds = items.stream().map(PrescriptionItemDO::getMedicineId).collect(Collectors.toSet());
        List<MedicineDO> medicines = medicineMapper.selectListByMedicineIds(medicineIds);

        String diagnosis = "";
        VisitDO visit = visitMapper.selectById(prescription.getVisitId());
        if (visit != null && StringUtils.hasText(visit.getDiagnosis())) {
            diagnosis = visit.getDiagnosis();
        }

        Map<String, List<MedicineDO>> standardMapping = knowledgeGraphService.getDiseaseMedicineMapping(diagnosis);
        String reviewPrompt = buildReviewPrompt(medicines, diagnosis, standardMapping);

        ReviewResponseVO aiResult = callAiReview(reviewPrompt);
        if (aiResult != null) {
            aiResult.setOffline(false);
            return aiResult;
        }

        return buildOfflineReview(medicines, standardMapping);
    }

    private List<String> extractSymptomsWithAi(String symptomDescription) {
        String json = chatWithRetry(EXTRACT_PROMPT, symptomDescription);
        if (!StringUtils.hasText(json)) {
            return Collections.emptyList();
        }
        try {
            Map<String, Object> map = JsonUtils.parseObject(json, Map.class);
            Object symptoms = map.get("symptoms");
            if (symptoms instanceof List) {
                return ((List<?>) symptoms).stream()
                        .map(String::valueOf)
                        .filter(StringUtils::hasText)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.warn("[assistDiagnosis] 解析 AI 症状提取结果失败: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    /**
     * 离线降级：从文本中匹配已知症状名称
     */
    private List<String> extractSymptomsOffline(String text) {
        if (!StringUtils.hasText(text)) {
            return Collections.emptyList();
        }
        List<SymptomDO> allSymptoms = symptomMapper.selectList();
        List<String> matched = new ArrayList<>();
        for (SymptomDO symptom : allSymptoms) {
            if (StringUtils.hasText(symptom.getName()) && text.contains(symptom.getName())) {
                matched.add(symptom.getName());
            }
        }
        return matched;
    }

    private String buildDiagnosisPrompt(String description, List<String> extracted,
                                        List<DiseaseMatchDTO> matchedDiseases) {
        StringBuilder sb = new StringBuilder();
        sb.append("症状描述：").append(description).append("\n");
        sb.append("提取症状：").append(extracted).append("\n");
        sb.append("知识图谱匹配疾病：").append(matchedDiseases).append("\n");
        sb.append("总输入症状数：").append(extracted.size());
        return sb.toString();
    }

    private AssistDiagnosisRespVO callAiDiagnosis(String ragPrompt) {
        String json = chatWithRetry(DIAGNOSIS_PROMPT, ragPrompt);
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return JsonUtils.parseObject(json, AssistDiagnosisRespVO.class);
        } catch (Exception e) {
            log.warn("[assistDiagnosis] 解析 AI 诊断结果失败: {}", e.getMessage());
            return null;
        }
    }

    private AssistDiagnosisRespVO buildOfflineDiagnosis(List<String> extractedSymptoms,
                                                        List<DiseaseMatchDTO> matchedDiseases) {
        AssistDiagnosisRespVO resp = new AssistDiagnosisRespVO();
        resp.setExtractedSymptoms(extractedSymptoms);
        resp.setOffline(true);
        resp.setUrgency("建议尽快就诊");
        resp.setAdvice("多休息，多饮水，监测体温；本结果为知识图谱离线匹配，仅供参考");

        int totalSymptoms = extractedSymptoms.size();
        List<AssistDiagnosisRespVO.MatchedDiseaseVO> diseaseVOs = new ArrayList<>();
        for (DiseaseMatchDTO dto : matchedDiseases) {
            AssistDiagnosisRespVO.MatchedDiseaseVO vo = new AssistDiagnosisRespVO.MatchedDiseaseVO();
            vo.setName(dto.getName());
            vo.setIcdCode(dto.getIcdCode());
            vo.setMatchCount(dto.getMatchCount());
            vo.setTotalSymptoms(totalSymptoms);
            diseaseVOs.add(vo);
        }
        resp.setMatchedDiseases(diseaseVOs);

        if (!matchedDiseases.isEmpty() && matchedDiseases.get(0).getDeptId() != null) {
            DepartmentDO dept = departmentMapper.selectById(matchedDiseases.get(0).getDeptId());
            if (dept != null) {
                resp.setReferenceDept(dept.getDeptName());
            }
        }
        return resp;
    }

    private String buildReviewPrompt(List<MedicineDO> medicines, String diagnosis,
                                     Map<String, List<MedicineDO>> standardMapping) {
        StringBuilder sb = new StringBuilder();
        sb.append("患者诊断：").append(diagnosis).append("\n");
        sb.append("处方药品：").append(medicines.stream().map(MedicineDO::getName).collect(Collectors.toList())).append("\n");
        sb.append("标准用药方案：").append(standardMapping);
        return sb.toString();
    }

    private ReviewResponseVO callAiReview(String reviewPrompt) {
        String json = chatWithRetry(REVIEW_PROMPT, reviewPrompt);
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return JsonUtils.parseObject(json, ReviewResponseVO.class);
        } catch (Exception e) {
            log.warn("[prescriptionReview] 解析 AI 审核结果失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 离线降级：对比处方药品与标准用药映射，评估风险等级
     */
    private ReviewResponseVO buildOfflineReview(List<MedicineDO> medicines,
                                                Map<String, List<MedicineDO>> standardMapping) {
        ReviewResponseVO resp = new ReviewResponseVO();
        resp.setOffline(true);

        Set<String> prescriptionNames = medicines.stream()
                .map(MedicineDO::getName)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());

        Set<String> standardNames = standardMapping.values().stream()
                .flatMap(List::stream)
                .map(MedicineDO::getName)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());

        long matchedCount = prescriptionNames.stream().filter(standardNames::contains).count();
        String riskLevel;
        if (standardNames.isEmpty()) {
            riskLevel = "MEDIUM";
            resp.setAdvice("未找到对应疾病的标准用药方案，请人工复核");
        } else if (matchedCount == prescriptionNames.size()) {
            riskLevel = "LOW";
            resp.setAdvice("处方药品与标准方案一致，建议按医嘱服用");
        } else if (matchedCount > 0) {
            riskLevel = "MEDIUM";
            resp.setAdvice("部分药品与标准方案不一致，请核对适应症与剂量");
        } else {
            riskLevel = "HIGH";
            resp.setAdvice("处方药品与标准方案差异较大，请重点审核");
        }
        resp.setRiskLevel(riskLevel);

        List<ReviewResponseVO.InteractionVO> interactions = new ArrayList<>();
        List<String> names = new ArrayList<>(prescriptionNames);
        for (int i = 0; i < names.size(); i++) {
            for (int j = i + 1; j < names.size(); j++) {
                ReviewResponseVO.InteractionVO interaction = new ReviewResponseVO.InteractionVO();
                interaction.setMedicineA(names.get(i));
                interaction.setMedicineB(names.get(j));
                boolean bothStandard = standardNames.contains(names.get(i)) && standardNames.contains(names.get(j));
                interaction.setNote(bothStandard ? "均在标准方案内，无明确相互作用" : "请核对两种药品联用安全性");
                interactions.add(interaction);
            }
        }
        if (interactions.isEmpty() && !names.isEmpty()) {
            ReviewResponseVO.InteractionVO single = new ReviewResponseVO.InteractionVO();
            single.setMedicineA(names.get(0));
            single.setMedicineB("-");
            single.setNote("单药处方，无药物相互作用");
            interactions.add(single);
        }
        resp.setInteractions(interactions);
        return resp;
    }

    private String chatWithRetry(String systemPrompt, String userMessage) {
        for (int i = 0; i < MAX_JSON_RETRY; i++) {
            String json = deepSeekClient.chat(systemPrompt, userMessage);
            if (StringUtils.hasText(json)) {
                return json;
            }
        }
        return null;
    }

}
