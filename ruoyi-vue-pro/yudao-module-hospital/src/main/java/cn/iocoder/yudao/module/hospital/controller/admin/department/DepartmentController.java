package cn.iocoder.yudao.module.hospital.controller.admin.department;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.department.vo.*;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DepartmentDO;
import cn.iocoder.yudao.module.hospital.service.department.DepartmentService;
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

@Tag(name = "管理后台 - 科室管理")
@RestController
@RequestMapping("/hospital/department")
@Validated
public class DepartmentController {

    @Resource
    private DepartmentService departmentService;

    @PostMapping("/create")
    @Operation(summary = "创建科室")
    @PreAuthorize("@ss.hasPermission('hospital:department:create')")
    public CommonResult<Long> createDepartment(@Valid @RequestBody DepartmentSaveReqVO createReqVO) {
        return success(departmentService.createDepartment(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改科室")
    @PreAuthorize("@ss.hasPermission('hospital:department:update')")
    public CommonResult<Boolean> updateDepartment(@Valid @RequestBody DepartmentSaveReqVO updateReqVO) {
        departmentService.updateDepartment(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除科室")
    @Parameter(name = "id", description = "科室ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:department:delete')")
    public CommonResult<Boolean> deleteDepartment(@RequestParam("id") Long id) {
        departmentService.deleteDepartment(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取科室详情")
    @Parameter(name = "id", description = "科室ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:department:query')")
    public CommonResult<DepartmentRespVO> getDepartment(@RequestParam("id") Long id) {
        DepartmentDO department = departmentService.getDepartment(id);
        return success(BeanUtils.toBean(department, DepartmentRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取科室分页列表")
    @PreAuthorize("@ss.hasPermission('hospital:department:query')")
    public CommonResult<PageResult<DepartmentRespVO>> getDepartmentPage(@Validated DepartmentPageReqVO pageReqVO) {
        PageResult<DepartmentDO> pageResult = departmentService.getDepartmentPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, DepartmentRespVO.class));
    }

    @GetMapping("/list-all")
    @Operation(summary = "获取科室全列表（下拉选择用）")
    public CommonResult<List<DepartmentRespVO>> getDepartmentList() {
        List<DepartmentDO> list = departmentService.getDepartmentList();
        return success(BeanUtils.toBean(list, DepartmentRespVO.class));
    }
}
