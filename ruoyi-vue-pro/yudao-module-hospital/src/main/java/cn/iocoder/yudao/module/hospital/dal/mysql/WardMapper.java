package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.ward.vo.WardPageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.WardDO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface WardMapper extends BaseMapperX<WardDO> {

    default PageResult<WardDO> selectPage(WardPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<WardDO>()
                .eqIfPresent(WardDO::getDeptId, reqVO.getDeptId())
                .likeIfPresent(WardDO::getWardNo, reqVO.getWardNo())
                .eqIfPresent(WardDO::getType, reqVO.getType())
                .eqIfPresent(WardDO::getStatus, reqVO.getStatus())
                .orderByDesc(WardDO::getId));
    }

    default List<WardDO> selectListByDeptId(Long deptId) {
        return selectList(new LambdaQueryWrapperX<WardDO>().eqIfPresent(WardDO::getDeptId, deptId));
    }

}
