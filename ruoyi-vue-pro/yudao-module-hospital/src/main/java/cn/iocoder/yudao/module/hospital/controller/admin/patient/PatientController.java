package cn.iocoder.yudao.module.hospital.controller.admin.patient;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.patient.vo.*;
import cn.iocoder.yudao.module.hospital.controller.admin.visit.vo.VisitRespVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PatientDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.VisitDO;
import cn.iocoder.yudao.module.hospital.service.patient.PatientService;
import cn.iocoder.yudao.module.hospital.service.visit.VisitService;
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

/**
 * 病人管理 管理后台 Controller
 */
@Tag(name = "管理后台 - 病人管理")
@RestController
@RequestMapping("/hospital/patient")
@Validated
public class PatientController {

    @Resource
    private PatientService patientService; // 病人 Service
    @Resource
    private VisitService visitService; // 就诊 Service

    /**
     * 创建病人
     */
    @PostMapping("/create")
    @Operation(summary = "创建病人")
    @PreAuthorize("@ss.hasPermission('hospital:patient:create')")
    public CommonResult<Long> createPatient(@Valid @RequestBody PatientSaveReqVO createReqVO) {
        return success(patientService.createPatient(createReqVO));
    }

    /**
     * 修改病人
     */
    @PutMapping("/update")
    @Operation(summary = "修改病人")
    @PreAuthorize("@ss.hasPermission('hospital:patient:update')")
    public CommonResult<Boolean> updatePatient(@Valid @RequestBody PatientSaveReqVO updateReqVO) {
        patientService.updatePatient(updateReqVO);
        return success(true);
    }

    /**
     * 删除病人
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除病人")
    @Parameter(name = "id", description = "病人ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:patient:delete')")
    public CommonResult<Boolean> deletePatient(@RequestParam("id") Long id) {
        patientService.deletePatient(id);
        return success(true);
    }

    /**
     * 获取病人详情
     */
    @GetMapping("/get")
    @Operation(summary = "获取病人详情")
    @Parameter(name = "id", description = "病人ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:patient:query')")
    public CommonResult<PatientRespVO> getPatient(@RequestParam("id") Long id) {
        PatientDO patient = patientService.getPatient(id);
        return success(BeanUtils.toBean(patient, PatientRespVO.class));
    }

    /**
     * 获取病人分页列表
     */
    @GetMapping("/page")
    @Operation(summary = "获取病人分页列表")
    @PreAuthorize("@ss.hasPermission('hospital:patient:query')")
    public CommonResult<PageResult<PatientRespVO>> getPatientPage(@Validated PatientPageReqVO pageReqVO) {
        PageResult<PatientDO> pageResult = patientService.getPatientPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PatientRespVO.class));
    }

    /**
     * 获取病人的就诊记录
     */
    @GetMapping("/{id}/visits")
    @Operation(summary = "获取病人的就诊记录")
    @Parameter(name = "id", description = "病人ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:patient:query')")
    public CommonResult<List<VisitRespVO>> getPatientVisits(@PathVariable("id") Long id) {
        List<VisitDO> visits = visitService.getVisitsByPatientId(id);
        return success(BeanUtils.toBean(visits, VisitRespVO.class));
    }
}
