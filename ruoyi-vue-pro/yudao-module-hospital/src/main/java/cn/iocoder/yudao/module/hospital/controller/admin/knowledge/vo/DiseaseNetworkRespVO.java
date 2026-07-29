package cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 疾病知识图谱网络 Response VO")
@Data
public class DiseaseNetworkRespVO {

    @Schema(description = "节点列表")
    private List<Node> nodes;
    @Schema(description = "边列表")
    private List<Link> links;

    @Schema(description = "图谱节点")
    @Data
    public static class Node {
        @Schema(description = "节点 ID")
        private Long id;
        @Schema(description = "节点名称")
        private String name;
        @Schema(description = "节点类别：0疾病 1症状 2药品")
        private Integer category;
    }

    @Schema(description = "图谱边")
    @Data
    public static class Link {
        @Schema(description = "源节点 ID")
        private Long source;
        @Schema(description = "目标节点 ID")
        private Long target;
        @Schema(description = "边标签")
        private String label;
    }

}
