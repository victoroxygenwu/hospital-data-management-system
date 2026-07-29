package cn.iocoder.yudao.module.hospital.service.stats;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.stats.vo.VisitTrendVO;
import cn.iocoder.yudao.module.hospital.controller.admin.stats.vo.WardUsageVO;
import cn.iocoder.yudao.module.hospital.controller.admin.stats.vo.MedicineStockVO;
import cn.iocoder.yudao.module.hospital.controller.admin.stats.vo.TodaySummaryVO;
import cn.iocoder.yudao.module.hospital.controller.admin.stats.vo.DeptRankVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.MedicineDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.VisitDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.WardDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.BillDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.DepartmentDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.MedicineMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.VisitMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.WardMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.BillMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.DepartmentMapper;
import cn.iocoder.yudao.module.hospital.enums.BillStatusEnum;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hospital.enums.ErrorCodeConstants.HOSPITAL_DATA_ACCESS_DENIED;

/**
 * 统计 Service 实现类
 */
@Service
public class StatsServiceImpl implements StatsService {

    private static final int STOCK_WARNING_THRESHOLD = 10;

    @Resource
    private HospitalSecurityContext securityContext; // 角色权限上下文

    @Resource
    private VisitMapper visitMapper; // 就诊数据访问
    @Resource
    private WardMapper wardMapper; // 病房数据访问
    @Resource
    private MedicineMapper medicineMapper; // 药品数据访问
    @Resource
    private BillMapper billMapper; // 账单数据访问
    @Resource
    private DepartmentMapper departmentMapper; // 科室数据访问

    /**
     * 就诊趋势统计（仅管理员可查看）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 就诊趋势列表
     */
    @Override
    public List<VisitTrendVO> getVisitTrend(String startDate, String endDate) {
        if (!securityContext.isAdmin()) {
            throw exception(HOSPITAL_DATA_ACCESS_DENIED);
        }
        List<VisitDO> visits = visitMapper.selectList(new LambdaQueryWrapperX<VisitDO>()
                .ge(startDate != null, VisitDO::getVisitDate, startDate + " 00:00:00")
                .le(endDate != null, VisitDO::getVisitDate, endDate + " 23:59:59")
                .orderByAsc(VisitDO::getVisitDate));
        Map<String, Long> grouped = visits.stream()
                .filter(v -> v.getVisitDate() != null)
                .collect(Collectors.groupingBy(v -> v.getVisitDate().toLocalDate().toString(), Collectors.counting()));
        return grouped.entrySet().stream()
                .map(e -> VisitTrendVO.builder().date(e.getKey()).count(e.getValue()).build())
                .sorted(Comparator.comparing(VisitTrendVO::getDate))
                .collect(Collectors.toList());
    }

    /**
     * 病房使用率统计（仅管理员可查看）
     * @return 病房使用情况列表
     */
    @Override
    public List<WardUsageVO> getWardUsage() {
        if (!securityContext.isAdmin()) {
            throw exception(HOSPITAL_DATA_ACCESS_DENIED);
        }
        List<WardDO> wards = wardMapper.selectList(new LambdaQueryWrapperX<>());
        return wards.stream().map(ward -> WardUsageVO.builder()
                .wardId(ward.getId())
                .wardNo(ward.getWardNo())
                .capacity(ward.getCapacity())
                .usedBeds(ward.getUsedBeds())
                .usageRate(ward.getCapacity() != null && ward.getCapacity() > 0
                        && ward.getUsedBeds() != null
                        ? String.format("%.1f%%", ward.getUsedBeds() * 100.0 / ward.getCapacity()) : "0%")
                .build()).collect(Collectors.toList());
    }

    /**
     * 药品库存统计（仅管理员可查看，库存低于阈值标记预警）
     * @return 药品库存列表
     */
    @Override
    public List<MedicineStockVO> getMedicineStock() {
        if (!securityContext.isAdmin()) {
            throw exception(HOSPITAL_DATA_ACCESS_DENIED);
        }
        List<MedicineDO> medicines = medicineMapper.selectList(
                new LambdaQueryWrapperX<MedicineDO>().orderByAsc(MedicineDO::getStock));
        return medicines.stream().map(med -> MedicineStockVO.builder()
                .id(med.getId())
                .name(med.getName())
                .specification(med.getSpecification())
                .unit(med.getUnit())
                .stock(med.getStock())
                .expiryDate(Objects.toString(med.getExpiryDate(), ""))
                .stockWarning(med.getStock() != null && med.getStock() < STOCK_WARNING_THRESHOLD)
                .build()).collect(Collectors.toList());
    }

    @Override
    public TodaySummaryVO getTodaySummary() {
        if (!securityContext.isAdmin()) {
            throw exception(HOSPITAL_DATA_ACCESS_DENIED);
        }
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);
        List<VisitDO> todayVisits = visitMapper.selectList(new LambdaQueryWrapperX<VisitDO>()
                .between(VisitDO::getVisitDate, start, end));
        List<BillDO> bills = billMapper.selectList(new LambdaQueryWrapperX<>());
        BigDecimal todayIncome = bills.stream()
                .filter(b -> BillStatusEnum.PAID.getStatus().equals(b.getStatus()))
                .filter(b -> b.getPayTime() != null && !b.getPayTime().isBefore(start) && !b.getPayTime().isAfter(end))
                .map(BillDO::getPayAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long pendingBills = bills.stream()
                .filter(b -> BillStatusEnum.UNPAID.getStatus().equals(b.getStatus()))
                .count();
        List<WardDO> wards = wardMapper.selectList(new LambdaQueryWrapperX<>());
        long inpatientCount = wards.stream()
                .map(WardDO::getUsedBeds)
                .filter(Objects::nonNull)
                .mapToLong(Integer::longValue)
                .sum();
        return TodaySummaryVO.builder()
                .outpatientCount((long) todayVisits.size())
                .todayIncome(todayIncome)
                .inpatientCount(inpatientCount)
                .pendingBills(pendingBills)
                .build();
    }

    @Override
    public List<DeptRankVO> getDeptRank() {
        if (!securityContext.isAdmin()) {
            throw exception(HOSPITAL_DATA_ACCESS_DENIED);
        }
        Map<Long, String> deptNameMap = departmentMapper.selectList(new LambdaQueryWrapperX<>()).stream()
                .collect(Collectors.toMap(DepartmentDO::getId, DepartmentDO::getDeptName, (a, b) -> a));
        List<VisitDO> visits = visitMapper.selectList(new LambdaQueryWrapperX<>());
        return visits.stream()
                .filter(v -> v.getDeptId() != null)
                .collect(Collectors.groupingBy(VisitDO::getDeptId, Collectors.counting()))
                .entrySet().stream()
                .map(e -> DeptRankVO.builder()
                        .deptName(deptNameMap.getOrDefault(e.getKey(), "未知科室"))
                        .visitCount(e.getValue())
                        .build())
                .sorted(Comparator.comparing(DeptRankVO::getVisitCount).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }
}
