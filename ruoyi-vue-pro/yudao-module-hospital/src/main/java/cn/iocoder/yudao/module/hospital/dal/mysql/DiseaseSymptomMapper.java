package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseaseSymptomPageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DiseaseSymptomDO;
import cn.iocoder.yudao.module.hospital.service.knowledge.dto.DiseaseMatchDTO;
import cn.iocoder.yudao.module.hospital.service.knowledge.dto.NetworkEdgeDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/** 疾病-症状关联 Mapper */
@Mapper
public interface DiseaseSymptomMapper extends BaseMapperX<DiseaseSymptomDO> {

    /** 分页查询 */
    default PageResult<DiseaseSymptomDO> selectPage(DiseaseSymptomPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DiseaseSymptomDO>()
                .eqIfPresent(DiseaseSymptomDO::getDiseaseId, reqVO.getDiseaseId())
                .eqIfPresent(DiseaseSymptomDO::getSymptomId, reqVO.getSymptomId())
                .eqIfPresent(DiseaseSymptomDO::getStrength, reqVO.getStrength())
                .orderByDesc(DiseaseSymptomDO::getId));
    }

    /** 按疾病 ID 查询关联列表 */
    default List<DiseaseSymptomDO> selectListByDiseaseId(Long diseaseId) {
        return selectList(DiseaseSymptomDO::getDiseaseId, diseaseId);
    }

    /** 统计引用指定疾病的关联数量 */
    default long countByDiseaseId(Long diseaseId) {
        return selectCount(DiseaseSymptomDO::getDiseaseId, diseaseId);
    }

    /** 统计引用指定症状的关联数量 */
    default long countBySymptomId(Long symptomId) {
        return selectCount(DiseaseSymptomDO::getSymptomId, symptomId);
    }

    /** 按症状 ID 列表匹配疾病（按匹配数降序） */
    @Select("<script>" +
            "SELECT d.id, d.name, d.dept_id AS deptId, d.icd_code AS icdCode, " +
            "COUNT(ds.symptom_id) AS matchCount " +
            "FROM hospital_disease d " +
            "JOIN hospital_disease_symptom ds ON d.id = ds.disease_id AND ds.deleted = 0 " +
            "WHERE ds.symptom_id IN " +
            "<foreach collection='symptomIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach> " +
            "AND d.deleted = 0 " +
            "GROUP BY d.id, d.name, d.dept_id, d.icd_code " +
            "ORDER BY matchCount DESC" +
            "</script>")
    List<DiseaseMatchDTO> selectDiseasesBySymptomIds(@Param("symptomIds") List<Long> symptomIds);

    /** 查询疾病关联的症状边（图谱用） */
    @Select("SELECT 'symptom' AS type, s.id, s.name, ds.strength AS edgeValue " +
            "FROM hospital_disease_symptom ds " +
            "JOIN hospital_symptom s ON ds.symptom_id = s.id AND s.deleted = 0 " +
            "WHERE ds.disease_id = #{diseaseId} AND ds.deleted = 0")
    List<NetworkEdgeDTO> selectSymptomEdgesByDiseaseId(@Param("diseaseId") Long diseaseId);

}
