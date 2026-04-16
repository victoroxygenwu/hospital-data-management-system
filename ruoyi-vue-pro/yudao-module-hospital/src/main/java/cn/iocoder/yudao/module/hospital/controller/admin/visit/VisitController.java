package cn.iocoder.yudao.module.hospital.controller.admin.visit;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.visit.vo.*;
import cn.iocoder.yudao.module.hospital.dal.dataobject.VisitDO;
import cn.iocoder.yudao.module.hospital.service.visit.VisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 就诊管理")
@RestController
@RequestMapping("/hospital/visit")
@Validated
public class VisitController {

    @Resource
    private VisitService visitService;

    @PostMapping("/create")
    @Operation(summary = "创建就诊记录")
    @PreAuthorize("@ss.hasPermission('hospital:visit:create')")
    public CommonResult<Long> createVisit(@Valid @RequestBody VisitSaveReqVO createReqVO) {
        return success(visitService.createVisit(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改就诊记录")
    @PreAuthorize("@ss.hasPermission('hospital:visit:update')")
    public CommonResult<Boolean> updateVisit(@Valid @RequestBody VisitSaveReqVO updateReqVO) {
        visitService.updateVisit(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除就诊记录")
    @Parameter(name = "id", description = "就诊ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:visit:delete')")
    public CommonResult<Boolean> deleteVisit(@RequestParam("id") Long id) {
        visitService.deleteVisit(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取就诊详情")
    @Parameter(name = "id", description = "就诊ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:visit:query')")
    public CommonResult<VisitRespVO> getVisit(@RequestParam("id") Long id) {
        VisitDO visit = visitService.getVisit(id);
        return success(BeanUtils.toBean(visit, VisitRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取就诊分页列表")
    @PreAuthorize("@ss.hasPermission('hospital:visit:query')")
    public CommonResult<PageResult<VisitRespVO>> getVisitPage(@Validated VisitPageReqVO pageReqVO) {
        PageResult<VisitDO> pageResult = visitService.getVisitPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, VisitRespVO.class));
    }
}
