package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.ward.vo.WardPageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.WardDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import java.util.List;

/** 病区 Mapper */
@Mapper
public interface WardMapper extends BaseMapperX<WardDO> {

    /** 分页查询 */
    default PageResult<WardDO> selectPage(WardPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<WardDO>()
                .eqIfPresent(WardDO::getDeptId, reqVO.getDeptId())
                .likeIfPresent(WardDO::getWardNo, reqVO.getWardNo())
                .eqIfPresent(WardDO::getType, reqVO.getType())
                .eqIfPresent(WardDO::getStatus, reqVO.getStatus())
                .orderByDesc(WardDO::getId));
    }

    /** 按科室ID查询病区列表 */
    default List<WardDO> selectListByDeptId(Long deptId) {
        return selectList(WardDO::getDeptId, deptId);
    }

    /**
     * 原子递增已用床位数（SQL 级原子操作，消除 READ-MODIFY-WRITE 竞态条件）
     * 同时检查 used_beds < capacity，防止超额分配
     */
    @Update("UPDATE hospital_ward SET used_beds = used_beds + 1 WHERE id = #{wardId} AND used_beds < capacity")
    int incrementUsedBeds(@Param("wardId") Long wardId);

    /**
     * 原子递减已用床位数
     * 同时检查 used_beds > 0，防止负数
     */
    @Update("UPDATE hospital_ward SET used_beds = used_beds - 1 WHERE id = #{wardId} AND used_beds > 0")
    int decrementUsedBeds(@Param("wardId") Long wardId);

}
