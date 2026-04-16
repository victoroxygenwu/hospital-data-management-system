package cn.iocoder.yudao.module.hospital.service.bill;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.hospital.controller.admin.bill.vo.BillPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.bill.vo.BillSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BillDO;

public interface BillService {
    Long createBill(BillSaveReqVO createReqVO);
    void updateBill(BillSaveReqVO updateReqVO);
    void deleteBill(Long id);
    BillDO getBill(Long id);
    PageResult<BillDO> getBillPage(BillPageReqVO pageReqVO);
    /** 支付账单 */
    void payBill(Long id, String payMethod);
}
