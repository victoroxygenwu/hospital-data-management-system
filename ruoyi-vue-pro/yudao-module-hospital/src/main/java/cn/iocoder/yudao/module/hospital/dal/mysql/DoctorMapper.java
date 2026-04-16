package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.doctor.vo.DoctorPageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DoctorDO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface DoctorMapper extends BaseMapperX<DoctorDO> {

    default PageResult<DoctorDO> selectPage(DoctorPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DoctorDO>()
                .likeIfPresent(DoctorDO::getName, reqVO.getName())
                .eqIfPresent(DoctorDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(DoctorDO::getTitle, reqVO.getTitle())
                .orderByDesc(DoctorDO::getId));
    }

    default List<DoctorDO> selectListByDeptId(Long deptId) {
        return selectList(new LambdaQueryWrapperX<DoctorDO>().eqIfPresent(DoctorDO::getDeptId, deptId));
    }

}
