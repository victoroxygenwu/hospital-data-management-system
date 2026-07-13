package cn.iocoder.yudao.module.hospital.service.visit;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.hospital.controller.admin.visit.vo.VisitPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.visit.vo.VisitSaveReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.VisitDO;

import java.util.List;

public interface VisitService {
    Long createVisit(VisitSaveReqVO createReqVO);
    void updateVisit(VisitSaveReqVO updateReqVO);
    void deleteVisit(Long id);
    VisitDO getVisit(Long id);
    PageResult<VisitDO> getVisitPage(VisitPageReqVO pageReqVO);
    /** 获取指定患者的所有就诊记录 */
    List<VisitDO> getVisitsByPatientId(Long patientId);
}
