package cn.iocoder.yudao.module.hospital.service.bill;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.bill.vo.BillPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.bill.vo.BillSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BillDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.BillMapper;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.*;

/**
 * 账单 Service 实现类
 */
@Service
public class BillServiceImpl implements BillService {

    @Resource
    private BillMapper billMapper; // 账单数据访问
    @Resource
    private HospitalSecurityContext securityContext; // 角色权限上下文

    /**
     * 创建账单
     * @param createReqVO 创建请求
     * @return 新账单ID
     */
    @Override
    public Long createBill(BillSaveReqVO createReqVO) {
        BillDO bill = BeanUtils.toBean(createReqVO, BillDO.class);
        billMapper.insert(bill);
        return bill.getId();
    }

    /**
     * 更新账单
     * @param updateReqVO 更新请求
     */
    @Override
    public void updateBill(BillSaveReqVO updateReqVO) {
        validateBillExists(updateReqVO.getId());
        BillDO updateObj = BeanUtils.toBean(updateReqVO, BillDO.class);
        billMapper.updateById(updateObj);
    }

    /**
     * 删除账单
     * @param id 账单ID
     */
    @Override
    public void deleteBill(Long id) {
        validateBillExists(id);
        billMapper.deleteById(id);
    }

    /**
     * 查询账单
     * @param id 账单ID
     * @return 账单信息
     */
    @Override
    public BillDO getBill(Long id) {
        return billMapper.selectById(id);
    }

    /**
     * 分页查询账单（角色数据隔离：患者只能查看自己的账单）
     * @param pageReqVO 分页请求
     * @return 账单分页结果
     */
    @Override
    public PageResult<BillDO> getBillPage(BillPageReqVO pageReqVO) {
        // 角色数据隔离：患者只看自己的账单
        if (!securityContext.isAdmin()) {
            Long patientId = securityContext.getCurrentPatientId();
            if (patientId != null) {
                pageReqVO.setPatientId(patientId);
            }
        }
        return billMapper.selectPage(pageReqVO);
    }

    /**
     * 支付账单（原子操作：WHERE status=0 防止重复付款）
     * @param id 账单ID
     * @param payMethod 支付方式
     */
    @Override
    public void payBill(Long id, String payMethod) {
        BillDO bill = billMapper.selectById(id);
        if (bill == null) throw exception(BILL_NOT_EXISTS);
        // 原子支付：WHERE status=0 防止重复付款，pay_amount 直接取 total_amount 列
        int affected = billMapper.payBill(id, payMethod);
        if (affected == 0) throw exception(BILL_ALREADY_PAID);
    }

    private void validateBillExists(Long id) {
        if (id == null) return;
        if (billMapper.selectById(id) == null) throw exception(BILL_NOT_EXISTS);
    }
}
