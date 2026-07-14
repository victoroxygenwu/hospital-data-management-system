package cn.iocoder.yudao.module.hospital.service.bed;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.bed.vo.BedPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.bed.vo.BedSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BedDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.WardDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.BedMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.WardMapper;
import cn.iocoder.yudao.module.hospital.enums.BedStatusEnum;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import cn.iocoder.yudao.module.hospital.service.ward.WardService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.time.LocalDateTime;
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
    private WardMapper wardMapper; // 病房数据访问（用于校验床位容量）
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
        // 校验所属病房存在，并取出容量上限
        WardDO ward = wardMapper.selectById(createReqVO.getWardId());
        if (ward == null) throw exception(WARD_NOT_EXISTS);
        // 校验床位总数不超过病房容量（capacity 为 null 时不限制）
        if (ward.getCapacity() != null) {
            long bedCount = bedMapper.selectCount(
                    new LambdaQueryWrapper<BedDO>().eq(BedDO::getWardId, createReqVO.getWardId()));
            if (bedCount >= ward.getCapacity()) {
                throw exception(WARD_BED_EXCEED_CAPACITY);
            }
        }
        BedDO bed = BeanUtils.toBean(createReqVO, BedDO.class);
        // 新建床位默认为空闲，状态只能通过 assignBed/releaseBed 变更
        bed.setStatus(BedStatusEnum.FREE.getStatus());
        bed.setPatientId(null);
        bedMapper.insert(bed);
        return bed.getId();
    }

    /**
     * 更新床位（仅允许修改 wardId 和 bedNo，状态由分配/释放流程管理）
     * @param updateReqVO 更新请求
     */
    @Override
    public void updateBed(BedSaveReqVO updateReqVO) {
        securityContext.requireAdmin();
        validateBedExists(updateReqVO.getId());
        BedDO updateObj = BeanUtils.toBean(updateReqVO, BedDO.class);
        updateObj.setStatus(null);
        updateObj.setPatientId(null);
        bedMapper.updateById(updateObj);
    }

    /**
     * 删除床位，若床位被占用则同步递减病房已用床位数
     * @param id 床位ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
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
     * 分配床位（条件更新：仅当 status=FREE(0) 时才分配，WHERE 携带状态约束防止并发双占）
     * @param bedId 床位ID
     * @param patientId 患者ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignBed(Long bedId, Long patientId) {
        BedDO bed = bedMapper.selectById(bedId);
        if (bed == null) throw exception(BED_NOT_EXISTS);
        if (bed.getStatus() == null || !BedStatusEnum.FREE.getStatus().equals(bed.getStatus())) throw exception(BED_ALREADY_OCCUPIED);
        // 真条件更新：WHERE id=? AND status=FREE(0)。并发下若已被占用则影响行数为 0，
        // 直接抛异常，避免双占与 ward.usedBeds 虚高
        int rows = bedMapper.update(null, new LambdaUpdateWrapper<BedDO>()
                .eq(BedDO::getId, bedId)
                .eq(BedDO::getStatus, BedStatusEnum.FREE.getStatus())
                .set(BedDO::getStatus, BedStatusEnum.OCCUPIED.getStatus())
                .set(BedDO::getPatientId, patientId)
                .set(BedDO::getAdmissionTime, LocalDateTime.now()));
        if (rows == 0) throw exception(BED_ALREADY_OCCUPIED);
        wardService.incrementUsedBeds(bed.getWardId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseBed(Long bedId) {
        BedDO bed = bedMapper.selectById(bedId);
        if (bed == null) throw exception(BED_NOT_EXISTS);
        if (bed.getStatus() == null || !BedStatusEnum.OCCUPIED.getStatus().equals(bed.getStatus())) throw exception(BED_NOT_OCCUPIED);
        // 真条件更新：WHERE id=? AND status=OCCUPIED(1)。并发下若已被释放则影响行数为 0，
        // 直接抛异常，避免重复释放导致 ward.usedBeds 虚低
        int rows = bedMapper.update(null, new LambdaUpdateWrapper<BedDO>()
                .eq(BedDO::getId, bedId)
                .eq(BedDO::getStatus, BedStatusEnum.OCCUPIED.getStatus())
                .set(BedDO::getStatus, BedStatusEnum.FREE.getStatus())
                .set(BedDO::getPatientId, null)
                .set(BedDO::getAdmissionTime, null));
        if (rows == 0) throw exception(BED_NOT_OCCUPIED);
        wardService.decrementUsedBeds(bed.getWardId());
    }

    private void validateBedExists(Long id) {
        if (id == null) return;
        if (bedMapper.selectById(id) == null) throw exception(BED_NOT_EXISTS);
    }
}
