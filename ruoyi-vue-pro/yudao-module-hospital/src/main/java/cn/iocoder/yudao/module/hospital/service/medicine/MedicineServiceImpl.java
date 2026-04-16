package cn.iocoder.yudao.module.hospital.service.medicine;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.medicine.vo.MedicinePageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.medicine.vo.MedicineSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.MedicineDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.MedicineMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.MEDICINE_NOT_EXISTS;

@Service
public class MedicineServiceImpl implements MedicineService {

    @Resource
    private MedicineMapper medicineMapper;

    @Override
    public Long createMedicine(MedicineSaveReqVO createReqVO) {
        MedicineDO medicine = BeanUtils.toBean(createReqVO, MedicineDO.class);
        medicineMapper.insert(medicine);
        return medicine.getId();
    }

    @Override
    public void updateMedicine(MedicineSaveReqVO updateReqVO) {
        validateMedicineExists(updateReqVO.getId());
        MedicineDO updateObj = BeanUtils.toBean(updateReqVO, MedicineDO.class);
        medicineMapper.updateById(updateObj);
    }

    @Override
    public void deleteMedicine(Long id) {
        validateMedicineExists(id);
        medicineMapper.deleteById(id);
    }

    @Override
    public MedicineDO getMedicine(Long id) {
        return medicineMapper.selectById(id);
    }

    @Override
    public PageResult<MedicineDO> getMedicinePage(MedicinePageReqVO pageReqVO) {
        return medicineMapper.selectPage(pageReqVO);
    }

    private void validateMedicineExists(Long id) {
        if (id == null) return;
        if (medicineMapper.selectById(id) == null) throw exception(MEDICINE_NOT_EXISTS);
    }
}
