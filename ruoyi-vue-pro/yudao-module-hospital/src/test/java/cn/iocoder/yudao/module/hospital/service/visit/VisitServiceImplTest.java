package cn.iocoder.yudao.module.hospital.service.visit;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.hospital.controller.admin.visit.vo.VisitSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BillDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionItemDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.VisitDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.BillMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.PrescriptionItemMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.PrescriptionMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.VisitMapper;
import cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants;
import cn.iocoder.yudao.module.hospital.enums.VisitStatusEnum;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("就诊 Service 单元测试（覆盖终态守卫与默认状态）")
class VisitServiceImplTest {

    private VisitServiceImpl visitService;

    @Mock
    private VisitMapper visitMapper;
    @Mock
    private PrescriptionMapper prescriptionMapper;
    @Mock
    private PrescriptionItemMapper prescriptionItemMapper;
    @Mock
    private BillMapper billMapper;
    @Mock
    private HospitalSecurityContext securityContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        visitService = new VisitServiceImpl();
        ReflectionTestUtils.setField(visitService, "visitMapper", visitMapper);
        ReflectionTestUtils.setField(visitService, "prescriptionMapper", prescriptionMapper);
        ReflectionTestUtils.setField(visitService, "prescriptionItemMapper", prescriptionItemMapper);
        ReflectionTestUtils.setField(visitService, "billMapper", billMapper);
        ReflectionTestUtils.setField(visitService, "securityContext", securityContext);
    }

    @Test
    @DisplayName("更新就诊-已完成(终态)被拒：2->1 抛 VISIT_STATUS_ILLEGAL")
    void updateVisit_terminalCompleted_blocked() {
        VisitDO existing = new VisitDO();
        existing.setId(1L);
        existing.setStatus(VisitStatusEnum.COMPLETED.getStatus());
        when(visitMapper.selectById(1L)).thenReturn(existing);

        VisitSaveReqVO req = new VisitSaveReqVO();
        req.setId(1L);
        req.setStatus(1);

        ServiceException ex = assertThrows(ServiceException.class, () -> visitService.updateVisit(req));
        assertEquals(ErrorCodeConstants.VISIT_STATUS_ILLEGAL.getCode(), ex.getCode());
        verify(visitMapper, never()).updateById(any(VisitDO.class));
    }

    @Test
    @DisplayName("更新就诊-已取消(终态)被拒：3->1 抛 VISIT_STATUS_ILLEGAL")
    void updateVisit_terminalCancelled_blocked() {
        VisitDO existing = new VisitDO();
        existing.setId(1L);
        existing.setStatus(VisitStatusEnum.CANCELLED.getStatus());
        when(visitMapper.selectById(1L)).thenReturn(existing);

        VisitSaveReqVO req = new VisitSaveReqVO();
        req.setId(1L);
        req.setStatus(1);

        ServiceException ex = assertThrows(ServiceException.class, () -> visitService.updateVisit(req));
        assertEquals(ErrorCodeConstants.VISIT_STATUS_ILLEGAL.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("更新就诊-非终态允许转换：0->1 正常更新")
    void updateVisit_nonTerminal_allowed() {
        VisitDO existing = new VisitDO();
        existing.setId(1L);
        existing.setStatus(VisitStatusEnum.PENDING.getStatus());
        when(visitMapper.selectById(1L)).thenReturn(existing);

        VisitSaveReqVO req = new VisitSaveReqVO();
        req.setId(1L);
        req.setStatus(1);

        visitService.updateVisit(req);
        verify(visitMapper).updateById(any(VisitDO.class));
    }

    @Test
    @DisplayName("创建就诊-默认状态为待就诊(0)")
    void createVisit_defaultStatusPending() {
        VisitSaveReqVO req = new VisitSaveReqVO();
        req.setPatientId(1L);
        req.setDoctorId(2L);

        ArgumentCaptor<VisitDO> captor = ArgumentCaptor.forClass(VisitDO.class);
        visitService.createVisit(req);
        verify(visitMapper).insert(captor.capture());
        assertEquals(VisitStatusEnum.PENDING.getStatus(), captor.getValue().getStatus());
    }
}
