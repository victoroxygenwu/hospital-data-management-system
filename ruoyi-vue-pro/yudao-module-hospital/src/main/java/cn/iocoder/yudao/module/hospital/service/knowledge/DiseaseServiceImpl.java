package cn.iocoder.yudao.module.hospital.service.knowledge;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseasePageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseaseSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DiseaseDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.DiseaseMapper;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.DISEASE_NOT_EXISTS;

/**
 * 疾病 Service 实现类
 */
@Service
public class DiseaseServiceImpl implements DiseaseService {

    @Resource
    private DiseaseMapper diseaseMapper;
    @Resource
    private HospitalSecurityContext securityContext;

    @Override
    public Long createDisease(DiseaseSaveReqVO createReqVO) {
        securityContext.requireAdmin();
        DiseaseDO disease = BeanUtils.toBean(createReqVO, DiseaseDO.class);
        diseaseMapper.insert(disease);
        return disease.getId();
    }

    @Override
    public void updateDisease(DiseaseSaveReqVO updateReqVO) {
        securityContext.requireAdmin();
        validateDiseaseExists(updateReqVO.getId());
        DiseaseDO updateObj = BeanUtils.toBean(updateReqVO, DiseaseDO.class);
        diseaseMapper.updateById(updateObj);
    }

    @Override
    public void deleteDisease(Long id) {
        securityContext.requireAdmin();
        validateDiseaseExists(id);
        diseaseMapper.deleteById(id);
    }

    @Override
    public DiseaseDO getDisease(Long id) {
        return diseaseMapper.selectById(id);
    }

    @Override
    public PageResult<DiseaseDO> getDiseasePage(DiseasePageReqVO pageReqVO) {
        return diseaseMapper.selectPage(pageReqVO);
    }

    private void validateDiseaseExists(Long id) {
        if (id == null) {
            return;
        }
        if (diseaseMapper.selectById(id) == null) {
            throw exception(DISEASE_NOT_EXISTS);
        }
    }

}
