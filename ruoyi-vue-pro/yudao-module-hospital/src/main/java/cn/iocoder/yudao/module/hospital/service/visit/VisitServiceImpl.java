package cn.iocoder.yudao.module.hospital.service.visit;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.visit.vo.VisitPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.visit.vo.VisitSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.VisitDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.VisitMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.VISIT_NOT_EXISTS;

@Service
public class VisitServiceImpl implements VisitService {

    @Resource
    private VisitMapper visitMapper;

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
    public void deleteVisit(Long id) {
        validateVisitExists(id);
        visitMapper.deleteById(id);
    }

    @Override
    public VisitDO getVisit(Long id) {
        return visitMapper.selectById(id);
    }

    @Override
    public PageResult<VisitDO> getVisitPage(VisitPageReqVO pageReqVO) {
        return visitMapper.selectPage(pageReqVO);
    }

    private void validateVisitExists(Long id) {
        if (id == null) return;
        if (visitMapper.selectById(id) == null) throw exception(VISIT_NOT_EXISTS);
    }
}
