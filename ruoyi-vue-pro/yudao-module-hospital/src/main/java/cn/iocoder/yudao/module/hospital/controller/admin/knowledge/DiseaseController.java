package cn.iocoder.yudao.module.hospital.controller.admin.knowledge;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.*;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DiseaseDO;
import cn.iocoder.yudao.module.hospital.service.knowledge.DiseaseService;
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
 * 疾病字典 管理后台 Controller
 */
@Tag(name = "管理后台 - 疾病字典")
@RestController
@RequestMapping("/hospital/disease")
@Validated
public class DiseaseController {

    @Resource
    private DiseaseService diseaseService;

    @PostMapping("/create")
    @Operation(summary = "创建疾病")
    @PreAuthorize("@ss.hasPermission('hospital:disease:create')")
    public CommonResult<Long> createDisease(@Valid @RequestBody DiseaseSaveReqVO createReqVO) {
        return success(diseaseService.createDisease(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改疾病")
    @PreAuthorize("@ss.hasPermission('hospital:disease:update')")
    public CommonResult<Boolean> updateDisease(@Valid @RequestBody DiseaseSaveReqVO updateReqVO) {
        diseaseService.updateDisease(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除疾病")
    @Parameter(name = "id", description = "疾病 ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:disease:delete')")
    public CommonResult<Boolean> deleteDisease(@RequestParam("id") Long id) {
        diseaseService.deleteDisease(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取疾病详情")
    @Parameter(name = "id", description = "疾病 ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:disease:query')")
    public CommonResult<DiseaseRespVO> getDisease(@RequestParam("id") Long id) {
        DiseaseDO disease = diseaseService.getDisease(id);
        return success(BeanUtils.toBean(disease, DiseaseRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取疾病分页列表")
    @PreAuthorize("@ss.hasPermission('hospital:disease:query')")
    public CommonResult<PageResult<DiseaseRespVO>> getDiseasePage(@Validated DiseasePageReqVO pageReqVO) {
        PageResult<DiseaseDO> pageResult = diseaseService.getDiseasePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, DiseaseRespVO.class));
    }

}
