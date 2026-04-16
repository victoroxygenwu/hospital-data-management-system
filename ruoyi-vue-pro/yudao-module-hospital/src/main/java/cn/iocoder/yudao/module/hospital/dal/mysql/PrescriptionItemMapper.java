package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionItemDO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PrescriptionItemMapper extends BaseMapperX<PrescriptionItemDO> {

    default List<PrescriptionItemDO> selectListByPrescriptionId(Long prescriptionId) {
        return selectList(PrescriptionItemDO::getPrescriptionId, prescriptionId);
    }

}
