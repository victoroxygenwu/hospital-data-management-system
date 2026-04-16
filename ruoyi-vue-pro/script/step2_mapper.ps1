$baseDir = "c:\Users\17404\Desktop\数据库原理\hospital-data-management-system\ruoyi-vue-pro\yudao-module-hospital\src\main\java\cn\iocoder\yudao\module\hospital"

# ===== Mapper Interfaces =====
# DepartmentMapper
@"
package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.department.vo.DepartmentPageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DepartmentDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DepartmentMapper extends BaseMapperX<DepartmentDO> {

    default PageResult<DepartmentDO> selectPage(DepartmentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DepartmentDO>()
                .likeIfPresent(DepartmentDO::getDeptName, reqVO.getDeptName())
                .eqIfPresent(DepartmentDO::getPhone, reqVO.getPhone())
                .orderByDesc(DepartmentDO::getId));
    }

}
"@ | Set-Content -Path "$baseDir\dal\mysql\DepartmentMapper.java" -Encoding UTF8

# DoctorMapper
@"
package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.doctor.vo.DoctorPageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DoctorDO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface DoctorMapper extends BaseMapperX<DoctorDO> {

    default PageResult<DoctorDO> selectPage(DoctorPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DoctorDO>()
                .likeIfPresent(DoctorDO::getName, reqVO.getName())
                .eqIfPresent(DoctorDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(DoctorDO::getTitle, reqVO.getTitle())
                .orderByDesc(DoctorDO::getId));
    }

    default List<DoctorDO> selectListByDeptId(Long deptId) {
        return selectList(new LambdaQueryWrapperX<DoctorDO>().eqIfPresent(DoctorDO::getDeptId, deptId));
    }

}
"@ | Set-Content -Path "$baseDir\dal\mysql\DoctorMapper.java" -Encoding UTF8

# PatientMapper
@"
package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.patient.vo.PatientPageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PatientDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatientMapper extends BaseMapperX<PatientDO> {

    default PageResult<PatientDO> selectPage(PatientPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PatientDO>()
                .likeIfPresent(PatientDO::getName, reqVO.getName())
                .eqIfPresent(PatientDO::getPhone, reqVO.getPhone())
                .eqIfPresent(PatientDO::getIdCard, reqVO.getIdCard())
                .orderByDesc(PatientDO::getId));
    }

}
"@ | Set-Content -Path "$baseDir\dal\mysql\PatientMapper.java" -Encoding UTF8

# WardMapper
@"
package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.ward.vo.WardPageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.WardDO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface WardMapper extends BaseMapperX<WardDO> {

    default PageResult<WardDO> selectPage(WardPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<WardDO>()
                .eqIfPresent(WardDO::getDeptId, reqVO.getDeptId())
                .likeIfPresent(WardDO::getWardNo, reqVO.getWardNo())
                .eqIfPresent(WardDO::getType, reqVO.getType())
                .eqIfPresent(WardDO::getStatus, reqVO.getStatus())
                .orderByDesc(WardDO::getId));
    }

    default List<WardDO> selectListByDeptId(Long deptId) {
        return selectList(new LambdaQueryWrapperX<WardDO>().eqIfPresent(WardDO::getDeptId, deptId));
    }

}
"@ | Set-Content -Path "$baseDir\dal\mysql\WardMapper.java" -Encoding UTF8

# BedMapper
@"
package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.bed.vo.BedPageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BedDO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface BedMapper extends BaseMapperX<BedDO> {

    default PageResult<BedDO> selectPage(BedPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BedDO>()
                .eqIfPresent(BedDO::getWardId, reqVO.getWardId())
                .likeIfPresent(BedDO::getBedNo, reqVO.getBedNo())
                .eqIfPresent(BedDO::getStatus, reqVO.getStatus())
                .orderByDesc(BedDO::getId));
    }

    default List<BedDO> selectListByWardId(Long wardId) {
        return selectList(new LambdaQueryWrapperX<BedDO>().eqIfPresent(BedDO::getWardId, wardId));
    }

}
"@ | Set-Content -Path "$baseDir\dal\mysql\BedMapper.java" -Encoding UTF8

# VisitMapper
@"
package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.visit.vo.VisitPageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.VisitDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VisitMapper extends BaseMapperX<VisitDO> {

    default PageResult<VisitDO> selectPage(VisitPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<VisitDO>()
                .eqIfPresent(VisitDO::getPatientId, reqVO.getPatientId())
                .eqIfPresent(VisitDO::getDoctorId, reqVO.getDoctorId())
                .eqIfPresent(VisitDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(VisitDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(VisitDO::getVisitDate, reqVO.getVisitDate())
                .orderByDesc(VisitDO::getId));
    }

}
"@ | Set-Content -Path "$baseDir\dal\mysql\VisitMapper.java" -Encoding UTF8

# MedicineMapper
@"
package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.medicine.vo.MedicinePageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.MedicineDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MedicineMapper extends BaseMapperX<MedicineDO> {

    default PageResult<MedicineDO> selectPage(MedicinePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MedicineDO>()
                .likeIfPresent(MedicineDO::getName, reqVO.getName())
                .eqIfPresent(MedicineDO::getManufacturer, reqVO.getManufacturer())
                .orderByDesc(MedicineDO::getId));
    }

}
"@ | Set-Content -Path "$baseDir\dal\mysql\MedicineMapper.java" -Encoding UTF8

# PrescriptionMapper
@"
package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.prescription.vo.PrescriptionPageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PrescriptionMapper extends BaseMapperX<PrescriptionDO> {

    default PageResult<PrescriptionDO> selectPage(PrescriptionPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PrescriptionDO>()
                .eqIfPresent(PrescriptionDO::getVisitId, reqVO.getVisitId())
                .eqIfPresent(PrescriptionDO::getDoctorId, reqVO.getDoctorId())
                .eqIfPresent(PrescriptionDO::getStatus, reqVO.getStatus())
                .orderByDesc(PrescriptionDO::getId));
    }

}
"@ | Set-Content -Path "$baseDir\dal\mysql\PrescriptionMapper.java" -Encoding UTF8

# PrescriptionItemMapper
@"
package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.hospital.dal.dataobject.PrescriptionItemDO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PrescriptionItemMapper extends BaseMapperX<PrescriptionItemDO> {

    default List<PrescriptionItemDO> selectListByPrescriptionId(Long prescriptionId) {
        return selectList(PrescriptionItemDO::getPrescriptionId, prescriptionId);
    }

}
"@ | Set-Content -Path "$baseDir\dal\mysql\PrescriptionItemMapper.java" -Encoding UTF8

# BillMapper
@"
package cn.iocoder.yudao.module.hospital.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.bill.vo.BillPageReqVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BillDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BillMapper extends BaseMapperX<BillDO> {

    default PageResult<BillDO> selectPage(BillPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BillDO>()
                .eqIfPresent(BillDO::getVisitId, reqVO.getVisitId())
                .eqIfPresent(BillDO::getPatientId, reqVO.getPatientId())
                .eqIfPresent(BillDO::getStatus, reqVO.getStatus())
                .orderByDesc(BillDO::getId));
    }

}
"@ | Set-Content -Path "$baseDir\dal\mysql\BillMapper.java" -Encoding UTF8

Write-Host "Step 2: Mapper interfaces created"
