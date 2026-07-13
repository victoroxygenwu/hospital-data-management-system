package cn.iocoder.yudao.module.hospital.service.stats;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hospital.controller.admin.stats.vo.VisitTrendVO;
import cn.iocoder.yudao.module.hospital.controller.admin.stats.vo.WardUsageVO;
import cn.iocoder.yudao.module.hospital.controller.admin.stats.vo.MedicineStockVO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.MedicineDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.VisitDO;
import cn.iocoder.yudao.module.hospital.dal.dataobject.WardDO;
import cn.iocoder.yudao.module.hospital.dal.mysql.MedicineMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.VisitMapper;
import cn.iocoder.yudao.module.hospital.dal.mysql.WardMapper;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StatsServiceImpl implements StatsService {

    private static final int STOCK_WARNING_THRESHOLD = 10;

    @Resource
    private HospitalSecurityContext securityContext;

    @Resource
    private VisitMapper visitMapper;
    @Resource
    private WardMapper wardMapper;
    @Resource
    private MedicineMapper medicineMapper;

    @Override
    public List<VisitTrendVO> getVisitTrend(String startDate, String endDate) {
        if (!securityContext.isAdmin()) {
            return Collections.emptyList();
        }
        List<VisitDO> visits = visitMapper.selectList(new LambdaQueryWrapperX<VisitDO>()
                .ge(startDate != null, VisitDO::getVisitDate, startDate + " 00:00:00")
                .le(endDate != null, VisitDO::getVisitDate, endDate + " 23:59:59")
                .orderByAsc(VisitDO::getVisitDate));
        Map<String, Long> grouped = visits.stream()
                .collect(Collectors.groupingBy(v -> v.getVisitDate().toLocalDate().toString(), Collectors.counting()));
        return grouped.entrySet().stream()
                .map(e -> VisitTrendVO.builder().date(e.getKey()).count(e.getValue()).build())
                .sorted(Comparator.comparing(VisitTrendVO::getDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<WardUsageVO> getWardUsage() {
        if (!securityContext.isAdmin()) {
            return Collections.emptyList();
        }
        List<WardDO> wards = wardMapper.selectList(new LambdaQueryWrapperX<>());
        return wards.stream().map(ward -> WardUsageVO.builder()
                .wardId(ward.getId())
                .wardNo(ward.getWardNo())
                .capacity(ward.getCapacity())
                .usedBeds(ward.getUsedBeds())
                .usageRate(ward.getCapacity() > 0
                        ? String.format("%.1f%%", ward.getUsedBeds() * 100.0 / ward.getCapacity()) : "0%")
                .build()).collect(Collectors.toList());
    }

    @Override
    public List<MedicineStockVO> getMedicineStock() {
        if (!securityContext.isAdmin()) {
            return Collections.emptyList();
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
}
