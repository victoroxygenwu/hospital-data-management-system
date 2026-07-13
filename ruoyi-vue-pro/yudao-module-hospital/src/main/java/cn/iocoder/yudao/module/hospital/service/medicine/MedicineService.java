package cn.iocoder.yudao.module.hospital.service.medicine;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.hospital.controller.admin.medicine.vo.MedicinePageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.medicine.vo.MedicineSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.MedicineDO;

import java.util.Collection;
import java.util.List;

public interface MedicineService {
    Long createMedicine(MedicineSaveReqVO createReqVO);
    void updateMedicine(MedicineSaveReqVO updateReqVO);
    void deleteMedicine(Long id);
    MedicineDO getMedicine(Long id);
    PageResult<MedicineDO> getMedicinePage(MedicinePageReqVO pageReqVO);
    /** 扣减药品库存（原子操作，校验库存充足） */
    void decrementStock(Long medicineId, int quantity);
    /** 批量查询药品（避免 N+1 查询） */
    List<MedicineDO> getMedicineListByIds(Collection<Long> ids);
}
