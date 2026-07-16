package cn.iocoder.yudao.module.hospital.service.ward;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.hospital.controller.admin.ward.vo.WardSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.WardDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.WardMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.BedMapper;
import cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("病房 Service 单元测试（覆盖系统托管字段与管理员守卫）")
class WardServiceImplTest {

    private WardServiceImpl wardService;

    @Mock
    private WardMapper wardMapper;
    @Mock
    private BedMapper bedMapper;
    @Mock
    private HospitalSecurityContext securityContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        wardService = new WardServiceImpl();
        ReflectionTestUtils.setField(wardService, "wardMapper", wardMapper);
        ReflectionTestUtils.setField(wardService, "bedMapper", bedMapper);
        ReflectionTestUtils.setField(wardService, "securityContext", securityContext);
    }

    @Test
    @DisplayName("创建病房-成功：系统强制 usedBeds=0 且 status=0")
    void createWard_systemManagedFields() {
        wardService.createWard(new WardSaveReqVO());

        ArgumentCaptor<WardDO> captor = ArgumentCaptor.forClass(WardDO.class);
        verify(wardMapper).insert(captor.capture());
        assertEquals(0, captor.getValue().getUsedBeds());
        assertEquals(0, captor.getValue().getStatus());
    }

    @Test
    @DisplayName("更新病房-系统托管字段被置空：usedBeds/status 不经由前端修改")
    void updateWard_managedFieldsNulled() {
        WardSaveReqVO req = new WardSaveReqVO();
        req.setId(1L);
        when(wardMapper.selectById(1L)).thenReturn(new WardDO());

        wardService.updateWard(req);

        ArgumentCaptor<WardDO> captor = ArgumentCaptor.forClass(WardDO.class);
        verify(wardMapper).updateById(captor.capture());
        assertEquals(null, captor.getValue().getUsedBeds());
        assertEquals(null, captor.getValue().getStatus());
    }

    @Test
    @DisplayName("创建病房-非管理员被拒：requireAdmin 抛权限异常")
    void createWard_nonAdmin_rejected() {
        doThrow(new ServiceException(ErrorCodeConstants.HOSPITAL_PERMISSION_DENIED))
                .when(securityContext).requireAdmin();

        ServiceException ex = assertThrows(ServiceException.class,
                () -> wardService.createWard(new WardSaveReqVO()));
        assertEquals(ErrorCodeConstants.HOSPITAL_PERMISSION_DENIED.getCode(), ex.getCode());
        verify(wardMapper, never()).insert(any(WardDO.class));
    }
}
