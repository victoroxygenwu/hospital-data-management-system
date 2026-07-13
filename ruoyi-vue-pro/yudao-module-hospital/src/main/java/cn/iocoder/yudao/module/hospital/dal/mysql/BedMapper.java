package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.bed.vo.BedPageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BedDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import java.util.List;

@Mapper
public interface BedMapper extends BaseMapperX<BedDO> {

    default PageResult<BedDO> selectPage(BedPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BedDO>()
                .eqIfPresent(BedDO::getWardId, reqVO.getWardId())
                .likeIfPresent(BedDO::getBedNo, reqVO.getBedNo())
                .eqIfPresent(BedDO::getStatus, reqVO.getStatus())
                .orderByDesc(BedDO::getId));
    }

    default List<BedDO> selectListByWardId(Long wardId) {
        return selectList(BedDO::getWardId, wardId);
    }

    /** 分配床位（原子 SQL，WHERE status=0 防止并发重复分配） */
    @Update("UPDATE hospital_bed SET status = 1, patient_id = #{patientId}, admission_time = NOW() WHERE id = #{bedId} AND status = 0")
    int assignBed(@Param("bedId") Long bedId, @Param("patientId") Long patientId);

    /** 释放床位（WHERE status=1 防止并发重复释放） */
    @Update("UPDATE hospital_bed SET status = 0, patient_id = NULL, admission_time = NULL WHERE id = #{id} AND status = 1")
    int releaseBed(@Param("id") Long id);

}
