package cn.iocoder.yudao.module.hospital.controller.admin.visual;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.hospital.controller.admin.visual.vo.*;
import cn.iocoder.yudao.module.hospital.service.visual.VisualService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 数据可视化")
@RestController
@RequestMapping("/hospital/visual")
@Validated
public class VisualController {

    @Resource
    private VisualService visualService;

    @GetMapping("/heatmap")
    @Operation(summary = "接诊热力图")
    @PreAuthorize("@ss.hasPermission('hospital:visual:query')")
    public CommonResult<List<HeatmapVO>> getHeatmap() {
        return success(visualService.getHeatmapData());
    }

    @GetMapping("/dept-radar")
    @Operation(summary = "科室雷达图")
    @PreAuthorize("@ss.hasPermission('hospital:visual:query')")
    public CommonResult<List<DeptRadarVO>> getDeptRadar() {
        return success(visualService.getDeptRadarData());
    }

    @GetMapping("/patient-profile")
    @Operation(summary = "患者画像")
    @PreAuthorize("@ss.hasPermission('hospital:visual:query')")
    public CommonResult<PatientProfileRespVO> getPatientProfile() {
        return success(visualService.getPatientProfile());
    }

    @GetMapping("/disease-seasonal")
    @Operation(summary = "疾病季节性趋势")
    @PreAuthorize("@ss.hasPermission('hospital:visual:query')")
    public CommonResult<List<DiseaseSeasonalVO>> getDiseaseSeasonal() {
        return success(visualService.getDiseaseSeasonal());
    }

    @GetMapping("/medicine-cooccurrence")
    @Operation(summary = "药品联合使用")
    @PreAuthorize("@ss.hasPermission('hospital:visual:query')")
    public CommonResult<List<MedicineCooccurrenceVO>> getMedicineCooccurrence() {
        return success(visualService.getMedicineCooccurrence());
    }
}
