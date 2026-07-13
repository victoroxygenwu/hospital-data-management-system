package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.medicine.vo.MedicinePageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.MedicineDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;
import java.util.List;

@Mapper
public interface MedicineMapper extends BaseMapperX<MedicineDO> {

    default PageResult<MedicineDO> selectPage(MedicinePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MedicineDO>()
                .likeIfPresent(MedicineDO::getName, reqVO.getName())
                .likeIfPresent(MedicineDO::getManufacturer, reqVO.getManufacturer())
                .orderByDesc(MedicineDO::getId));
    }

    /** 批量按 ID 查询药品（避免 N+1） */
    default List<MedicineDO> selectListByMedicineIds(Collection<Long> ids) {
        return selectList(MedicineDO::getId, ids);
    }

    /**
     * 原子扣减库存（SQL 级原子操作，消除 READ-MODIFY-WRITE 竞态条件）
     * 同时检查 stock >= quantity，防止超卖
     */
    @Update("UPDATE hospital_medicine SET stock = stock - #{quantity} WHERE id = #{id} AND stock >= #{quantity}")
    int decrementStock(@Param("id") Long id, @Param("quantity") int quantity);

}
