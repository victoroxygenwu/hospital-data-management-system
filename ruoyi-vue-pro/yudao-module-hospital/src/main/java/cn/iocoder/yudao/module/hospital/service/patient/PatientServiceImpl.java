package cn.iocoder.yudao.module.hospital.service.patient;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.patient.vo.PatientPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.patient.vo.PatientSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BedDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BillDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PatientDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionItemDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.VisitDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.BedMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.BillMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.PatientMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.PrescriptionItemMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.PrescriptionMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.VisitMapper;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import cn.iocoder.yudao.module.hospital.service.ward.WardService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.PATIENT_NOT_EXISTS;

/**
 * 患者 Service 实现类
 */
@Service
public class PatientServiceImpl implements PatientService {

    @Resource
    private PatientMapper patientMapper; // 患者数据访问
    @Resource
    private VisitMapper visitMapper; // 就诊数据访问
    @Resource
    private PrescriptionMapper prescriptionMapper; // 处方数据访问
    @Resource
    private PrescriptionItemMapper prescriptionItemMapper; // 处方明细数据访问
    @Resource
    private BillMapper billMapper; // 账单数据访问
    @Resource
    private BedMapper bedMapper; // 床位数据访问
    @Resource
    private WardService wardService; // 病房服务
    @Resource
    private HospitalSecurityContext securityContext; // 角色权限上下文

    /**
     * 创建患者
     * @param createReqVO 创建请求
     * @return 新患者ID
     */
    @Override
    public Long createPatient(PatientSaveReqVO createReqVO) {
        securityContext.requireAdmin(); // 患者档案由管理员录入，避免匿名建档（与 createDoctor 一致）
        PatientDO patient = BeanUtils.toBean(createReqVO, PatientDO.class);
        patientMapper.insert(patient);
        return patient.getId();
    }

    /**
     * 更新患者
     * @param updateReqVO 更新请求
     */
    @Override
    public void updatePatient(PatientSaveReqVO updateReqVO) {
        validatePatientExists(updateReqVO.getId());
        PatientDO updateObj = BeanUtils.toBean(updateReqVO, PatientDO.class);
        patientMapper.updateById(updateObj);
    }

    /**
     * 删除患者（级联删除关联的就诊/处方/明细/账单，释放床位）
     * @param id 患者ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePatient(Long id) {
        validatePatientExists(id);
        // 1. 级联删除就诊及其子表（处方→处方明细→账单）
        List<VisitDO> visits = visitMapper.selectList(new LambdaQueryWrapper<VisitDO>()
                .eq(VisitDO::getPatientId, id));
        for (VisitDO visit : visits) {
            List<PrescriptionDO> prescriptions = prescriptionMapper.selectList(
                    new LambdaQueryWrapper<PrescriptionDO>()
                            .eq(PrescriptionDO::getVisitId, visit.getId()));
            for (PrescriptionDO p : prescriptions) {
                prescriptionItemMapper.delete(PrescriptionItemDO::getPrescriptionId, p.getId());
                prescriptionMapper.deleteById(p.getId());
            }
            billMapper.delete(BillDO::getVisitId, visit.getId());
            visitMapper.deleteById(visit.getId());
        }
        // 2. 删除直接关联的账单
        billMapper.delete(BillDO::getPatientId, id);
        // 3. 释放床位关联
        List<BedDO> beds = bedMapper.selectList(new LambdaQueryWrapper<BedDO>()
                .eq(BedDO::getPatientId, id));
        for (BedDO bed : beds) {
            wardService.decrementUsedBeds(bed.getWardId());
            bed.setStatus(0);
            bed.setPatientId(null);
            bedMapper.updateById(bed);
        }
        // 4. 删除患者
        patientMapper.deleteById(id);
    }

    /**
     * 查询患者
     * @param id 患者ID
     * @return 患者信息
     */
    @Override
    public PatientDO getPatient(Long id) {
        return patientMapper.selectById(id);
    }

    /**
     * 分页查询患者（角色数据隔离：患者只能看到自己的档案）
     * @param pageReqVO 分页请求
     * @return 患者分页结果
     */
    @Override
    public PageResult<PatientDO> getPatientPage(PatientPageReqVO pageReqVO) {
        // 角色数据隔离：患者只能看到自己的档案，走正常分页
        Long patientId = securityContext.resolvePatientScope();
        if (patientId != null) {
            pageReqVO.setId(patientId);
        }
        return patientMapper.selectPage(pageReqVO);
    }

    private void validatePatientExists(Long id) {
        if (id == null) return;
        if (patientMapper.selectById(id) == null) {
            throw exception(PATIENT_NOT_EXISTS);
        }
    }
}
