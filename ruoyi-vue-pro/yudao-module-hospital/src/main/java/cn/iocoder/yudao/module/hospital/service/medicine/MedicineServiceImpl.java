package cn.iocoder.yudao.module.hospital.service.medicine;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.medicine.vo.MedicinePageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.medicine.vo.MedicineSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.MedicineDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.MedicineMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.PrescriptionItemMapper;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.MEDICINE_NOT_EXISTS;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.MEDICINE_HAS_PRESCRIPTION;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.MEDICINE_STOCK_NOT_ENOUGH;

/**
 * 药品 Service 实现类
 */
@Service
public class MedicineServiceImpl implements MedicineService {

    @Resource
    private MedicineMapper medicineMapper; // 药品数据访问
    @Resource
    private PrescriptionItemMapper prescriptionItemMapper; // 处方明细数据访问
    @Resource
    private HospitalSecurityContext securityContext; // 角色权限上下文

    /**
     * 创建药品
     * @param createReqVO 创建请求
     * @return 新药品ID
     */
    @Override
    public Long createMedicine(MedicineSaveReqVO createReqVO) {
        securityContext.requireAdmin();
        MedicineDO medicine = BeanUtils.toBean(createReqVO, MedicineDO.class);
        medicineMapper.insert(medicine);
        return medicine.getId();
    }

    /**
     * 更新药品
     * @param updateReqVO 更新请求
     */
    @Override
    public void updateMedicine(MedicineSaveReqVO updateReqVO) {
        securityContext.requireAdmin();
        validateMedicineExists(updateReqVO.getId());
        MedicineDO updateObj = BeanUtils.toBean(updateReqVO, MedicineDO.class);
        medicineMapper.updateById(updateObj);
    }

    /**
     * 删除药品
     * @param id 药品ID
     */
    @Override
    public void deleteMedicine(Long id) {
        securityContext.requireAdmin();
        validateMedicineExists(id);
        // 删除前校验：若存在引用该药品的处方明细，禁止删除，避免产生孤儿处方明细
        if (prescriptionItemMapper.countByMedicineId(id) > 0) {
            throw exception(MEDICINE_HAS_PRESCRIPTION);
        }
        medicineMapper.deleteById(id);
    }

    /**
     * 查询药品
     * @param id 药品ID
     * @return 药品信息
     */
    @Override
    public MedicineDO getMedicine(Long id) {
        return medicineMapper.selectById(id);
    }

    /**
     * 分页查询药品
     * @param pageReqVO 分页请求
     * @return 药品分页结果
     */
    @Override
    public PageResult<MedicineDO> getMedicinePage(MedicinePageReqVO pageReqVO) {
        return medicineMapper.selectPage(pageReqVO);
    }

    /**
     * 扣减库存（原子操作：WHERE stock>=quantity 防止超卖）
     * @param medicineId 药品ID
     * @param quantity 扣减数量
     */
    @Override
    public void decrementStock(Long medicineId, int quantity) {
        int affected = medicineMapper.decrementStock(medicineId, quantity);
        if (affected == 0) {
            throw exception(MEDICINE_STOCK_NOT_ENOUGH);
        }
    }

    /**
     * 批量查询药品
     * @param ids 药品ID集合
     * @return 药品列表
     */
    @Override
    public List<MedicineDO> getMedicineListByIds(Collection<Long> ids) {
        return medicineMapper.selectListByMedicineIds(ids);
    }

    private void validateMedicineExists(Long id) {
        if (id == null) return;
        if (medicineMapper.selectById(id) == null) throw exception(MEDICINE_NOT_EXISTS);
    }
}
