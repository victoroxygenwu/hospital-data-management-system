package cn.iocoder.yudao.module.hospital.controller.admin.knowledge;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseaseSymptomPageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseaseSymptomRespVO;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseaseSymptomSaveReqVO;
import cn.iocoder.yudao.module.hospital.service.knowledge.DiseaseSymptomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 疾病-症状关联 管理后台 Controller
 */
@Tag(name = "管理后台 - 疾病-症状关联")
@RestController
@RequestMapping("/hospital/disease-symptom")
@Validated
public class DiseaseSymptomController {

    @Resource
    private DiseaseSymptomService diseaseSymptomService;

    @PostMapping("/create")
    @Operation(summary = "创建疾病-症状关联")
    @PreAuthorize("@ss.hasPermission('hospital:disease-symptom:create')")
    public CommonResult<Long> createDiseaseSymptom(@Valid @RequestBody DiseaseSymptomSaveReqVO createReqVO) {
        return success(diseaseSymptomService.createDiseaseSymptom(createReqVO));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除疾病-症状关联")
    @Parameter(name = "id", description = "关联 ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:disease-symptom:delete')")
    public CommonResult<Boolean> deleteDiseaseSymptom(@RequestParam("id") Long id) {
        diseaseSymptomService.deleteDiseaseSymptom(id);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获取疾病-症状关联分页列表")
    @PreAuthorize("@ss.hasPermission('hospital:disease-symptom:query')")
    public CommonResult<PageResult<DiseaseSymptomRespVO>> getDiseaseSymptomPage(
            @Validated DiseaseSymptomPageReqVO pageReqVO) {
        return success(diseaseSymptomService.getDiseaseSymptomPage(pageReqVO));
    }

}
