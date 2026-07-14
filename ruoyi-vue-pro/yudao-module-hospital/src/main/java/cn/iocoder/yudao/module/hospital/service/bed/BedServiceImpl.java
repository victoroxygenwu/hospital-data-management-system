package cn.iocoder.yudao.module.hospital.service.bed;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.bed.vo.BedPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.bed.vo.BedSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BedDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.BedMapper;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import cn.iocoder.yudao.module.hospital.service.ward.WardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.*;

/**
 * 床位 Service 实现类
 */
@Service
public class BedServiceImpl implements BedService {

    @Resource
    private BedMapper bedMapper; // 床位数据访问
    @Resource
    private WardService wardService; // 病房服务
    @Resource
    private HospitalSecurityContext securityContext; // 角色权限上下文

    /**
     * 创建床位
     * @param createReqVO 创建请求
     * @return 新床位ID
     */
    @Override
    public Long createBed(BedSaveReqVO createReqVO) {
        securityContext.requireAdmin();
        BedDO bed = BeanUtils.toBean(createReqVO, BedDO.class);
        bedMapper.insert(bed);
        return bed.getId();
    }

    /**
     * 更新床位
     * @param updateReqVO 更新请求
     */
    @Override
    public void updateBed(BedSaveReqVO updateReqVO) {
        securityContext.requireAdmin();
        validateBedExists(updateReqVO.getId());
        BedDO updateObj = BeanUtils.toBean(updateReqVO, BedDO.class);
        bedMapper.updateById(updateObj);
    }

    /**
     * 删除床位，若床位被占用则同步递减病房已用床位数
     * @param id 床位ID
     */
    @Override
    public void deleteBed(Long id) {
        securityContext.requireAdmin();
        BedDO bed = bedMapper.selectById(id);
        if (bed == null) throw exception(BED_NOT_EXISTS);
        // 如果床位被占用，需先递减病房已用床位数
        if (bed.getPatientId() != null) {
            wardService.decrementUsedBeds(bed.getWardId());
        }
        bedMapper.deleteById(id);
    }

    /**
     * 查询床位
     * @param id 床位ID
     * @return 床位信息
     */
    @Override
    public BedDO getBed(Long id) {
        return bedMapper.selectById(id);
    }

    /**
     * 分页查询床位
     * @param pageReqVO 分页请求
     * @return 床位分页结果
     */
    @Override
    public PageResult<BedDO> getBedPage(BedPageReqVO pageReqVO) {
        return bedMapper.selectPage(pageReqVO);
    }

    /**
     * 按病房ID查询床位列表
     * @param wardId 病房ID
     * @return 床位列表
     */
    @Override
    public List<BedDO> getBedListByWardId(Long wardId) {
        return bedMapper.selectListByWardId(wardId);
    }

    /**
     * 分配床位（原子操作：WHERE status=0 防止并发重复分配）
     * @param bedId 床位ID
     * @param patientId 患者ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignBed(Long bedId, Long patientId) {
        BedDO bed = getBedOrThrow(bedId);
        // 原子分配：WHERE status=0 防止并发重复分配
        int affected = bedMapper.assignBed(bedId, patientId);
        if (affected == 0) throw exception(BED_ALREADY_OCCUPIED);
        wardService.incrementUsedBeds(bed.getWardId());
    }

    /**
     * 释放床位（原子操作：WHERE status=1 防止并发重复释放）
     * @param bedId 床位ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseBed(Long bedId) {
        BedDO bed = getBedOrThrow(bedId);
        // 原子释放：WHERE status=1 防止并发重复释放
        int affected = bedMapper.releaseBed(bedId);
        if (affected == 0) throw exception(BED_NOT_OCCUPIED);
        wardService.decrementUsedBeds(bed.getWardId());
    }

    private BedDO getBedOrThrow(Long bedId) {
        BedDO bed = bedMapper.selectById(bedId);
        if (bed == null) throw exception(BED_NOT_EXISTS);
        return bed;
    }

    private void validateBedExists(Long id) {
        if (id == null) return;
        if (bedMapper.selectById(id) == null) throw exception(BED_NOT_EXISTS);
    }
}
