package cn.iocoder.yudao.module.hospital.controller.admin.prescription;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.prescription.vo.*;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionItemDO;
import cn.iocoder.yudao.module.hospital.service.prescription.PrescriptionService;
import cn.iocoder.yudao.module.hospital.dal.mysql.PrescriptionItemMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 处方管理")
@RestController
@RequestMapping("/hospital/prescription")
@Validated
public class PrescriptionController {

    @Resource
    private PrescriptionService prescriptionService;
    @Resource
    private PrescriptionItemMapper prescriptionItemMapper;

    @PostMapping("/create")
    @Operation(summary = "创建处方")
    @PreAuthorize("@ss.hasPermission('hospital:prescription:create')")
    public CommonResult<Long> createPrescription(@Valid @RequestBody PrescriptionSaveReqVO createReqVO) {
        return success(prescriptionService.createPrescription(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改处方")
    @PreAuthorize("@ss.hasPermission('hospital:prescription:update')")
    public CommonResult<Boolean> updatePrescription(@Valid @RequestBody PrescriptionSaveReqVO updateReqVO) {
        prescriptionService.updatePrescription(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除处方")
    @Parameter(name = "id", description = "处方ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:prescription:delete')")
    public CommonResult<Boolean> deletePrescription(@RequestParam("id") Long id) {
        prescriptionService.deletePrescription(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取处方详情（含明细）")
    @Parameter(name = "id", description = "处方ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:prescription:query')")
    public CommonResult<PrescriptionRespVO> getPrescription(@RequestParam("id") Long id) {
        PrescriptionDO prescription = prescriptionService.getPrescription(id);
        PrescriptionRespVO respVO = BeanUtils.toBean(prescription, PrescriptionRespVO.class);
        if (respVO != null) {
            List<PrescriptionItemDO> items = prescriptionItemMapper.selectListByPrescriptionId(id);
            respVO.setItems(BeanUtils.toBean(items, PrescriptionRespVO.PrescriptionItemRespVO.class));
        }
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获取处方分页列表")
    @PreAuthorize("@ss.hasPermission('hospital:prescription:query')")
    public CommonResult<PageResult<PrescriptionRespVO>> getPrescriptionPage(@Validated PrescriptionPageReqVO pageReqVO) {
        PageResult<PrescriptionDO> pageResult = prescriptionService.getPrescriptionPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PrescriptionRespVO.class));
    }

    @PutMapping("/dispense")
    @Operation(summary = "发药")
    @Parameter(name = "id", description = "处方ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:prescription:dispense')")
    public CommonResult<Boolean> dispensePrescription(@RequestParam("id") Long id) {
        prescriptionService.dispensePrescription(id);
        return success(true);
    }
}
