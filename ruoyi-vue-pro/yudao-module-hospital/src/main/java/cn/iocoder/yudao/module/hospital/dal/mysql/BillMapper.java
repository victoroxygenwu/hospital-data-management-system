package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.bill.vo.BillPageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BillDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface BillMapper extends BaseMapperX<BillDO> {

    default PageResult<BillDO> selectPage(BillPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BillDO>()
                .eqIfPresent(BillDO::getVisitId, reqVO.getVisitId())
                .eqIfPresent(BillDO::getPatientId, reqVO.getPatientId())
                .eqIfPresent(BillDO::getStatus, reqVO.getStatus())
                .orderByDesc(BillDO::getId));
    }

    /** 支付账单（原子 SQL，WHERE status!='已支付' 防止重复付款；pay_amount 直接引用 total_amount 列） */
    @Update("UPDATE hospital_bill SET status = '已支付', pay_amount = total_amount, pay_time = NOW(), pay_method = #{payMethod} WHERE id = #{id} AND status != '已支付'")
    int payBill(@Param("id") Long id, @Param("payMethod") String payMethod);

}
