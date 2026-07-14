package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.prescription.vo.PrescriptionPageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import java.util.List;

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

    /** 发药（原子 SQL，WHERE status=0 待发药 防止重复发药扣库存） */
    @Update("UPDATE hospital_prescription SET status = 1 WHERE id = #{id} AND status = 0")
    int dispense(@Param("id") Long id);

}
