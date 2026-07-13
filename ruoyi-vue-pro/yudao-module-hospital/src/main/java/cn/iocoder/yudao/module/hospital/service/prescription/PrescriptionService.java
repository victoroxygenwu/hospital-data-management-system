package cn.iocoder.yudao.module.hospital.service.prescription;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.hospital.controller.admin.prescription.vo.PrescriptionPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.prescription.vo.PrescriptionSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionItemDO;

import java.util.List;

public interface PrescriptionService {
    Long createPrescription(PrescriptionSaveReqVO createReqVO);
    void updatePrescription(PrescriptionSaveReqVO updateReqVO);
    void deletePrescription(Long id);
    PrescriptionDO getPrescription(Long id);
    PageResult<PrescriptionDO> getPrescriptionPage(PrescriptionPageReqVO pageReqVO);
    /** 发药：更新处方状态为已发药，并扣减药品库存 */
    void dispensePrescription(Long id);
    /** 获取处方的所有明细项 */
    List<PrescriptionItemDO> getPrescriptionItems(Long prescriptionId);
}
