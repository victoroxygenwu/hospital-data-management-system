package cn.iocoder.yudao.module.hospital.service.bed;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.bed.vo.BedPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.bed.vo.BedSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BedDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.BedMapper;
import cn.iocoder.yudao.module.hospital.service.ward.WardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.*;

@Service
public class BedServiceImpl implements BedService {

    @Resource
    private BedMapper bedMapper;
    @Resource
    private WardService wardService;

    @Override
    public Long createBed(BedSaveReqVO createReqVO) {
        BedDO bed = BeanUtils.toBean(createReqVO, BedDO.class);
        bedMapper.insert(bed);
        return bed.getId();
    }

    @Override
    public void updateBed(BedSaveReqVO updateReqVO) {
        validateBedExists(updateReqVO.getId());
        BedDO updateObj = BeanUtils.toBean(updateReqVO, BedDO.class);
        bedMapper.updateById(updateObj);
    }

    @Override
    public void deleteBed(Long id) {
        validateBedExists(id);
        bedMapper.deleteById(id);
    }

    @Override
    public BedDO getBed(Long id) {
        return bedMapper.selectById(id);
    }

    @Override
    public PageResult<BedDO> getBedPage(BedPageReqVO pageReqVO) {
        return bedMapper.selectPage(pageReqVO);
    }

    @Override
    public List<BedDO> getBedListByWardId(Long wardId) {
        return bedMapper.selectListByWardId(wardId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignBed(Long bedId, Long patientId) {
        BedDO bed = bedMapper.selectById(bedId);
        if (bed == null) throw exception(BED_NOT_EXISTS);
        if ("已占用".equals(bed.getStatus())) throw exception(BED_ALREADY_OCCUPIED);
        bedMapper.updateById(BedDO.builder().id(bedId).status("已占用")
                .patientId(patientId).admissionTime(LocalDateTime.now()).build());
        wardService.incrementUsedBeds(bed.getWardId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseBed(Long bedId) {
        BedDO bed = bedMapper.selectById(bedId);
        if (bed == null) throw exception(BED_NOT_EXISTS);
        if (!"已占用".equals(bed.getStatus())) throw exception(BED_NOT_OCCUPIED);
        bedMapper.updateById(BedDO.builder().id(bedId).status("空闲")
                .patientId(null).admissionTime(null).build());
        wardService.decrementUsedBeds(bed.getWardId());
    }

    private void validateBedExists(Long id) {
        if (id == null) return;
        if (bedMapper.selectById(id) == null) throw exception(BED_NOT_EXISTS);
    }
}
