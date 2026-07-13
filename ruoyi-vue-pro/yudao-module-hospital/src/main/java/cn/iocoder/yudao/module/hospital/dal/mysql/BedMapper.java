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

    /** 分配床位（原子 SQL，WHERE status='空闲' 防止并发重复分配） */
    @Update("UPDATE hospital_bed SET status = '已占用', patient_id = #{patientId}, admission_time = NOW() WHERE id = #{bedId} AND status = '空闲'")
    int assignBed(@Param("bedId") Long bedId, @Param("patientId") Long patientId);

    /** 释放床位（WHERE status='已占用' 防止并发重复释放），显式 SQL 保证 null 字段写入 */
    @Update("UPDATE hospital_bed SET status = '空闲', patient_id = NULL, admission_time = NULL WHERE id = #{id} AND status = '已占用'")
    int releaseBed(@Param("id") Long id);

}
