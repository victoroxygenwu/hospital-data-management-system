package cn.iocoder.yudao.module.hospital.service.medicine;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.hospital.controller.admin.medicine.vo.MedicinePageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.medicine.vo.MedicineSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.MedicineDO;

public interface MedicineService {
    Long createMedicine(MedicineSaveReqVO createReqVO);
    void updateMedicine(MedicineSaveReqVO updateReqVO);
    void deleteMedicine(Long id);
    MedicineDO getMedicine(Long id);
    PageResult<MedicineDO> getMedicinePage(MedicinePageReqVO pageReqVO);
}
