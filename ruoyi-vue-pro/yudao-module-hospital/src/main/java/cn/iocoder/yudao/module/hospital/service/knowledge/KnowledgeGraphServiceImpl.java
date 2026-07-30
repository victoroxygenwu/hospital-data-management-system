package cn.iocoder.yudao.module.hospital.service.knowledge;

import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseaseNetworkRespVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DiseaseDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DiseaseMedicineDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DiseaseSymptomDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.MedicineDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.SymptomDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.DiseaseMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.DiseaseMedicineMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.DiseaseSymptomMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.MedicineMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.SymptomMapper;
import cn.iocoder.yudao.module.hospital.service.knowledge.dto.DiseaseMatchDTO;
import cn.iocoder.yudao.module.hospital.service.knowledge.dto.NetworkEdgeDTO;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.DISEASE_NOT_EXISTS;

/**
 * 知识图谱 Service 实现类
 */
@Service
public class KnowledgeGraphServiceImpl implements KnowledgeGraphService {

    private static final Map<Integer, String> STRENGTH_LABELS;
    private static final Map<Integer, String> USAGE_TYPE_LABELS;

    static {
        Map<Integer, String> strengthLabels = new HashMap<>();
        strengthLabels.put(1, "主要症状");
        strengthLabels.put(2, "次要");
        strengthLabels.put(3, "偶见");
        STRENGTH_LABELS = Collections.unmodifiableMap(strengthLabels);

        Map<Integer, String> usageTypeLabels = new HashMap<>();
        usageTypeLabels.put(1, "首选");
        usageTypeLabels.put(2, "备选");
        usageTypeLabels.put(3, "辅助");
        USAGE_TYPE_LABELS = Collections.unmodifiableMap(usageTypeLabels);
    }

    /** 症状节点 ID 编码，避免与疾病/药品 ID 冲突 */
    private static long encodeSymptomNodeId(Long symptomId) {
        return -symptomId;
    }

    /** 药品节点 ID 编码，避免与疾病/症状 ID 冲突 */
    private static long encodeMedicineNodeId(Long medicineId) {
        return 1_000_000L + medicineId;
    }

    @Resource
    private DiseaseMapper diseaseMapper;
    @Resource
    private SymptomMapper symptomMapper;
    @Resource
    private DiseaseSymptomMapper diseaseSymptomMapper;
    @Resource
    private DiseaseMedicineMapper diseaseMedicineMapper;
    @Resource
    private MedicineMapper medicineMapper;

    @Override
    public DiseaseNetworkRespVO getDiseaseNetwork(Long diseaseId) {
        DiseaseDO disease = diseaseMapper.selectById(diseaseId);
        if (disease == null) {
            throw exception(DISEASE_NOT_EXISTS);
        }
        DiseaseNetworkRespVO resp = new DiseaseNetworkRespVO();
        List<DiseaseNetworkRespVO.Node> nodes = new ArrayList<>();
        List<DiseaseNetworkRespVO.Link> links = new ArrayList<>();

        DiseaseNetworkRespVO.Node diseaseNode = new DiseaseNetworkRespVO.Node();
        diseaseNode.setId(disease.getId());
        diseaseNode.setName(disease.getName());
        diseaseNode.setCategory(0);
        nodes.add(diseaseNode);

        List<NetworkEdgeDTO> symptomEdges = diseaseSymptomMapper.selectSymptomEdgesByDiseaseId(diseaseId);
        for (NetworkEdgeDTO edge : symptomEdges) {
            long symptomNodeId = encodeSymptomNodeId(edge.getId());
            DiseaseNetworkRespVO.Node node = new DiseaseNetworkRespVO.Node();
            node.setId(symptomNodeId);
            node.setName(edge.getName());
            node.setCategory(1);
            nodes.add(node);

            DiseaseNetworkRespVO.Link link = new DiseaseNetworkRespVO.Link();
            link.setSource(diseaseId);
            link.setTarget(symptomNodeId);
            link.setLabel(STRENGTH_LABELS.getOrDefault(edge.getEdgeValue(), "关联"));
            links.add(link);
        }

        List<NetworkEdgeDTO> medicineEdges = diseaseMedicineMapper.selectMedicineEdgesByDiseaseId(diseaseId);
        for (NetworkEdgeDTO edge : medicineEdges) {
            long medicineNodeId = encodeMedicineNodeId(edge.getId());
            DiseaseNetworkRespVO.Node node = new DiseaseNetworkRespVO.Node();
            node.setId(medicineNodeId);
            node.setName(edge.getName());
            node.setCategory(2);
            nodes.add(node);

            DiseaseNetworkRespVO.Link link = new DiseaseNetworkRespVO.Link();
            link.setSource(diseaseId);
            link.setTarget(medicineNodeId);
            link.setLabel(USAGE_TYPE_LABELS.getOrDefault(edge.getEdgeValue(), "关联"));
            links.add(link);
        }

        resp.setNodes(nodes);
        resp.setLinks(links);
        return resp;
    }

