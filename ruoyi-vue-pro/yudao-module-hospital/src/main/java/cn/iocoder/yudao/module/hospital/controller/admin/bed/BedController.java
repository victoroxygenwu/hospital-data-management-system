package cn.iocoder.yudao.module.hospital.controller.admin.bed;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.bed.vo.*;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BedDO;
import cn.iocoder.yudao.module.hospital.service.bed.BedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 床位管理")
@RestController
@RequestMapping("/hospital/bed")
@Validated
public class BedController {

    @Resource
    private BedService bedService;

    @PostMapping("/create")
    @Operation(summary = "创建床位")
    @PreAuthorize("@ss.hasPermission('hospital:bed:create')")
    public CommonResult<Long> createBed(@Valid @RequestBody BedSaveReqVO createReqVO) {
        return success(bedService.createBed(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改床位")
    @PreAuthorize("@ss.hasPermission('hospital:bed:update')")
    public CommonResult<Boolean> updateBed(@Valid @RequestBody BedSaveReqVO updateReqVO) {
        bedService.updateBed(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除床位")
    @Parameter(name = "id", description = "床位ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:bed:delete')")
    public CommonResult<Boolean> deleteBed(@RequestParam("id") Long id) {
        bedService.deleteBed(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取床位详情")
    @Parameter(name = "id", description = "床位ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:bed:query')")
    public CommonResult<BedRespVO> getBed(@RequestParam("id") Long id) {
        BedDO bed = bedService.getBed(id);
        return success(BeanUtils.toBean(bed, BedRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取床位分页列表")
    @PreAuthorize("@ss.hasPermission('hospital:bed:query')")
    public CommonResult<PageResult<BedRespVO>> getBedPage(@Validated BedPageReqVO pageReqVO) {
        PageResult<BedDO> pageResult = bedService.getBedPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, BedRespVO.class));
    }

    @GetMapping("/list-by-ward")
    @Operation(summary = "获取某病房下的床位列表")
    @Parameter(name = "wardId", description = "病房ID", required = true)
    public CommonResult<List<BedRespVO>> getBedListByWardId(@RequestParam("wardId") Long wardId) {
        List<BedDO> list = bedService.getBedListByWardId(wardId);
        return success(BeanUtils.toBean(list, BedRespVO.class));
    }

    @PutMapping("/assign")
    @Operation(summary = "床位分配")
    @Parameters({@Parameter(name = "bedId", description = "床位ID", required = true),
            @Parameter(name = "patientId", description = "患者ID", required = true)})
    @PreAuthorize("@ss.hasPermission('hospital:bed:assign')")
    public CommonResult<Boolean> assignBed(@RequestParam("bedId") Long bedId,
                                           @RequestParam("patientId") Long patientId) {
        bedService.assignBed(bedId, patientId);
        return success(true);
    }

    @PutMapping("/release")
    @Operation(summary = "床位释放")
    @Parameter(name = "bedId", description = "床位ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:bed:release')")
    public CommonResult<Boolean> releaseBed(@RequestParam("bedId") Long bedId) {
        bedService.releaseBed(bedId);
        return success(true);
    }
}
