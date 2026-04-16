package cn.iocoder.yudao.module.hospital.service.bed;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.hospital.controller.admin.bed.vo.BedPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.bed.vo.BedSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BedDO;
import java.util.List;

public interface BedService {
    Long createBed(BedSaveReqVO createReqVO);
    void updateBed(BedSaveReqVO updateReqVO);
    void deleteBed(Long id);
    BedDO getBed(Long id);
    PageResult<BedDO> getBedPage(BedPageReqVO pageReqVO);
    List<BedDO> getBedListByWardId(Long wardId);
    /** 床位分配：将患者分配到指定床位 */
    void assignBed(Long bedId, Long patientId);
    /** 床位释放：患者出院，释放床位 */
    void releaseBed(Long bedId);
}
