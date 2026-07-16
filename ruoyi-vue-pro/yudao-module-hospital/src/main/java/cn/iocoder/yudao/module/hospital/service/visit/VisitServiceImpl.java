package cn.iocoder.yudao.module.hospital.service.visit;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.visit.vo.VisitPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.visit.vo.VisitSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BillDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionItemDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.VisitDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.BillMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.PrescriptionItemMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.PrescriptionMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.VisitMapper;
import cn.iocoder.yudao.module.hospital.enums.VisitStatusEnum;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.VISIT_NOT_EXISTS;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.VISIT_STATUS_ILLEGAL;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.HOSPITAL_DATA_ACCESS_DENIED;

/**
 * 就诊 Service 实现类
 */
@Service
public class VisitServiceImpl implements VisitService {

    @Resource
    private VisitMapper visitMapper; // 就诊数据访问
    @Resource
    private PrescriptionMapper prescriptionMapper; // 处方数据访问
    @Resource
    private PrescriptionItemMapper prescriptionItemMapper; // 处方明细数据访问
    @Resource
    private BillMapper billMapper; // 账单数据访问
    @Resource
    private HospitalSecurityContext securityContext; // 角色权限上下文

    /**
     * 创建就诊记录
     * @param createReqVO 创建请求
     * @return 新就诊ID
     */
    @Override
    public Long createVisit(VisitSaveReqVO createReqVO) {
        VisitDO visit = BeanUtils.toBean(createReqVO, VisitDO.class);
        // 身份绑定：医生/患者登录建档时，强制归属到当前登录身份（防止越权冒用他人 id）
        if (!securityContext.isAdmin()) {
            Long doctorId = securityContext.getCurrentDoctorId();
            if (doctorId != null) {
                visit.setDoctorId(doctorId);
            }
            Long patientId = securityContext.getCurrentPatientId();
            if (patientId != null) {
                visit.setPatientId(patientId);
            }
        }
        // 后端兜底默认状态为待就诊(0)，避免前端漏传导致状态为空
        if (visit.getStatus() == null) {
            visit.setStatus(VisitStatusEnum.PENDING.getStatus());
        }
        // 就诊时间兜底：为空或落在 epoch 边界（日期反序列化失败时会产生 1970 值，
        // 低于 TIMESTAMP 最小范围导致插入报错）时取当前时间
        LocalDateTime vd = visit.getVisitDate();
        if (vd == null || vd.getYear() <= 1970) {
            visit.setVisitDate(LocalDateTime.now());
        }
        visitMapper.insert(visit);
        return visit.getId();
    }

    /**
     * 更新就诊记录
     * @param updateReqVO 更新请求
     */
    @Override
    public void updateVisit(VisitSaveReqVO updateReqVO) {
        VisitDO existing = validateVisitExists(updateReqVO.getId());
        // 终态守卫：已完成 / 已取消 的状态不可再变更
        // 与本项目处方/账单的专用动作守卫一致，仅在通用 update 上加这一条最小保护
        Integer from = existing != null ? existing.getStatus() : null;
        Integer to = updateReqVO.getStatus();
        VisitStatusEnum fromEnum = VisitStatusEnum.fromStatus(from);
        if (to != null && fromEnum != null && fromEnum.isTerminal()
                && !fromEnum.getStatus().equals(to)) {
            throw exception(VISIT_STATUS_ILLEGAL);
        }
        VisitDO updateObj = BeanUtils.toBean(updateReqVO, VisitDO.class);
        visitMapper.updateById(updateObj);
    }

    /**
     * 删除就诊记录（级联删除关联的处方、处方明细和账单）
     * @param id 就诊ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVisit(Long id) {
        validateVisitExists(id);
        // 级联删除关联的处方（含明细）和账单，避免孤儿数据
        List<PrescriptionDO> prescriptions = prescriptionMapper.selectList(
                new LambdaQueryWrapperX<PrescriptionDO>().eq(PrescriptionDO::getVisitId, id));
        for (PrescriptionDO p : prescriptions) {
            prescriptionItemMapper.delete(PrescriptionItemDO::getPrescriptionId, p.getId());
            prescriptionMapper.deleteById(p.getId());
        }
        billMapper.delete(BillDO::getVisitId, id);
        visitMapper.deleteById(id);
    }

    /**
     * 查询就诊记录
     * @param id 就诊ID
     * @return 就诊信息
     */
    @Override
    public VisitDO getVisit(Long id) {
        return visitMapper.selectById(id);
    }

    /**
     * 分页查询就诊（角色数据隔离：医生看自己的患者，患者看自己的记录）
     * @param pageReqVO 分页请求
     * @return 就诊分页结果
     */
    @Override
    public PageResult<VisitDO> getVisitPage(VisitPageReqVO pageReqVO) {
        // 角色数据隔离：医生只看自己的患者，患者只看自己的记录
        Long doctorId = securityContext.resolveDoctorScope();
        Long patientId = securityContext.resolvePatientScope();
        if (doctorId != null) {
            pageReqVO.setDoctorId(doctorId);
        } else if (patientId != null) {
            pageReqVO.setPatientId(patientId);
        }
        return visitMapper.selectPage(pageReqVO);
    }

    /**
     * 按患者ID查询就诊列表（权限校验：非管理员只能查自己）
     * @param patientId 患者ID
     * @return 就诊列表
     */
    @Override
    public List<VisitDO> getVisitsByPatientId(Long patientId) {
        // 权限校验：非管理员只能查自己的就诊记录
        if (!securityContext.isAdmin()) {
            Long currentPatientId = securityContext.getCurrentPatientId();
            if (currentPatientId == null || !currentPatientId.equals(patientId)) {
                throw exception(HOSPITAL_DATA_ACCESS_DENIED);
            }
        }
        return visitMapper.selectList(
                new LambdaQueryWrapperX<VisitDO>().eq(VisitDO::getPatientId, patientId).orderByDesc(VisitDO::getVisitDate));
    }

    private VisitDO validateVisitExists(Long id) {
        if (id == null) return null;
        VisitDO visit = visitMapper.selectById(id);
        if (visit == null) throw exception(VISIT_NOT_EXISTS);
        return visit;
    }
}
