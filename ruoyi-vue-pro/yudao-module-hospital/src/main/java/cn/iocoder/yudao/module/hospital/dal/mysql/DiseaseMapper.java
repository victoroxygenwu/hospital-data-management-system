package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.DiseasePageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DiseaseDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/** 疾病 Mapper */
@Mapper
public interface DiseaseMapper extends BaseMapperX<DiseaseDO> {

    /** 分页查询 */
    default PageResult<DiseaseDO> selectPage(DiseasePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DiseaseDO>()
                .likeIfPresent(DiseaseDO::getName, reqVO.getName())
                .likeIfPresent(DiseaseDO::getIcdCode, reqVO.getIcdCode())
                .likeIfPresent(DiseaseDO::getCategory, reqVO.getCategory())
                .eqIfPresent(DiseaseDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(DiseaseDO::getIsCommon, reqVO.getIsCommon())
                .orderByDesc(DiseaseDO::getId));
    }

    /** 批量按 ID 查询 */
    default List<DiseaseDO> selectListByIds(Collection<Long> ids) {
        return selectList(DiseaseDO::getId, ids);
    }

    /** 按名称或典型症状模糊匹配疾病 */
    default List<DiseaseDO> selectListByDiagnosisLike(String diagnosis) {
        return selectList(new LambdaQueryWrapperX<DiseaseDO>()
                .like(DiseaseDO::getName, diagnosis)
                .or()
                .like(DiseaseDO::getTypicalSymptoms, diagnosis));
    }

}
