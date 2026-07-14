package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionItemDO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/** 处方明细 Mapper */
@Mapper
public interface PrescriptionItemMapper extends BaseMapperX<PrescriptionItemDO> {

    /** 按处方ID查询明细列表 */
    default List<PrescriptionItemDO> selectListByPrescriptionId(Long prescriptionId) {
        return selectList(PrescriptionItemDO::getPrescriptionId, prescriptionId);
    }

    /** 统计引用指定药品的处方明细数量（用于删除药品前的引用校验） */
    default long countByMedicineId(Long medicineId) {
        return selectCount(new LambdaQueryWrapperX<PrescriptionItemDO>()
                .eq(PrescriptionItemDO::getMedicineId, medicineId));
    }

}
