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
        return selectList(new LambdaQueryWrapperX<BedDO>().eqIfPresent(BedDO::getWardId, wardId));
    }

    /** 释放床位，清除患者信息和入院时间（显式 SQL 保证 null 字段写入） */
    @Update("UPDATE hospital_bed SET status = '空闲', patient_id = NULL, admission_time = NULL WHERE id = #{id}")
    int releaseBed(@Param("id") Long id);

}
