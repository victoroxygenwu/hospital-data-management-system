package cn.iocoder.yudao.module.hospital.controller.admin.knowledge;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.AssistDiagnosisReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.AssistDiagnosisRespVO;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.PrescriptionReviewReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.ReviewResponseVO;
import cn.iocoder.yudao.module.hospital.service.knowledge.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * AI 辅助诊断与处方审核 管理后台 Controller
 */
@Tag(name = "管理后台 - AI 辅助")
@RestController
@RequestMapping("/hospital/ai")
@Validated
public class AiController {

    @Resource
    private AiService aiService;

    @PostMapping("/assist-diagnosis")
    @Operation(summary = "AI 辅助诊断")
    @PreAuthorize("@ss.hasPermission('hospital:ai:assist-diagnosis')")
    public CommonResult<AssistDiagnosisRespVO> assistDiagnosis(@Valid @RequestBody AssistDiagnosisReqVO reqVO) {
        return success(aiService.assistDiagnosis(reqVO.getSymptomDescription()));
    }

    @PostMapping("/prescription-review")
    @Operation(summary = "AI 处方审核")
    @PreAuthorize("@ss.hasPermission('hospital:ai:prescription-review')")
    public CommonResult<ReviewResponseVO> prescriptionReview(@Valid @RequestBody PrescriptionReviewReqVO reqVO) {
        return success(aiService.prescriptionReview(reqVO.getPrescriptionId()));
    }

}
