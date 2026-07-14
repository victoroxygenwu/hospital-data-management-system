package cn.iocoder.yudao.module.hospital.service.ward;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.ward.vo.WardPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.ward.vo.WardSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BedDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.WardDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.BedMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.WardMapper;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.WARD_CAPACITY_FULL;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.WARD_NOT_EXISTS;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.WARD_NO_BED_TO_RELEASE;

/**
 * 病房 Service 实现类
 */
@Service
public class WardServiceImpl implements WardService {

    @Resource
    private WardMapper wardMapper; // 病房数据访问
    @Resource
    private BedMapper bedMapper; // 床位数据访问
    @Resource
    private HospitalSecurityContext securityContext; // 角色权限上下文

    /**
     * 创建病房
     * @param createReqVO 创建请求
     * @return 新病房ID
     */
    @Override
    public Long createWard(WardSaveReqVO createReqVO) {
        securityContext.requireAdmin();
        WardDO ward = BeanUtils.toBean(createReqVO, WardDO.class);
        wardMapper.insert(ward);
        return ward.getId();
    }

    /**
     * 更新病房
     * @param updateReqVO 更新请求
     */
    @Override
    public void updateWard(WardSaveReqVO updateReqVO) {
        securityContext.requireAdmin();
        validateWardExists(updateReqVO.getId());
        WardDO updateObj = BeanUtils.toBean(updateReqVO, WardDO.class);
        wardMapper.updateById(updateObj);
    }

    /**
     * 删除病房（级联删除病房下所有床位）
     * @param id 病房ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWard(Long id) {
        securityContext.requireAdmin();
        validateWardExists(id);
        // 级联删除病房下所有床位，避免孤儿数据
        bedMapper.delete(BedDO::getWardId, id);
        wardMapper.deleteById(id);
    }

    /**
     * 查询病房
     * @param id 病房ID
     * @return 病房信息
     */
    @Override
    public WardDO getWard(Long id) {
        return wardMapper.selectById(id);
    }

    /**
     * 分页查询病房
     * @param pageReqVO 分页请求
     * @return 病房分页结果
     */
    @Override
    public PageResult<WardDO> getWardPage(WardPageReqVO pageReqVO) {
        return wardMapper.selectPage(pageReqVO);
    }

    /**
     * 按科室ID查询病房列表
     * @param deptId 科室ID
     * @return 病房列表
     */
    @Override
    public List<WardDO> getWardListByDeptId(Long deptId) {
        return wardMapper.selectListByDeptId(deptId);
    }

    /**
     * 递增已用床位数（原子操作：SQL层校验 used_beds < capacity）
     * @param wardId 病房ID
     */
    @Override
    public void incrementUsedBeds(Long wardId) {
        int affected = wardMapper.incrementUsedBeds(wardId);
        if (affected == 0) {
            // SQL 层已校验 id 存在且 used_beds < capacity，affected=0 即条件不满足
            throw exception(WARD_CAPACITY_FULL);
        }
    }

    /**
     * 递减已用床位数（原子操作：SQL层校验 used_beds > 0）
     * @param wardId 病房ID
     */
    @Override
    public void decrementUsedBeds(Long wardId) {
        int affected = wardMapper.decrementUsedBeds(wardId);
        if (affected == 0) {
            // 已无已占用床位可释放（used_beds 已为 0 或 ward 不存在）
            throw exception(WARD_NO_BED_TO_RELEASE);
        }
    }

    private void validateWardExists(Long id) {
        if (id == null) return;
        if (wardMapper.selectById(id) == null) {
            throw exception(WARD_NOT_EXISTS);
        }
    }
}
