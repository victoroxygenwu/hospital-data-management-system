package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.bill.vo.BillPageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BillDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BillMapper extends BaseMapperX<BillDO> {

    default PageResult<BillDO> selectPage(BillPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BillDO>()
                .eqIfPresent(BillDO::getVisitId, reqVO.getVisitId())
                .eqIfPresent(BillDO::getPatientId, reqVO.getPatientId())
                .eqIfPresent(BillDO::getStatus, reqVO.getStatus())
                .orderByDesc(BillDO::getId));
    }

}
