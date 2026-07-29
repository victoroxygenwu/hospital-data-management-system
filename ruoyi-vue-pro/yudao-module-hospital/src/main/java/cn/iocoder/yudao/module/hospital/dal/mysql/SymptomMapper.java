package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.knowledge.vo.SymptomPageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.SymptomDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/** 症状 Mapper */
@Mapper
public interface SymptomMapper extends BaseMapperX<SymptomDO> {

    /** 分页查询 */
    default PageResult<SymptomDO> selectPage(SymptomPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SymptomDO>()
                .likeIfPresent(SymptomDO::getName, reqVO.getName())
                .likeIfPresent(SymptomDO::getLocation, reqVO.getLocation())
                .likeIfPresent(SymptomDO::getType, reqVO.getType())
                .orderByDesc(SymptomDO::getId));
    }

    /** 批量按 ID 查询 */
    default List<SymptomDO> selectListByIds(Collection<Long> ids) {
        return selectList(SymptomDO::getId, ids);
    }

    /** 按名称模糊匹配症状（用于知识图谱症状匹配） */
    default List<SymptomDO> selectListByNameLike(String name) {
        return selectList(new LambdaQueryWrapperX<SymptomDO>()
                .like(SymptomDO::getName, name));
    }

}
