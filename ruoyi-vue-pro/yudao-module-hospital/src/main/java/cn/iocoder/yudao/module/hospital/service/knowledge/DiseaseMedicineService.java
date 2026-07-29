package cn.iocoder.yudao.module.hospital.service.knowledge;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseaseMedicinePageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseaseMedicineRespVO;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseaseMedicineSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DiseaseMedicineDO;

public interface DiseaseMedicineService {

    Long createDiseaseMedicine(DiseaseMedicineSaveReqVO createReqVO);

    void updateDiseaseMedicine(DiseaseMedicineSaveReqVO updateReqVO);

    void deleteDiseaseMedicine(Long id);

    PageResult<DiseaseMedicineRespVO> getDiseaseMedicinePage(DiseaseMedicinePageReqVO pageReqVO);

    DiseaseMedicineDO getDiseaseMedicine(Long id);

}
