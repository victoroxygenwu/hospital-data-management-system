package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.prescription.vo.PrescriptionPageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionDO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/** 处方 Mapper */
@Mapper
public interface PrescriptionMapper extends BaseMapperX<PrescriptionDO> {

    default PageResult<PrescriptionDO> selectPage(PrescriptionPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PrescriptionDO>()
                .eqIfPresent(PrescriptionDO::getVisitId, reqVO.getVisitId())
                .eqIfPresent(PrescriptionDO::getDoctorId, reqVO.getDoctorId())
                .eqIfPresent(PrescriptionDO::getStatus, reqVO.getStatus())
                .orderByDesc(PrescriptionDO::getId));
    }

    /** 患者维度过滤：按就诊ID列表查处方 */
    default PageResult<PrescriptionDO> selectPageByVisitIds(PrescriptionPageReqVO reqVO, List<Long> visitIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PrescriptionDO>()
                .in(PrescriptionDO::getVisitId, visitIds)
                .eqIfPresent(PrescriptionDO::getDoctorId, reqVO.getDoctorId())
                .eqIfPresent(PrescriptionDO::getStatus, reqVO.getStatus())
                .orderByDesc(PrescriptionDO::getId));
    }

    /** 发药 */
    default int dispense(Long id) {
        PrescriptionDO p = selectById(id);
        if (p == null || !Integer.valueOf(0).equals(p.getStatus())) return 0;
        p.setStatus(1);
        updateById(p);
        return 1;
    }
}
