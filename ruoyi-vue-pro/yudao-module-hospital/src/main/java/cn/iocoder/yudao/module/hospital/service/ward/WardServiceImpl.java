package cn.iocoder.yudao.module.hospital.service.ward;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.ward.vo.WardPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.ward.vo.WardSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.WardDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.WardMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.WARD_CAPACITY_FULL;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.WARD_NOT_EXISTS;

@Service
public class WardServiceImpl implements WardService {

    @Resource
    private WardMapper wardMapper;

    @Override
    public Long createWard(WardSaveReqVO createReqVO) {
        WardDO ward = BeanUtils.toBean(createReqVO, WardDO.class);
        wardMapper.insert(ward);
        return ward.getId();
    }

    @Override
    public void updateWard(WardSaveReqVO updateReqVO) {
        validateWardExists(updateReqVO.getId());
        WardDO updateObj = BeanUtils.toBean(updateReqVO, WardDO.class);
        wardMapper.updateById(updateObj);
    }

    @Override
    public void deleteWard(Long id) {
        validateWardExists(id);
        wardMapper.deleteById(id);
    }

    @Override
    public WardDO getWard(Long id) {
        return wardMapper.selectById(id);
    }

    @Override
    public PageResult<WardDO> getWardPage(WardPageReqVO pageReqVO) {
        return wardMapper.selectPage(pageReqVO);
    }

    @Override
    public List<WardDO> getWardListByDeptId(Long deptId) {
        return wardMapper.selectListByDeptId(deptId);
    }

    @Override
    public void incrementUsedBeds(Long wardId) {
        int affected = wardMapper.incrementUsedBeds(wardId);
        if (affected == 0) {
            // SQL 层已校验 id 存在且 used_beds < capacity，affected=0 即条件不满足
            throw exception(WARD_CAPACITY_FULL);
        }
    }

    @Override
    public void decrementUsedBeds(Long wardId) {
        int affected = wardMapper.decrementUsedBeds(wardId);
        if (affected == 0) {
            // 已无已占用床位可释放（used_beds 已为 0 或 ward 不存在）
            throw exception(WARD_CAPACITY_FULL);
        }
    }

    private void validateWardExists(Long id) {
        if (id == null) return;
        if (wardMapper.selectById(id) == null) {
            throw exception(WARD_NOT_EXISTS);
        }
    }
}
