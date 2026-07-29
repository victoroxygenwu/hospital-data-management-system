package cn.iocoder.yudao.module.hospital.controller.admin.knowledge;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseaseMedicinePageReqVO;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseaseMedicineRespVO;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseaseMedicineSaveReqVO;
import cn.iocoder.yudao.module.hospital.service.knowledge.DiseaseMedicineService;
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
 * 疾病-药品关联 管理后台 Controller
 */
@Tag(name = "管理后台 - 疾病-药品关联")
@RestController
@RequestMapping("/hospital/disease-medicine")
@Validated
public class DiseaseMedicineController {

    @Resource
    private DiseaseMedicineService diseaseMedicineService;

    @PostMapping("/create")
    @Operation(summary = "创建疾病-药品关联")
    @PreAuthorize("@ss.hasPermission('hospital:disease-medicine:create')")
    public CommonResult<Long> createDiseaseMedicine(@Valid @RequestBody DiseaseMedicineSaveReqVO createReqVO) {
        return success(diseaseMedicineService.createDiseaseMedicine(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改疾病-药品关联")
    @PreAuthorize("@ss.hasPermission('hospital:disease-medicine:update')")
    public CommonResult<Boolean> updateDiseaseMedicine(@Valid @RequestBody DiseaseMedicineSaveReqVO updateReqVO) {
        diseaseMedicineService.updateDiseaseMedicine(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除疾病-药品关联")
    @Parameter(name = "id", description = "关联 ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:disease-medicine:delete')")
    public CommonResult<Boolean> deleteDiseaseMedicine(@RequestParam("id") Long id) {
        diseaseMedicineService.deleteDiseaseMedicine(id);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获取疾病-药品关联分页列表")
    @PreAuthorize("@ss.hasPermission('hospital:disease-medicine:query')")
    public CommonResult<PageResult<DiseaseMedicineRespVO>> getDiseaseMedicinePage(
            @Validated DiseaseMedicinePageReqVO pageReqVO) {
        return success(diseaseMedicineService.getDiseaseMedicinePage(pageReqVO));
    }

}