    @Override
    public DiseaseNetworkRespVO getFullNetwork() {
        DiseaseNetworkRespVO resp = new DiseaseNetworkRespVO();
        List<DiseaseNetworkRespVO.Node> nodes = new ArrayList<>();
        List<DiseaseNetworkRespVO.Link> links = new ArrayList<>();

        // 疾病节点
        List<DiseaseDO> diseases = diseaseMapper.selectList(new LambdaQueryWrapperX<>());
        // 症状节点
        List<SymptomDO> symptoms = symptomMapper.selectList(new LambdaQueryWrapperX<>());
        // 药品节点：仅图谱中实际出现者
        List<DiseaseMedicineDO> dmAll = diseaseMedicineMapper.selectList(new LambdaQueryWrapperX<>());
        Set<Long> medIds = dmAll.stream().map(DiseaseMedicineDO::getMedicineId).collect(Collectors.toSet());
        List<MedicineDO> medicines = medIds.isEmpty()
                ? Collections.emptyList()
                : medicineMapper.selectListByMedicineIds(medIds);

        for (DiseaseDO d : diseases) {
            DiseaseNetworkRespVO.Node n = new DiseaseNetworkRespVO.Node();
            n.setId(d.getId());
            n.setName(d.getName());
            n.setCategory(0);
            nodes.add(n);
        }
        Map<Long, Long> symptomNodeId = new HashMap<>();
        for (SymptomDO s : symptoms) {
            long sid = encodeSymptomNodeId(s.getId());
            DiseaseNetworkRespVO.Node n = new DiseaseNetworkRespVO.Node();
            n.setId(sid);
            n.setName(s.getName());
            n.setCategory(1);
            nodes.add(n);
            symptomNodeId.put(s.getId(), sid);
        }
        Map<Long, Long> medicineNodeId = new HashMap<>();
        for (MedicineDO m : medicines) {
            long mid = encodeMedicineNodeId(m.getId());
            DiseaseNetworkRespVO.Node n = new DiseaseNetworkRespVO.Node();
            n.setId(mid);
            n.setName(m.getName());
            n.setCategory(2);
            nodes.add(n);
            medicineNodeId.put(m.getId(), mid);
        }

        // 边：疾病-症状
        List<DiseaseSymptomDO> dsAll = diseaseSymptomMapper.selectList(new LambdaQueryWrapperX<>());
        for (DiseaseSymptomDO e : dsAll) {
            Long target = symptomNodeId.get(e.getSymptomId());
            if (target == null) {
                continue;
            }
            DiseaseNetworkRespVO.Link l = new DiseaseNetworkRespVO.Link();
            l.setSource(e.getDiseaseId());
            l.setTarget(target);
            l.setLabel(STRENGTH_LABELS.getOrDefault(e.getStrength(), "关联"));
            links.add(l);
        }
        // 边：疾病-药品
        for (DiseaseMedicineDO e : dmAll) {
            Long target = medicineNodeId.get(e.getMedicineId());
            if (target == null) {
                continue;
            }
            DiseaseNetworkRespVO.Link l = new DiseaseNetworkRespVO.Link();
            l.setSource(e.getDiseaseId());
            l.setTarget(target);
            l.setLabel(USAGE_TYPE_LABELS.getOrDefault(e.getUsageType(), "关联"));
            links.add(l);
        }

        resp.setNodes(nodes);
        resp.setLinks(links);
        return resp;
    }

    @Override
    public List<DiseaseMatchDTO> findDiseasesBySymptoms(List<Long> symptomIds) {
        if (symptomIds == null || symptomIds.isEmpty()) {
            return Collections.emptyList();
        }
        return diseaseSymptomMapper.selectDiseasesBySymptomIds(symptomIds);
    }

    @Override
    public List<Long> matchSymptoms(List<String> names) {
        if (names == null || names.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> matchedIds = new LinkedHashSet<>();
        for (String name : names) {
            if (!StringUtils.hasText(name)) {
                continue;
            }
            List<SymptomDO> symptoms = symptomMapper.selectListByNameLike(name.trim());
            for (SymptomDO symptom : symptoms) {
                matchedIds.add(symptom.getId());
            }
        }
        return new ArrayList<>(matchedIds);
    }

    @Override
    public Map<String, List<MedicineDO>> getDiseaseMedicineMapping(String diagnosis) {
        if (!StringUtils.hasText(diagnosis)) {
            return Collections.emptyMap();
        }
        List<DiseaseDO> diseases = diseaseMapper.selectListByDiagnosisLike(diagnosis.trim());
        if (diseases.isEmpty()) {
            diseases = diseaseMapper.selectList(new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<DiseaseDO>()
                    .like(DiseaseDO::getName, diagnosis.trim()));
        }
        Map<String, List<MedicineDO>> result = new LinkedHashMap<>();
        for (DiseaseDO disease : diseases) {
            List<DiseaseMedicineDO> relations = diseaseMedicineMapper.selectListByDiseaseId(disease.getId());
            if (relations.isEmpty()) {
                continue;
            }
            Set<Long> medicineIds = relations.stream()
                    .map(DiseaseMedicineDO::getMedicineId)
                    .collect(Collectors.toSet());
            List<MedicineDO> medicines = medicineMapper.selectListByMedicineIds(medicineIds);
            result.put(disease.getName(), medicines);
        }
        return result;
    }

}
