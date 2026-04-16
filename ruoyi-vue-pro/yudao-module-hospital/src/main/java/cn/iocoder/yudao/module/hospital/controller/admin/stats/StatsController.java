package cn.iocoder.yudao.module.hospital.controller.admin.stats;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.hospital.service.stats.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 数据统计分析")
@RestController
@RequestMapping("/hospital/stats")
@Validated
public class StatsController {

    @Resource
    private StatsService statsService;

    @GetMapping("/visit-trend")
    @Operation(summary = "就诊趋势统计")
    @PreAuthorize("@ss.hasPermission('hospital:stats:query')")
    public CommonResult<List<Map<String, Object>>> getVisitTrend(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        return success(statsService.getVisitTrend(startDate, endDate));
    }

    @GetMapping("/ward-usage")
    @Operation(summary = "床位使用率统计")
    @PreAuthorize("@ss.hasPermission('hospital:stats:query')")
    public CommonResult<List<Map<String, Object>>> getWardUsage() {
        return success(statsService.getWardUsage());
    }

    @GetMapping("/medicine-stock")
    @Operation(summary = "药品库存统计")
    @PreAuthorize("@ss.hasPermission('hospital:stats:query')")
    public CommonResult<List<Map<String, Object>>> getMedicineStock() {
        return success(statsService.getMedicineStock());
    }
}
