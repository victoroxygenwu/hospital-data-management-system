package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseaseMedicinePageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DiseaseMedicineDO;
import cn.iocoder.yudao.module.hospital.service.knowledge.dto.NetworkEdgeDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/** 疾病-药品关联 Mapper */
@Mapper
public interface DiseaseMedicineMapper extends BaseMapperX<DiseaseMedicineDO> {

    /** 分页查询 */
    default PageResult<DiseaseMedicineDO> selectPage(DiseaseMedicinePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DiseaseMedicineDO>()
                .eqIfPresent(DiseaseMedicineDO::getDiseaseId, reqVO.getDiseaseId())
                .eqIfPresent(DiseaseMedicineDO::getMedicineId, reqVO.getMedicineId())
                .eqIfPresent(DiseaseMedicineDO::getUsageType, reqVO.getUsageType())
                .orderByDesc(DiseaseMedicineDO::getId));
    }

    /** 按疾病 ID 查询关联列表 */
    default List<DiseaseMedicineDO> selectListByDiseaseId(Long diseaseId) {
        return selectList(DiseaseMedicineDO::getDiseaseId, diseaseId);
    }

    /** 统计引用指定疾病的关联数量 */
    default long countByDiseaseId(Long diseaseId) {
        return selectCount(DiseaseMedicineDO::getDiseaseId, diseaseId);
    }

    /** 统计引用指定药品的关联数量 */
    default long countByMedicineId(Long medicineId) {
        return selectCount(DiseaseMedicineDO::getMedicineId, medicineId);
    }

    /** 查询疾病关联的药品边（图谱用） */
    @Select("SELECT 'medicine' AS type, m.id, m.name, dm.usage_type AS edgeValue " +
            "FROM hospital_disease_medicine dm " +
            "JOIN hospital_medicine m ON dm.medicine_id = m.id AND m.deleted = 0 " +
            "WHERE dm.disease_id = #{diseaseId} AND dm.deleted = 0")
    List<NetworkEdgeDTO> selectMedicineEdgesByDiseaseId(@Param("diseaseId") Long diseaseId);

}
