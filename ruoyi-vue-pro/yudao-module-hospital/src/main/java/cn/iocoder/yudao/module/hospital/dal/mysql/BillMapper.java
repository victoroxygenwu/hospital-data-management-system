package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import cn.iocoder.yudao.module.hospital.controller.admin.bill.vo.BillPageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BillDO;
import cn.iocoder.yudao.module.hospital.enums.BillStatusEnum;
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

    /** 支付账单：真条件更新，仅当 status=0（未支付）时才置为 1 并将 pay_amount 置为 total_amount，返回影响行数（0 表示已支付或不存在），避免并发重复付款 */
    default int payBill(Long id, String payMethod) {
        return update(null, new LambdaUpdateWrapper<BillDO>()
                .eq(BillDO::getId, id)
                .eq(BillDO::getStatus, BillStatusEnum.UNPAID.getStatus())
                .set(BillDO::getStatus, BillStatusEnum.PAID.getStatus())
                .set(BillDO::getPayMethod, payMethod)
                .set(BillDO::getPayTime, LocalDateTime.now())
                .setSql("pay_amount = total_amount"));
    }
}
