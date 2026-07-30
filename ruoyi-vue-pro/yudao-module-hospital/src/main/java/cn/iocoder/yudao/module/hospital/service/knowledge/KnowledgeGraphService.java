package cn.iocoder.yudao.module.hospital.service.knowledge;

import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseaseNetworkRespVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.MedicineDO;
import cn.iocoder.yudao.module.hospital.service.knowledge.dto.DiseaseMatchDTO;

import java.util.List;
import java.util.Map;

public interface KnowledgeGraphService {

    DiseaseNetworkRespVO getDiseaseNetwork(Long diseaseId);

    /** 获取全量知识图谱（所有疾病/症状/药品节点与全部关联边），用于合成一张大图 */
    DiseaseNetworkRespVO getFullNetwork();

    List<DiseaseMatchDTO> findDiseasesBySymptoms(List<Long> symptomIds);

    List<Long> matchSymptoms(List<String> names);

    Map<String, List<MedicineDO>> getDiseaseMedicineMapping(String diagnosis);

}
