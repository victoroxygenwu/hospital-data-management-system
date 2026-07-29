package cn.iocoder.yudao.module.hospital.service.knowledge;

import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.AssistDiagnosisRespVO;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.ReviewResponseVO;

public interface AiService {

    AssistDiagnosisRespVO assistDiagnosis(String symptomDescription);

    ReviewResponseVO prescriptionReview(Long prescriptionId);

}
