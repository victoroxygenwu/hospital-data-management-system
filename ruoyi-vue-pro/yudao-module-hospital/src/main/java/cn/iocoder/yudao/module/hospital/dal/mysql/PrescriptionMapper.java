package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.prescription.vo.PrescriptionPageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PrescriptionMapper extends BaseMapperX<PrescriptionDO> {

    default PageResult<PrescriptionDO> selectPage(PrescriptionPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PrescriptionDO>()
                .eqIfPresent(PrescriptionDO::getVisitId, reqVO.getVisitId())
                .eqIfPresent(PrescriptionDO::getDoctorId, reqVO.getDoctorId())
                .eqIfPresent(PrescriptionDO::getStatus, reqVO.getStatus())
                .orderByDesc(PrescriptionDO::getId));
    }

}
