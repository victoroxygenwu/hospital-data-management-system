package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import cn.iocoder.yudao.module.hospital.controller.admin.prescription.vo.PrescriptionPageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionDO;
import cn.iocoder.yudao.module.hospital.enums.PrescriptionStatusEnum;
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

    /** 发药：真条件更新，仅当 status=0（未发药）时才置为 1，返回影响行数（0 表示已发药或不存在），避免并发重复发药 */
    default int dispense(Long id) {
        return update(null, new LambdaUpdateWrapper<PrescriptionDO>()
                .eq(PrescriptionDO::getId, id)
                .eq(PrescriptionDO::getStatus, PrescriptionStatusEnum.UNDISPENSED.getStatus())
                .set(PrescriptionDO::getStatus, PrescriptionStatusEnum.DISPENSED.getStatus()));
    }
}
