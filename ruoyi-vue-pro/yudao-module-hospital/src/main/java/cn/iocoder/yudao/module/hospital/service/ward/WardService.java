package cn.iocoder.yudao.module.hospital.service.ward;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.hospital.controller.admin.ward.vo.WardPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.ward.vo.WardSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.WardDO;
import java.util.List;

public interface WardService {
    Long createWard(WardSaveReqVO createReqVO);
    void updateWard(WardSaveReqVO updateReqVO);
    void deleteWard(Long id);
    WardDO getWard(Long id);
    PageResult<WardDO> getWardPage(WardPageReqVO pageReqVO);
    List<WardDO> getWardListByDeptId(Long deptId);
}
