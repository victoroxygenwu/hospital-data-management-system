package cn.iocoder.yudao.module.hospital.service.knowledge.dto;

import lombok.Data;

/** 知识图谱边查询结果 DTO */
@Data
public class NetworkEdgeDTO {
    /** symptom 或 medicine */
    private String type;
    private Long id;
    private String name;
    /** strength 或 usage_type */
    private Integer edgeValue;
}
