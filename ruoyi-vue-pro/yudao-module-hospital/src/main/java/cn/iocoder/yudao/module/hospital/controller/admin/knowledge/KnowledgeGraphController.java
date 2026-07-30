package cn.iocoder.yudao.module.hospital.controller.admin.knowledge;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseaseNetworkRespVO;
import cn.iocoder.yudao.module.hospital.service.knowledge.KnowledgeGraphService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 知识图谱 管理后台 Controller
 */
@Tag(name = "管理后台 - 知识图谱")
@RestController
@RequestMapping("/hospital/knowledge-graph")
@Validated
public class KnowledgeGraphController {

    @Resource
    private KnowledgeGraphService knowledgeGraphService;

    @GetMapping("/disease-network")
    @Operation(summary = "获取疾病关联图谱")
    @Parameter(name = "id", description = "疾病 ID", required = true)
    @PreAuthorize("@ss.hasPermission('hospital:knowledge-graph:query')")
    public CommonResult<DiseaseNetworkRespVO> getDiseaseNetwork(@RequestParam("id") Long id) {
        return success(knowledgeGraphService.getDiseaseNetwork(id));
    }

    @GetMapping("/full-network")
    @Operation(summary = "获取全量知识图谱（所有疾病/症状/药品及关联）")
    @PreAuthorize("@ss.hasPermission('hospital:knowledge-graph:query')")
    public CommonResult<DiseaseNetworkRespVO> getFullNetwork() {
        return success(knowledgeGraphService.getFullNetwork());
    }

}
