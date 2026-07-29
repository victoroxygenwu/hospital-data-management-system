package cn.iocoder.yudao.module.hospital.service.knowledge;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseasePageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseaseSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DiseaseDO;

public interface DiseaseService {

    Long createDisease(DiseaseSaveReqVO createReqVO);

    void updateDisease(DiseaseSaveReqVO updateReqVO);

    void deleteDisease(Long id);

    DiseaseDO getDisease(Long id);

    PageResult<DiseaseDO> getDiseasePage(DiseasePageReqVO pageReqVO);

}
