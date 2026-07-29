package cn.iocoder.yudao.module.hospital.service.visual;

import cn.iocoder.yudao.module.hospital.controller.admin.visual.vo.*;

import java.util.List;

public interface VisualService {

    List<HeatmapVO> getHeatmapData();

    List<DeptRadarVO> getDeptRadarData();

    PatientProfileRespVO getPatientProfile();

    List<DiseaseSeasonalVO> getDiseaseSeasonal();

    List<MedicineCooccurrenceVO> getMedicineCooccurrence();
}
