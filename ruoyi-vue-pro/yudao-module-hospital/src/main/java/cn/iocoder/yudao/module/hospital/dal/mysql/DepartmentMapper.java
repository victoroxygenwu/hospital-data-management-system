package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.department.vo.DepartmentPageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DepartmentDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DepartmentMapper extends BaseMapperX<DepartmentDO> {

    default PageResult<DepartmentDO> selectPage(DepartmentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DepartmentDO>()
                .likeIfPresent(DepartmentDO::getDeptName, reqVO.getDeptName())
                .likeIfPresent(DepartmentDO::getPhone, reqVO.getPhone())
                .orderByDesc(DepartmentDO::getId));
    }

}
