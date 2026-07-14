package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.bill.vo.BillPageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BillDO;
import org.apache.ibatis.annotations.Mapper;
import java.time.LocalDateTime;

/** 账单 Mapper */
@Mapper
public interface BillMapper extends BaseMapperX<BillDO> {

    default PageResult<BillDO> selectPage(BillPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BillDO>()
                .eqIfPresent(BillDO::getVisitId, reqVO.getVisitId())
                .eqIfPresent(BillDO::getPatientId, reqVO.getPatientId())
                .eqIfPresent(BillDO::getStatus, reqVO.getStatus())
                .orderByDesc(BillDO::getId));
    }

    /** 支付账单 */
    default int payBill(Long id, String payMethod) {
        BillDO bill = selectById(id);
        if (bill == null || !Integer.valueOf(0).equals(bill.getStatus())) return 0;
        bill.setStatus(1);
        bill.setPayAmount(bill.getTotalAmount());
        bill.setPayMethod(payMethod);
        bill.setPayTime(LocalDateTime.now());
        updateById(bill);
        return 1;
    }
}
