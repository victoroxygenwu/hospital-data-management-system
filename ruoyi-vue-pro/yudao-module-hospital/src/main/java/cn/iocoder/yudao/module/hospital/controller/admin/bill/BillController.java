package cn.iocoder.yudao.module.hospital.controller.admin.bill;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hospital.controller.admin.bill.vo.*;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BillDO;
import cn.iocoder.yudao.module.hospital.service.bill.BillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 账单管理")
@RestController
@RequestMapping("/hospital/bill")
@Validated
public class BillController {

    @Resource
    private BillService billService;

    @PostMapping("/create")
    @Operation(summary = "创建账单")
    @PreAuthorize("@ss.hasPermission('hospital:bill:create')")
    public CommonResult<Long> createBill(@Valid @RequestBody BillSaveReqVO createReqVO) {
        return success(billService.createBill(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改账单")
    @PreAuthorize("@ss.hasPermission('hospital:bill:update')")
    public CommonResult<Boolean> updateBill(@Valid @RequestBody BillSaveReqVO updateReqVO) {
        billService.updateBill(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除账单")
    @Parameter(name = "id", description = "账单ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:bill:delete')")
    public CommonResult<Boolean> deleteBill(@RequestParam("id") Long id) {
        billService.deleteBill(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取账单详情")
    @Parameter(name = "id", description = "账单ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:bill:query')")
    public CommonResult<BillRespVO> getBill(@RequestParam("id") Long id) {
        BillDO bill = billService.getBill(id);
        return success(BeanUtils.toBean(bill, BillRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取账单分页列表")
    @PreAuthorize("@ss.hasPermission('hospital:bill:query')")
    public CommonResult<PageResult<BillRespVO>> getBillPage(@Validated BillPageReqVO pageReqVO) {
        PageResult<BillDO> pageResult = billService.getBillPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, BillRespVO.class));
    }

    @PutMapping("/pay")
    @Operation(summary = "支付账单")
    @Parameters({@Parameter(name = "id", description = "账单ID", required = true),
            @Parameter(name = "payMethod", description = "支付方式", required = true)})
    @PreAuthorize("@ss.hasPermission('hospital:bill:pay')")
    public CommonResult<Boolean> payBill(@RequestParam("id") Long id,
                                         @RequestParam("payMethod") String payMethod) {
        billService.payBill(id, payMethod);
        return success(true);
    }
}
