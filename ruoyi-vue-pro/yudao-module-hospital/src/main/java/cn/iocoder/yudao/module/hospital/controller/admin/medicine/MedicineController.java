package cn.iocoder.yudao.module.hospital.controller.admin.medicine;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.medicine.vo.*;
import cn.iocoder.yudao.module.hospital.dal.dataobject.MedicineDO;
import cn.iocoder.yudao.module.hospital.service.medicine.MedicineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 药品管理")
@RestController
@RequestMapping("/hospital/medicine")
@Validated
public class MedicineController {

    @Resource
    private MedicineService medicineService;

    @PostMapping("/create")
    @Operation(summary = "创建药品")
    @PreAuthorize("@ss.hasPermission('hospital:medicine:create')")
    public CommonResult<Long> createMedicine(@Valid @RequestBody MedicineSaveReqVO createReqVO) {
        return success(medicineService.createMedicine(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改药品")
    @PreAuthorize("@ss.hasPermission('hospital:medicine:update')")
    public CommonResult<Boolean> updateMedicine(@Valid @RequestBody MedicineSaveReqVO updateReqVO) {
        medicineService.updateMedicine(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除药品")
    @Parameter(name = "id", description = "药品ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:medicine:delete')")
    public CommonResult<Boolean> deleteMedicine(@RequestParam("id") Long id) {
        medicineService.deleteMedicine(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取药品详情")
    @Parameter(name = "id", description = "药品ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:medicine:query')")
    public CommonResult<MedicineRespVO> getMedicine(@RequestParam("id") Long id) {
        MedicineDO medicine = medicineService.getMedicine(id);
        return success(BeanUtils.toBean(medicine, MedicineRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取药品分页列表")
    @PreAuthorize("@ss.hasPermission('hospital:medicine:query')")
    public CommonResult<PageResult<MedicineRespVO>> getMedicinePage(@Validated MedicinePageReqVO pageReqVO) {
        PageResult<MedicineDO> pageResult = medicineService.getMedicinePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MedicineRespVO.class));
    }
}
