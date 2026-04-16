package cn.iocoder.yudao.module.hospital.service.prescription;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.prescription.vo.PrescriptionPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.prescription.vo.PrescriptionSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.MedicineDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionItemDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.MedicineMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.PrescriptionItemMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.PrescriptionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.*;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    @Resource
    private PrescriptionMapper prescriptionMapper;
    @Resource
    private PrescriptionItemMapper prescriptionItemMapper;
    @Resource
    private MedicineMapper medicineMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPrescription(PrescriptionSaveReqVO createReqVO) {
        PrescriptionDO prescription = BeanUtils.toBean(createReqVO, PrescriptionDO.class);
        prescriptionMapper.insert(prescription);
        // 保存处方明细
        if (createReqVO.getItems() != null) {
            for (PrescriptionSaveReqVO.PrescriptionItemSaveVO itemVO : createReqVO.getItems()) {
                MedicineDO medicine = medicineMapper.selectById(itemVO.getMedicineId());
                PrescriptionItemDO item = PrescriptionItemDO.builder()
                        .prescriptionId(prescription.getId())
                        .medicineId(itemVO.getMedicineId())
                        .quantity(itemVO.getQuantity())
                        .price(medicine != null ? medicine.getPrice() : java.math.BigDecimal.ZERO)
                        .instructions(itemVO.getInstructions())
                        .build();
                prescriptionItemMapper.insert(item);
            }
        }
        return prescription.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePrescription(PrescriptionSaveReqVO updateReqVO) {
        validatePrescriptionExists(updateReqVO.getId());
        PrescriptionDO updateObj = BeanUtils.toBean(updateReqVO, PrescriptionDO.class);
        prescriptionMapper.updateById(updateObj);
        // 更新处方明细：先删后增
        if (updateReqVO.getItems() != null) {
            List<PrescriptionItemDO> oldItems = prescriptionItemMapper.selectListByPrescriptionId(updateReqVO.getId());
            for (PrescriptionItemDO oldItem : oldItems) {
                prescriptionItemMapper.deleteById(oldItem.getId());
            }
            for (PrescriptionSaveReqVO.PrescriptionItemSaveVO itemVO : updateReqVO.getItems()) {
                MedicineDO medicine = medicineMapper.selectById(itemVO.getMedicineId());
                PrescriptionItemDO item = PrescriptionItemDO.builder()
                        .prescriptionId(updateReqVO.getId())
                        .medicineId(itemVO.getMedicineId())
                        .quantity(itemVO.getQuantity())
                        .price(medicine != null ? medicine.getPrice() : java.math.BigDecimal.ZERO)
                        .instructions(itemVO.getInstructions())
                        .build();
                prescriptionItemMapper.insert(item);
            }
        }
    }

    @Override
    public void deletePrescription(Long id) {
        validatePrescriptionExists(id);
        prescriptionMapper.deleteById(id);
    }

    @Override
    public PrescriptionDO getPrescription(Long id) {
        return prescriptionMapper.selectById(id);
    }

    @Override
    public PageResult<PrescriptionDO> getPrescriptionPage(PrescriptionPageReqVO pageReqVO) {
        return prescriptionMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dispensePrescription(Long id) {
        PrescriptionDO prescription = prescriptionMapper.selectById(id);
        if (prescription == null) throw exception(PRESCRIPTION_NOT_EXISTS);
        if ("已发药".equals(prescription.getStatus())) return;
        // 扣减药品库存
        List<PrescriptionItemDO> items = prescriptionItemMapper.selectListByPrescriptionId(id);
        for (PrescriptionItemDO item : items) {
            MedicineDO medicine = medicineMapper.selectById(item.getMedicineId());
            if (medicine == null) throw exception(MEDICINE_NOT_EXISTS);
            if (medicine.getStock() < item.getQuantity()) throw exception(MEDICINE_STOCK_NOT_ENOUGH);
            medicineMapper.updateById(MedicineDO.builder().id(medicine.getId())
                    .stock(medicine.getStock() - item.getQuantity()).build());
        }
        // 更新处方状态
        prescriptionMapper.updateById(PrescriptionDO.builder().id(id).status("已发药").build());
    }

    private void validatePrescriptionExists(Long id) {
        if (id == null) return;
        if (prescriptionMapper.selectById(id) == null) throw exception(PRESCRIPTION_NOT_EXISTS);
    }
}
