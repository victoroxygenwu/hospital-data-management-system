package cn.iocoder.yudao.module.hospital.service.knowledge;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseaseSymptomPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseaseSymptomRespVO;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseaseSymptomSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DiseaseDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DiseaseSymptomDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.SymptomDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.DiseaseMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.DiseaseSymptomMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.SymptomMapper;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.*;

/**
 * 疾病-症状关联 Service 实现类
 */
@Service
public class DiseaseSymptomServiceImpl implements DiseaseSymptomService {

    @Resource
    private DiseaseSymptomMapper diseaseSymptomMapper;
    @Resource
    private DiseaseMapper diseaseMapper;
    @Resource
    private SymptomMapper symptomMapper;
    @Resource
    private HospitalSecurityContext securityContext;

    @Override
    public Long createDiseaseSymptom(DiseaseSymptomSaveReqVO createReqVO) {
        securityContext.requireAdmin();
        if (diseaseMapper.selectById(createReqVO.getDiseaseId()) == null) {
            throw exception(DISEASE_NOT_EXISTS);
        }
        if (symptomMapper.selectById(createReqVO.getSymptomId()) == null) {
            throw exception(SYMPTOM_NOT_EXISTS);
        }
        DiseaseSymptomDO relation = BeanUtils.toBean(createReqVO, DiseaseSymptomDO.class);
        diseaseSymptomMapper.insert(relation);
        return relation.getId();
    }

    @Override
    public void deleteDiseaseSymptom(Long id) {
        securityContext.requireAdmin();
        validateDiseaseSymptomExists(id);
        diseaseSymptomMapper.deleteById(id);
    }

    @Override
    public PageResult<DiseaseSymptomRespVO> getDiseaseSymptomPage(DiseaseSymptomPageReqVO pageReqVO) {
        PageResult<DiseaseSymptomDO> pageResult = diseaseSymptomMapper.selectPage(pageReqVO);
        return new PageResult<>(buildRespList(pageResult.getList()), pageResult.getTotal());
    }

    @Override
    public DiseaseSymptomDO getDiseaseSymptom(Long id) {
        return diseaseSymptomMapper.selectById(id);
    }

    private List<DiseaseSymptomRespVO> buildRespList(List<DiseaseSymptomDO> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> diseaseIds = list.stream().map(DiseaseSymptomDO::getDiseaseId).collect(Collectors.toSet());
        Set<Long> symptomIds = list.stream().map(DiseaseSymptomDO::getSymptomId).collect(Collectors.toSet());
        Map<Long, String> diseaseNameMap = diseaseMapper.selectListByIds(diseaseIds).stream()
                .collect(Collectors.toMap(DiseaseDO::getId, DiseaseDO::getName, (a, b) -> a));
        Map<Long, String> symptomNameMap = symptomMapper.selectListByIds(symptomIds).stream()
                .collect(Collectors.toMap(SymptomDO::getId, SymptomDO::getName, (a, b) -> a));
        List<DiseaseSymptomRespVO> result = new ArrayList<>(list.size());
        for (DiseaseSymptomDO item : list) {
            DiseaseSymptomRespVO vo = BeanUtils.toBean(item, DiseaseSymptomRespVO.class);
            vo.setDiseaseName(diseaseNameMap.get(item.getDiseaseId()));
            vo.setSymptomName(symptomNameMap.get(item.getSymptomId()));
            result.add(vo);
        }
        return result;
    }

    private void validateDiseaseSymptomExists(Long id) {
        if (id == null) {
            return;
        }
        if (diseaseSymptomMapper.selectById(id) == null) {
            throw exception(DISEASE_SYMPTOM_NOT_EXISTS);
        }
    }

}
