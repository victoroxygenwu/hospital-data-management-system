package cn.iocoder.yudao.module.hospital.service.knowledge;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseaseMedicinePageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseaseMedicineRespVO;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseaseMedicineSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DiseaseDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DiseaseMedicineDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.MedicineDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.DiseaseMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.DiseaseMedicineMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.MedicineMapper;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.*;

/**
 * 疾病-药品关联 Service 实现类
 */
@Service
public class DiseaseMedicineServiceImpl implements DiseaseMedicineService {

    @Resource
    private DiseaseMedicineMapper diseaseMedicineMapper;
    @Resource
    private DiseaseMapper diseaseMapper;
    @Resource
    private MedicineMapper medicineMapper;
    @Resource
    private HospitalSecurityContext securityContext;

    @Override
    public Long createDiseaseMedicine(DiseaseMedicineSaveReqVO createReqVO) {
        securityContext.requireAdmin();
        if (diseaseMapper.selectById(createReqVO.getDiseaseId()) == null) {
            throw exception(DISEASE_NOT_EXISTS);
        }
        if (medicineMapper.selectById(createReqVO.getMedicineId()) == null) {
            throw exception(MEDICINE_NOT_EXISTS);
        }
        DiseaseMedicineDO relation = BeanUtils.toBean(createReqVO, DiseaseMedicineDO.class);
        diseaseMedicineMapper.insert(relation);
        return relation.getId();
    }

    @Override
    public void updateDiseaseMedicine(DiseaseMedicineSaveReqVO updateReqVO) {
        securityContext.requireAdmin();
        validateDiseaseMedicineExists(updateReqVO.getId());
        if (diseaseMapper.selectById(updateReqVO.getDiseaseId()) == null) {
            throw exception(DISEASE_NOT_EXISTS);
        }
        if (medicineMapper.selectById(updateReqVO.getMedicineId()) == null) {
            throw exception(MEDICINE_NOT_EXISTS);
        }
        DiseaseMedicineDO updateObj = BeanUtils.toBean(updateReqVO, DiseaseMedicineDO.class);
        diseaseMedicineMapper.updateById(updateObj);
    }

    @Override
    public void deleteDiseaseMedicine(Long id) {
        securityContext.requireAdmin();
        validateDiseaseMedicineExists(id);
        diseaseMedicineMapper.deleteById(id);
    }

    @Override
    public PageResult<DiseaseMedicineRespVO> getDiseaseMedicinePage(DiseaseMedicinePageReqVO pageReqVO) {
        PageResult<DiseaseMedicineDO> pageResult = diseaseMedicineMapper.selectPage(pageReqVO);
        return new PageResult<>(buildRespList(pageResult.getList()), pageResult.getTotal());
    }

    @Override
    public DiseaseMedicineDO getDiseaseMedicine(Long id) {
        return diseaseMedicineMapper.selectById(id);
    }

    private List<DiseaseMedicineRespVO> buildRespList(List<DiseaseMedicineDO> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> diseaseIds = list.stream().map(DiseaseMedicineDO::getDiseaseId).collect(Collectors.toSet());
        Set<Long> medicineIds = list.stream().map(DiseaseMedicineDO::getMedicineId).collect(Collectors.toSet());
        Map<Long, String> diseaseNameMap = diseaseMapper.selectListByIds(diseaseIds).stream()
                .collect(Collectors.toMap(DiseaseDO::getId, DiseaseDO::getName, (a, b) -> a));
        Map<Long, String> medicineNameMap = medicineMapper.selectListByMedicineIds(medicineIds).stream()
                .collect(Collectors.toMap(MedicineDO::getId, MedicineDO::getName, (a, b) -> a));
        List<DiseaseMedicineRespVO> result = new ArrayList<>(list.size());
        for (DiseaseMedicineDO item : list) {
            DiseaseMedicineRespVO vo = BeanUtils.toBean(item, DiseaseMedicineRespVO.class);
            vo.setDiseaseName(diseaseNameMap.get(item.getDiseaseId()));
            vo.setMedicineName(medicineNameMap.get(item.getMedicineId()));
            result.add(vo);
        }
        return result;
    }

    private void validateDiseaseMedicineExists(Long id) {
        if (id == null) {
            return;
        }
        if (diseaseMedicineMapper.selectById(id) == null) {
            throw exception(DISEASE_MEDICINE_NOT_EXISTS);
        }
    }

}
