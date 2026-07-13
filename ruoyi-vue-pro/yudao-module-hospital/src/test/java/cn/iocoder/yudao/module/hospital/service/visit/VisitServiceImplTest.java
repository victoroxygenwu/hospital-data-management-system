package cn.iocoder.yudao.module.hospital.service.visit;

import cn.iocoder.yudao.module.hospital.controller.admin.visit.vo.VisitPageReqVO;
import cn.iocoder.yudao.module.hospital.dal.mysql.VisitMapper;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitServiceImplTest {

    @Mock
    private VisitMapper visitMapper;
    @Mock
    private HospitalSecurityContext securityContext;

    @InjectMocks
    private VisitServiceImpl visitService;

    // ==================== getVisitPage — 角色隔离（最复杂的三级分支） ====================

    @Test
    void getVisitPage_admin_seesAll() {
        VisitPageReqVO pageReqVO = new VisitPageReqVO();
        when(securityContext.isAdmin()).thenReturn(true);

        visitService.getVisitPage(pageReqVO);

        // 管理员不设置任何过滤
        assertNull(pageReqVO.getDoctorId());
        assertNull(pageReqVO.getPatientId());
        verify(visitMapper).selectPage(pageReqVO);
    }

    @Test
    void getVisitPage_doctor_seesOnlyOwnPatients() {
        VisitPageReqVO pageReqVO = new VisitPageReqVO();
        when(securityContext.isAdmin()).thenReturn(false);
        when(securityContext.getCurrentDoctorId()).thenReturn(3L);

        visitService.getVisitPage(pageReqVO);

        assertEquals(3L, pageReqVO.getDoctorId());
        assertNull(pageReqVO.getPatientId());
        verify(visitMapper).selectPage(pageReqVO);
    }

    @Test
    void getVisitPage_patient_seesOnlyOwnVisits() {
        VisitPageReqVO pageReqVO = new VisitPageReqVO();
        when(securityContext.isAdmin()).thenReturn(false);
        when(securityContext.getCurrentDoctorId()).thenReturn(null);
        when(securityContext.getCurrentPatientId()).thenReturn(7L);

        visitService.getVisitPage(pageReqVO);

        assertNull(pageReqVO.getDoctorId());
        assertEquals(7L, pageReqVO.getPatientId());
        verify(visitMapper).selectPage(pageReqVO);
    }

    @Test
    void getVisitPage_doctorTakesPriorityOverPatient() {
        // 如果有人同时有 doctorId 和 patientId，应优先按医生身份过滤
        VisitPageReqVO pageReqVO = new VisitPageReqVO();
        when(securityContext.isAdmin()).thenReturn(false);
        when(securityContext.getCurrentDoctorId()).thenReturn(3L);
        // getCurrentPatientId 可能也会被调用但结果应被忽略

        visitService.getVisitPage(pageReqVO);

        assertEquals(3L, pageReqVO.getDoctorId());
        // patientId 不应被设置，因为 doctor 优先级更高
        assertNull(pageReqVO.getPatientId());
        verify(visitMapper).selectPage(pageReqVO);
    }
}
