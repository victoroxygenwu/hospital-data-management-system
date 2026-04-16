package cn.iocoder.yudao.module.hospital.controller.admin.ward;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.ward.vo.*;
import cn.iocoder.yudao.module.hospital.controller.admin.bed.vo.BedRespVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BedDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.WardDO;
import cn.iocoder.yudao.module.hospital.service.ward.WardService;
import cn.iocoder.yudao.module.hospital.service.bed.BedService;
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

@Tag(name = "管理后台 - 病房管理")
@RestController
@RequestMapping("/hospital/ward")
@Validated
public class WardController {

    @Resource
    private WardService wardService;
    @Resource
    private BedService bedService;

    @PostMapping("/create")
    @Operation(summary = "创建病房")
    @PreAuthorize("@ss.hasPermission('hospital:ward:create')")
    public CommonResult<Long> createWard(@Valid @RequestBody WardSaveReqVO createReqVO) {
        return success(wardService.createWard(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改病房")
    @PreAuthorize("@ss.hasPermission('hospital:ward:update')")
    public CommonResult<Boolean> updateWard(@Valid @RequestBody WardSaveReqVO updateReqVO) {
        wardService.updateWard(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除病房")
    @Parameter(name = "id", description = "病房ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:ward:delete')")
    public CommonResult<Boolean> deleteWard(@RequestParam("id") Long id) {
        wardService.deleteWard(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取病房详情")
    @Parameter(name = "id", description = "病房ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:ward:query')")
    public CommonResult<WardRespVO> getWard(@RequestParam("id") Long id) {
        WardDO ward = wardService.getWard(id);
        return success(BeanUtils.toBean(ward, WardRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取病房分页列表")
    @PreAuthorize("@ss.hasPermission('hospital:ward:query')")
    public CommonResult<PageResult<WardRespVO>> getWardPage(@Validated WardPageReqVO pageReqVO) {
        PageResult<WardDO> pageResult = wardService.getWardPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, WardRespVO.class));
    }

    @GetMapping("/list-by-dept")
    @Operation(summary = "获取某科室下的病房列表")
    @Parameter(name = "deptId", description = "科室ID", required = true)
    public CommonResult<List<WardRespVO>> getWardListByDeptId(@RequestParam("deptId") Long deptId) {
        List<WardDO> list = wardService.getWardListByDeptId(deptId);
        return success(BeanUtils.toBean(list, WardRespVO.class));
    }

    @GetMapping("/{id}/beds")
    @Operation(summary = "获取病房下的床位列表")
    @Parameter(name = "id", description = "病房ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:ward:query')")
    public CommonResult<List<BedRespVO>> getWardBeds(@PathVariable("id") Long id) {
        List<BedDO> list = bedService.getBedListByWardId(id);
        return success(BeanUtils.toBean(list, BedRespVO.class));
    }
}
