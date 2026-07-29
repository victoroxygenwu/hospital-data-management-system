package cn.iocoder.yudao.module.hospital.service.knowledge.dto;

import lombok.Data;

/** 疾病症状匹配结果 DTO */
@Data
public class DiseaseMatchDTO {
    private Long id;
    private String name;
    private Long deptId;
    private String icdCode;
    private Integer matchCount;
}
