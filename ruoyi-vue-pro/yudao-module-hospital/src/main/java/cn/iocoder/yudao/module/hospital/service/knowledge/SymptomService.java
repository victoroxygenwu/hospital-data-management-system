package cn.iocoder.yudao.module.hospital.service.knowledge;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.SymptomPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.SymptomSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.SymptomDO;

public interface SymptomService {

    Long createSymptom(SymptomSaveReqVO createReqVO);

    void updateSymptom(SymptomSaveReqVO updateReqVO);

    void deleteSymptom(Long id);

    SymptomDO getSymptom(Long id);

    PageResult<SymptomDO> getSymptomPage(SymptomPageReqVO pageReqVO);

}
