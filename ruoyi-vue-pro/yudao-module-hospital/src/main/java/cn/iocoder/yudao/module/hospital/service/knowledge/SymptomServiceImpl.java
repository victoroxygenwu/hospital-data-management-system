package cn.iocoder.yudao.module.hospital.service.knowledge;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.SymptomPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.SymptomSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.SymptomDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.SymptomMapper;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.SYMPTOM_NOT_EXISTS;

/**
 * 症状 Service 实现类
 */
@Service
public class SymptomServiceImpl implements SymptomService {

    @Resource
    private SymptomMapper symptomMapper;
    @Resource
    private HospitalSecurityContext securityContext;

    @Override
    public Long createSymptom(SymptomSaveReqVO createReqVO) {
        securityContext.requireAdmin();
        SymptomDO symptom = BeanUtils.toBean(createReqVO, SymptomDO.class);
        symptomMapper.insert(symptom);
        return symptom.getId();
    }

    @Override
    public void updateSymptom(SymptomSaveReqVO updateReqVO) {
        securityContext.requireAdmin();
        validateSymptomExists(updateReqVO.getId());
        SymptomDO updateObj = BeanUtils.toBean(updateReqVO, SymptomDO.class);
        symptomMapper.updateById(updateObj);
    }

    @Override
    public void deleteSymptom(Long id) {
        securityContext.requireAdmin();
        validateSymptomExists(id);
        symptomMapper.deleteById(id);
    }

    @Override
    public SymptomDO getSymptom(Long id) {
        return symptomMapper.selectById(id);
    }

    @Override
    public PageResult<SymptomDO> getSymptomPage(SymptomPageReqVO pageReqVO) {
        return symptomMapper.selectPage(pageReqVO);
    }

    private void validateSymptomExists(Long id) {
        if (id == null) {
            return;
        }
        if (symptomMapper.selectById(id) == null) {
            throw exception(SYMPTOM_NOT_EXISTS);
        }
    }

}
