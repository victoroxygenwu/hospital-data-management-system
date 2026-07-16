package cn.iocoder.yudao.module.hospital.service.bill;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BillDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.BillMapper;
import cn.iocoder.yudao.module.hospital.enums.BillStatusEnum;
import cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("账单 Service 单元测试（覆盖支付幂等）")
class BillServiceImplTest {

    private BillServiceImpl billService;

    @Mock
    private BillMapper billMapper;
    @Mock
    private HospitalSecurityContext securityContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        billService = new BillServiceImpl();
        ReflectionTestUtils.setField(billService, "billMapper", billMapper);
        ReflectionTestUtils.setField(billService, "securityContext", securityContext);
    }

    @Test
    @DisplayName("支付账单-成功：条件更新影响行数=1")
    void payBill_success() {
        BillDO bill = new BillDO();
        bill.setId(1L);
        bill.setStatus(BillStatusEnum.UNPAID.getStatus());
        when(securityContext.isAdmin()).thenReturn(true); // 管理员支付，跳过越权校验，聚焦支付逻辑
        when(billMapper.selectById(1L)).thenReturn(bill);
        when(billMapper.payBill(1L, "微信")).thenReturn(1);

        billService.payBill(1L, "微信");
        // 成功则不抛异常
    }

    @Test
    @DisplayName("支付账单-重复支付被拒：条件更新影响行数=0，幂等")
    void payBill_idempotentAlreadyPaid_rejected() {
        BillDO bill = new BillDO();
        bill.setId(1L);
        bill.setStatus(BillStatusEnum.UNPAID.getStatus());
        when(securityContext.isAdmin()).thenReturn(true); // 管理员支付，跳过越权校验，聚焦幂等逻辑
        when(billMapper.selectById(1L)).thenReturn(bill);
        // 模拟已支付：WHERE status=0 条件不满足，影响行数=0
        when(billMapper.payBill(1L, "微信")).thenReturn(0);

        ServiceException ex = assertThrows(ServiceException.class, () -> billService.payBill(1L, "微信"));
        assertEquals(ErrorCodeConstants.BILL_ALREADY_PAID.getCode(), ex.getCode());
    }
}
