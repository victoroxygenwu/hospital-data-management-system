package cn.iocoder.yudao.module.hospital.service.visual;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.visual.vo.*;
import cn.iocoder.yudao.module.hospital.dal.dataobject.*;
import cn.iocoder.yudao.module.hospital.dal.mysql.*;
import cn.iocoder.yudao.module.hospital.enums.VisitStatusEnum;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据可视化 Service 实现类
 */
@Service
public class VisualServiceImpl implements VisualService {

    private static final int DISEASE_TOP_N = 8;
    private static final int MIN_CO_OCCURRENCE = 2;
    private static final int REGION_TOP_N = 10;

    @Resource
    private HospitalSecurityContext securityContext;
    @Resource
    private VisitMapper visitMapper;
    @Resource
    private PatientMapper patientMapper;
    @Resource
    private PrescriptionItemMapper prescriptionItemMapper;
    @Resource
    private BillMapper billMapper;
    @Resource
    private DepartmentMapper departmentMapper;
    @Resource
    private MedicineMapper medicineMapper;

    @Override
    public List<HeatmapVO> getHeatmapData() {
        if (!canViewVisual()) {
            return Collections.emptyList();
        }
        Map<Long, String> deptNameMap = buildDeptNameMap();
        List<VisitDO> visits = visitMapper.selectList(new LambdaQueryWrapperX<>());
        if (visits.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, Map<Integer, Long>> grouped = visits.stream()
                .filter(v -> v.getVisitDate() != null && v.getDeptId() != null)
                .collect(Collectors.groupingBy(VisitDO::getDeptId,
                        Collectors.groupingBy(v -> v.getVisitDate().getHour(), Collectors.counting())));
        List<HeatmapVO> result = new ArrayList<>();
        grouped.forEach((deptId, hourMap) -> {
            String deptName = deptNameMap.getOrDefault(deptId, "未知科室");
            hourMap.forEach((hour, count) -> result.add(HeatmapVO.builder()
                    .deptName(deptName).hour(hour).count(count).build()));
        });
        result.sort(Comparator.comparing(HeatmapVO::getDeptName).thenComparing(HeatmapVO::getHour));
        return result;
    }

    @Override
    public List<DeptRadarVO> getDeptRadarData() {
        if (!canViewVisual()) {
            return Collections.emptyList();
        }
        Map<Long, String> deptNameMap = buildDeptNameMap();
        List<VisitDO> visits = visitMapper.selectList(new LambdaQueryWrapperX<>());
        List<BillDO> bills = billMapper.selectList(new LambdaQueryWrapperX<>());
        if (visits.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, BigDecimal> billByVisitId = bills.stream()
                .filter(b -> b.getVisitId() != null && b.getTotalAmount() != null)
                .collect(Collectors.toMap(BillDO::getVisitId, BillDO::getTotalAmount, (a, b) -> a));
        Map<Long, List<VisitDO>> byDept = visits.stream()
                .filter(v -> v.getDeptId() != null)
                .collect(Collectors.groupingBy(VisitDO::getDeptId));
        List<DeptRadarVO> result = new ArrayList<>();
        byDept.forEach((deptId, deptVisits) -> {
            long total = deptVisits.size();
            long cured = deptVisits.stream()
                    .filter(v -> VisitStatusEnum.COMPLETED.getStatus().equals(v.getStatus()))
                    .count();
            double cureRate = total > 0 ? (double) cured / total : 0.0;
            List<BigDecimal> fees = deptVisits.stream()
                    .map(v -> billByVisitId.get(v.getId()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            double avgFee = 0.0;
            if (!fees.isEmpty()) {
                BigDecimal sum = fees.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                avgFee = sum.divide(BigDecimal.valueOf(fees.size()), 2, RoundingMode.HALF_UP).doubleValue();
            }
            result.add(DeptRadarVO.builder()
                    .deptName(deptNameMap.getOrDefault(deptId, "未知科室"))
                    .visitCount(total)
                    .cureRate(Math.round(cureRate * 10000) / 10000.0)
                    .avgFee(avgFee)
                    .build());
        });
        result.sort(Comparator.comparing(DeptRadarVO::getVisitCount).reversed());
        return result;
    }

    @Override
    public PatientProfileRespVO getPatientProfile() {
        if (!canViewVisual()) {
            return PatientProfileRespVO.builder()
                    .ageList(Collections.emptyList())
                    .regionList(Collections.emptyList())
                    .insuranceList(Collections.emptyList())
                    .build();
        }
        List<PatientDO> patients = patientMapper.selectList(new LambdaQueryWrapperX<>());
        return PatientProfileRespVO.builder()
                .ageList(buildAgeList(patients))
                .regionList(buildRegionList(patients))
                .insuranceList(buildInsuranceList(patients))
                .build();
    }

    @Override
    public List<DiseaseSeasonalVO> getDiseaseSeasonal() {
        if (!canViewVisual()) {
            return Collections.emptyList();
        }
        List<VisitDO> visits = visitMapper.selectList(new LambdaQueryWrapperX<>());
        if (visits.isEmpty()) {
            return Collections.emptyList();
        }
        // 先按总就诊量取 TOP-N 病种，再按月拆解成多条 series（前端按 diagnosis 分线）
        Map<String, Long> diseaseTotal = visits.stream()
                .filter(v -> StringUtils.hasText(v.getDiagnosis()))
                .collect(Collectors.groupingBy(VisitDO::getDiagnosis, Collectors.counting()));
        List<String> topDiseases = diseaseTotal.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(DISEASE_TOP_N)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        Map<String, Map<Integer, Long>> grouped = visits.stream()
                .filter(v -> StringUtils.hasText(v.getDiagnosis()) && v.getVisitDate() != null
                        && topDiseases.contains(v.getDiagnosis()))
                .collect(Collectors.groupingBy(VisitDO::getDiagnosis,
                        Collectors.groupingBy(v -> v.getVisitDate().getMonthValue(), Collectors.counting())));
        List<DiseaseSeasonalVO> result = new ArrayList<>();
        grouped.forEach((disease, monthMap) -> monthMap.forEach((month, count) ->
                result.add(DiseaseSeasonalVO.builder()
                        .month(month)
                        .diagnosis(disease)
                        .count(count)
                        .build())));
        result.sort(Comparator.comparing(DiseaseSeasonalVO::getDiagnosis)
                .thenComparing(DiseaseSeasonalVO::getMonth));
        return result;
    }

    @Override
    public List<MedicineCooccurrenceVO> getMedicineCooccurrence() {
        if (!canViewVisual()) {
            return Collections.emptyList();
        }
        List<PrescriptionItemDO> items = prescriptionItemMapper.selectList(new LambdaQueryWrapperX<>());
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, String> medicineNameMap = medicineMapper.selectList(new LambdaQueryWrapperX<>()).stream()
                .collect(Collectors.toMap(MedicineDO::getId, MedicineDO::getName, (a, b) -> a));
        Map<Long, List<Long>> byPrescription = items.stream()
                .filter(i -> i.getPrescriptionId() != null && i.getMedicineId() != null)
                .collect(Collectors.groupingBy(PrescriptionItemDO::getPrescriptionId,
                        Collectors.mapping(PrescriptionItemDO::getMedicineId, Collectors.toList())));
        Map<String, Long> pairCount = new HashMap<>();
        for (List<Long> medicineIds : byPrescription.values()) {
            List<Long> unique = medicineIds.stream().distinct().sorted().collect(Collectors.toList());
            for (int i = 0; i < unique.size(); i++) {
                for (int j = i + 1; j < unique.size(); j++) {
                    String key = unique.get(i) + ":" + unique.get(j);
                    pairCount.merge(key, 1L, Long::sum);
                }
            }
        }
        List<MedicineCooccurrenceVO> result = new ArrayList<>();
        pairCount.forEach((key, count) -> {
            if (count < MIN_CO_OCCURRENCE) {
                return;
            }
            String[] parts = key.split(":");
            Long idA = Long.valueOf(parts[0]);
            Long idB = Long.valueOf(parts[1]);
            result.add(MedicineCooccurrenceVO.builder()
                    .medicineA(medicineNameMap.getOrDefault(idA, "药品#" + idA))
                    .medicineB(medicineNameMap.getOrDefault(idB, "药品#" + idB))
                    .coCount(count)
                    .build());
        });
        result.sort(Comparator.comparing(MedicineCooccurrenceVO::getCoCount).reversed());
        return result;
    }

    private boolean canViewVisual() {
        // 数据看板面向管理员与医生：管理员（未关联医生/患者档案）与医生（关联医生档案）均可查看，
        // 患者未授予 hospital:visual:query 权限码，菜单层即不可见，这里作为数据级兜底。
        return securityContext.isAdmin() || securityContext.getCurrentDoctorId() != null;
    }

    private Map<Long, String> buildDeptNameMap() {
        return departmentMapper.selectList(new LambdaQueryWrapperX<>()).stream()
                .collect(Collectors.toMap(DepartmentDO::getId, DepartmentDO::getDeptName, (a, b) -> a));
    }

    private List<PatientAgeVO> buildAgeList(List<PatientDO> patients) {
        Map<String, Long> counter = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        for (PatientDO patient : patients) {
            if (patient.getBirthDate() == null) {
                continue;
            }
            int age = Period.between(patient.getBirthDate(), today).getYears();
            String ageGroup = resolveAgeGroup(age);
            String gender = StringUtils.hasText(patient.getGender()) ? patient.getGender() : "未知";
            String key = gender + "|" + ageGroup;
            counter.merge(key, 1L, Long::sum);
        }
        List<PatientAgeVO> result = new ArrayList<>();
        counter.forEach((key, count) -> {
            String[] parts = key.split("\\|", 2);
            result.add(PatientAgeVO.builder().gender(parts[0]).ageGroup(parts[1]).count(count).build());
        });
        return result;
    }

    private List<PatientRegionVO> buildRegionList(List<PatientDO> patients) {
        Map<String, Long> counter = new HashMap<>();
        for (PatientDO patient : patients) {
            // 直接按省级行政区聚合（patient.region 由生成器统一写入，如「北京市」「河北省」）
            if (!StringUtils.hasText(patient.getRegion())) {
                continue;
            }
            String region = patient.getRegion().trim();
            counter.merge(region, 1L, Long::sum);
        }
        return counter.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(REGION_TOP_N)
                .map(e -> PatientRegionVO.builder().region(e.getKey()).count(e.getValue()).build())
                .collect(Collectors.toList());
    }

    private List<PatientInsuranceVO> buildInsuranceList(List<PatientDO> patients) {
        // 按医保类型分组统计：职工医保 / 居民医保 / 新农合 / 商业保险 / 自费 / 未知
        Map<String, Long> counter = new LinkedHashMap<>();
        String[] order = {"职工医保", "居民医保", "新农合", "商业保险", "自费", "未知"};
        for (String t : order) {
            counter.put(t, 0L);
        }
        for (PatientDO patient : patients) {
            String t = StringUtils.hasText(patient.getInsuranceType()) ? patient.getInsuranceType() : "未知";
            counter.merge(t, 1L, Long::sum);
        }
        List<PatientInsuranceVO> result = new ArrayList<>();
        counter.forEach((t, c) -> {
            if (c > 0) {
                result.add(PatientInsuranceVO.builder().insuranceType(t).count(c).build());
            }
        });
        return result;
    }

    private static String resolveAgeGroup(int age) {
        if (age <= 17) {
            return "0-17";
        }
        if (age <= 35) {
            return "18-35";
        }
        if (age <= 50) {
            return "36-50";
        }
        if (age <= 65) {
            return "51-65";
        }
        return "65+";
    }
}
