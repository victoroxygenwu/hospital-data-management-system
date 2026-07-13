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
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.VISIT_NOT_EXISTS;

@Service
public class VisitServiceImpl implements VisitService {

    @Resource
    private VisitMapper visitMapper;
    @Resource
    private PrescriptionMapper prescriptionMapper;
    @Resource
    private PrescriptionItemMapper prescriptionItemMapper;
    @Resource
    private BillMapper billMapper;
    @Resource
    private HospitalSecurityContext securityContext;

    @Override
    public Long createVisit(VisitSaveReqVO createReqVO) {
        VisitDO visit = BeanUtils.toBean(createReqVO, VisitDO.class);
        visitMapper.insert(visit);
        return visit.getId();
    }

    @Override
    public void updateVisit(VisitSaveReqVO updateReqVO) {
        validateVisitExists(updateReqVO.getId());
        VisitDO updateObj = BeanUtils.toBean(updateReqVO, VisitDO.class);
        visitMapper.updateById(updateObj);
    }

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

    @Override
    public VisitDO getVisit(Long id) {
        return visitMapper.selectById(id);
    }

    @Override
    public PageResult<VisitDO> getVisitPage(VisitPageReqVO pageReqVO) {
        // 角色数据隔离：医生只看自己的患者，患者只看自己的记录
        if (!securityContext.isAdmin()) {
            Long doctorId = securityContext.getCurrentDoctorId();
            Long patientId = securityContext.getCurrentPatientId();
            if (doctorId != null) {
                pageReqVO.setDoctorId(doctorId);
            } else if (patientId != null) {
                pageReqVO.setPatientId(patientId);
            }
        }
        return visitMapper.selectPage(pageReqVO);
    }

    @Override
    public List<VisitDO> getVisitsByPatientId(Long patientId) {
        return visitMapper.selectList(
                new LambdaQueryWrapperX<VisitDO>().eq(VisitDO::getPatientId, patientId).orderByDesc(VisitDO::getVisitDate));
    }

    private void validateVisitExists(Long id) {
        if (id == null) return;
        if (visitMapper.selectById(id) == null) throw exception(VISIT_NOT_EXISTS);
    }
}
