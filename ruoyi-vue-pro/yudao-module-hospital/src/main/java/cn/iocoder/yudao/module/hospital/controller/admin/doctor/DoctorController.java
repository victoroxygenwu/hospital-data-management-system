package cn.iocoder.yudao.module.hospital.controller.admin.doctor;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.doctor.vo.*;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DoctorDO;
import cn.iocoder.yudao.module.hospital.service.doctor.DoctorService;
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

@Tag(name = "管理后台 - 医生管理")
@RestController
@RequestMapping("/hospital/doctor")
@Validated
public class DoctorController {

    @Resource
    private DoctorService doctorService;

    @PostMapping("/create")
    @Operation(summary = "创建医生")
    @PreAuthorize("@ss.hasPermission('hospital:doctor:create')")
    public CommonResult<Long> createDoctor(@Valid @RequestBody DoctorSaveReqVO createReqVO) {
        return success(doctorService.createDoctor(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改医生")
    @PreAuthorize("@ss.hasPermission('hospital:doctor:update')")
    public CommonResult<Boolean> updateDoctor(@Valid @RequestBody DoctorSaveReqVO updateReqVO) {
        doctorService.updateDoctor(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除医生")
    @Parameter(name = "id", description = "医生ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:doctor:delete')")
    public CommonResult<Boolean> deleteDoctor(@RequestParam("id") Long id) {
        doctorService.deleteDoctor(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取医生详情")
    @Parameter(name = "id", description = "医生ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:doctor:query')")
    public CommonResult<DoctorRespVO> getDoctor(@RequestParam("id") Long id) {
        DoctorDO doctor = doctorService.getDoctor(id);
        return success(BeanUtils.toBean(doctor, DoctorRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取医生分页列表")
    @PreAuthorize("@ss.hasPermission('hospital:doctor:query')")
    public CommonResult<PageResult<DoctorRespVO>> getDoctorPage(@Validated DoctorPageReqVO pageReqVO) {
        PageResult<DoctorDO> pageResult = doctorService.getDoctorPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, DoctorRespVO.class));
    }

    @GetMapping("/list-by-dept")
    @Operation(summary = "获取某科室下的医生列表")
    @Parameter(name = "deptId", description = "科室ID", required = true)
    public CommonResult<List<DoctorRespVO>> getDoctorListByDeptId(@RequestParam("deptId") Long deptId) {
        List<DoctorDO> list = doctorService.getDoctorListByDeptId(deptId);
        return success(BeanUtils.toBean(list, DoctorRespVO.class));
    }
}
