package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.medicine.vo.MedicinePageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.MedicineDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MedicineMapper extends BaseMapperX<MedicineDO> {

    default PageResult<MedicineDO> selectPage(MedicinePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MedicineDO>()
                .likeIfPresent(MedicineDO::getName, reqVO.getName())
                .likeIfPresent(MedicineDO::getManufacturer, reqVO.getManufacturer())
                .orderByDesc(MedicineDO::getId));
    }

}
