package cn.iocoder.yudao.module.hospital.service.knowledge;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseaseSymptomPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseaseSymptomRespVO;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseaseSymptomSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DiseaseSymptomDO;

public interface DiseaseSymptomService {

    Long createDiseaseSymptom(DiseaseSymptomSaveReqVO createReqVO);

    void deleteDiseaseSymptom(Long id);

    PageResult<DiseaseSymptomRespVO> getDiseaseSymptomPage(DiseaseSymptomPageReqVO pageReqVO);

    DiseaseSymptomDO getDiseaseSymptom(Long id);

}
