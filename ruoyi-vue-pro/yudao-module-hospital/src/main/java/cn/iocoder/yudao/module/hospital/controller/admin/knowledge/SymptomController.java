package cn.iocoder.yudao.module.hospital.controller.admin.knowledge;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.*;
import cn.iocoder.yudao.module.hospital.dal.dataobject.SymptomDO;
import cn.iocoder.yudao.module.hospital.service.knowledge.SymptomService;
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
 * 症状字典 管理后台 Controller
 */
@Tag(name = "管理后台 - 症状字典")
@RestController
@RequestMapping("/hospital/symptom")
@Validated
public class SymptomController {

    @Resource
    private SymptomService symptomService;

    @PostMapping("/create")
    @Operation(summary = "创建症状")
    @PreAuthorize("@ss.hasPermission('hospital:symptom:create')")
    public CommonResult<Long> createSymptom(@Valid @RequestBody SymptomSaveReqVO createReqVO) {
        return success(symptomService.createSymptom(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改症状")
    @PreAuthorize("@ss.hasPermission('hospital:symptom:update')")
    public CommonResult<Boolean> updateSymptom(@Valid @RequestBody SymptomSaveReqVO updateReqVO) {
        symptomService.updateSymptom(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除症状")
    @Parameter(name = "id", description = "症状 ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:symptom:delete')")
    public CommonResult<Boolean> deleteSymptom(@RequestParam("id") Long id) {
        symptomService.deleteSymptom(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取症状详情")
    @Parameter(name = "id", description = "症状 ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:symptom:query')")
    public CommonResult<SymptomRespVO> getSymptom(@RequestParam("id") Long id) {
        SymptomDO symptom = symptomService.getSymptom(id);
        return success(BeanUtils.toBean(symptom, SymptomRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取症状分页列表")
    @PreAuthorize("@ss.hasPermission('hospital:symptom:query')")
    public CommonResult<PageResult<SymptomRespVO>> getSymptomPage(@Validated SymptomPageReqVO pageReqVO) {
        PageResult<SymptomDO> pageResult = symptomService.getSymptomPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SymptomRespVO.class));
    }

}
