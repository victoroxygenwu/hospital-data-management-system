package cn.iocoder.yudao.module.hospital.service.prescription;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.prescription.vo.PrescriptionPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.prescription.vo.PrescriptionSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.MedicineDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionItemDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.PrescriptionItemMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.PrescriptionMapper;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import cn.iocoder.yudao.module.hospital.service.medicine.MedicineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.*;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    @Resource
    private PrescriptionMapper prescriptionMapper;
    @Resource
    private PrescriptionItemMapper prescriptionItemMapper;
    @Resource
    private MedicineService medicineService;
    @Resource
    private HospitalSecurityContext securityContext;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPrescription(PrescriptionSaveReqVO createReqVO) {
        PrescriptionDO prescription = BeanUtils.toBean(createReqVO, PrescriptionDO.class);
        prescriptionMapper.insert(prescription);
        if (createReqVO.getItems() == null || createReqVO.getItems().isEmpty()) {
            return prescription.getId();
        }

        // 批量查询药品信息（避免 N+1）
        Set<Long> medicineIds = createReqVO.getItems().stream()
                .map(PrescriptionSaveReqVO.PrescriptionItemSaveVO::getMedicineId)
                .collect(Collectors.toSet());
        Map<Long, MedicineDO> medicineMap = getMedicineMap(medicineIds);

        // 批量插入处方明细
        List<PrescriptionItemDO> items = createReqVO.getItems().stream().map(itemVO -> {
            MedicineDO medicine = medicineMap.get(itemVO.getMedicineId());
            return PrescriptionItemDO.builder()
                    .prescriptionId(prescription.getId())
                    .medicineId(itemVO.getMedicineId())
                    .quantity(itemVO.getQuantity())
                    .price(medicine != null ? medicine.getPrice() : BigDecimal.ZERO)
                    .instructions(itemVO.getInstructions())
                    .build();
        }).collect(Collectors.toList());
        prescriptionItemMapper.insertBatch(items);

        return prescription.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePrescription(PrescriptionSaveReqVO updateReqVO) {
        validatePrescriptionExists(updateReqVO.getId());
        PrescriptionDO updateObj = BeanUtils.toBean(updateReqVO, PrescriptionDO.class);
        prescriptionMapper.updateById(updateObj);
        if (updateReqVO.getItems() == null) return;

        List<PrescriptionItemDO> oldItems = prescriptionItemMapper.selectListByPrescriptionId(updateReqVO.getId());
        Map<Long, PrescriptionItemDO> oldItemMap = oldItems.stream()
                .collect(Collectors.toMap(PrescriptionItemDO::getMedicineId, i -> i));
        Set<Long> newMedicineIds = updateReqVO.getItems().stream()
                .map(PrescriptionSaveReqVO.PrescriptionItemSaveVO::getMedicineId)
                .collect(Collectors.toSet());

        // 批量查询所有涉及的药品（避免 N+1）
        Map<Long, MedicineDO> medicineMap = getMedicineMap(newMedicineIds);

        // 1. 批量删除在新明细中不存在的旧项目
        List<Long> deleteIds = oldItems.stream()
                .filter(item -> !newMedicineIds.contains(item.getMedicineId()))
                .map(PrescriptionItemDO::getId)
                .collect(Collectors.toList());
        if (!deleteIds.isEmpty()) {
            prescriptionItemMapper.deleteBatchIds(deleteIds);
        }

        // 2. 更新现有项目 / 收集新项目
        List<PrescriptionItemDO> newItems = new ArrayList<>();
        for (PrescriptionSaveReqVO.PrescriptionItemSaveVO itemVO : updateReqVO.getItems()) {
            MedicineDO medicine = medicineMap.get(itemVO.getMedicineId());
            BigDecimal price = medicine != null ? medicine.getPrice() : BigDecimal.ZERO;

            if (oldItemMap.containsKey(itemVO.getMedicineId())) {
                // 仅更新可变字段；prescriptionId/medicineId 由 NOT_NULL 策略保护不覆盖
                PrescriptionItemDO existing = oldItemMap.get(itemVO.getMedicineId());
                prescriptionItemMapper.updateById(PrescriptionItemDO.builder()
                        .id(existing.getId())
                        .quantity(itemVO.getQuantity())
                        .price(price)
                        .instructions(itemVO.getInstructions())
                        .build());
            } else {
                newItems.add(PrescriptionItemDO.builder()
                        .prescriptionId(updateReqVO.getId())
                        .medicineId(itemVO.getMedicineId())
                        .quantity(itemVO.getQuantity())
                        .price(price)
                        .instructions(itemVO.getInstructions())
                        .build());
            }
        }
        if (!newItems.isEmpty()) {
            prescriptionItemMapper.insertBatch(newItems);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePrescription(Long id) {
        validatePrescriptionExists(id);
        // 级联删除处方明细，避免孤儿数据
        prescriptionItemMapper.delete(PrescriptionItemDO::getPrescriptionId, id);
        prescriptionMapper.deleteById(id);
    }

    @Override
    public PrescriptionDO getPrescription(Long id) {
        return prescriptionMapper.selectById(id);
    }

    @Override
    public PageResult<PrescriptionDO> getPrescriptionPage(PrescriptionPageReqVO pageReqVO) {
        if (!securityContext.isAdmin()) {
            Long doctorId = securityContext.getCurrentDoctorId();
            if (doctorId != null) {
                pageReqVO.setDoctorId(doctorId);
            }
        }
        return prescriptionMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dispensePrescription(Long id) {
        PrescriptionDO prescription = prescriptionMapper.selectById(id);
        if (prescription == null) throw exception(PRESCRIPTION_NOT_EXISTS);
        // 原子状态更新：WHERE status!='已发药' 防止并发重复发药
        int affected = prescriptionMapper.dispense(id);
        if (affected == 0) return; // 已发药，幂等返回
        List<PrescriptionItemDO> items = prescriptionItemMapper.selectListByPrescriptionId(id);
        for (PrescriptionItemDO item : items) {
            medicineService.decrementStock(item.getMedicineId(), item.getQuantity());
        }
    }

    @Override
    public List<PrescriptionItemDO> getPrescriptionItems(Long prescriptionId) {
        return prescriptionItemMapper.selectListByPrescriptionId(prescriptionId);
    }

    private Map<Long, MedicineDO> getMedicineMap(Set<Long> medicineIds) {
        return medicineService.getMedicineListByIds(medicineIds).stream()
                .collect(Collectors.toMap(MedicineDO::getId, m -> m));
    }

    private void validatePrescriptionExists(Long id) {
        if (id == null) return;
        if (prescriptionMapper.selectById(id) == null) throw exception(PRESCRIPTION_NOT_EXISTS);
    }
}
