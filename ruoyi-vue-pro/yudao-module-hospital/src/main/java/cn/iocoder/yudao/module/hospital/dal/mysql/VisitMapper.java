package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.visit.vo.VisitPageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.VisitDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VisitMapper extends BaseMapperX<VisitDO> {

    default PageResult<VisitDO> selectPage(VisitPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<VisitDO>()
                .eqIfPresent(VisitDO::getPatientId, reqVO.getPatientId())
                .eqIfPresent(VisitDO::getDoctorId, reqVO.getDoctorId())
                .eqIfPresent(VisitDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(VisitDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(VisitDO::getVisitDate, reqVO.getVisitDate())
                .orderByDesc(VisitDO::getId));
    }

}
