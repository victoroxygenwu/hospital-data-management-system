package cn.iocoder.yudao.module.hospital.service.stats;

import cn.iocoder.yudao.module.hospital.controller.admin.stats.vo.VisitTrendVO;
import cn.iocoder.yudao.module.hospital.controller.admin.stats.vo.WardUsageVO;
import cn.iocoder.yudao.module.hospital.controller.admin.stats.vo.MedicineStockVO;
import cn.iocoder.yudao.module.hospital.dal.mysql.*;
import cn.iocoder.yudao.module.hospital.dal.dataobject.*;
import cn.iocoder.yudao.module.hospital.framework.security.HospitalSecurityContext;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsServiceImpl implements StatsService {

    @Resource
    private HospitalSecurityContext securityContext;

    @Resource
    private VisitMapper visitMapper;
    @Resource
    private WardMapper wardMapper;
    @Resource
    private BedMapper bedMapper;
    @Resource
    private MedicineMapper medicineMapper;

    @Override
    public List<VisitTrendVO> getVisitTrend(String startDate, String endDate) {
        if (!securityContext.isAdmin()) {
            return Collections.emptyList();
        }
        List<VisitDO> visits = visitMapper.selectList(new QueryWrapper<VisitDO>()
                .ge(startDate != null, "visit_date", startDate + " 00:00:00")
                .le(endDate != null, "visit_date", endDate + " 23:59:59")
                .orderByAsc("visit_date"));
        Map<String, Long> grouped = visits.stream()
                .collect(Collectors.groupingBy(v -> v.getVisitDate().toLocalDate().toString(), Collectors.counting()));
        List<VisitTrendVO> result = grouped.entrySet().stream()
                .map(e -> VisitTrendVO.builder().date(e.getKey()).count(e.getValue()).build())
                .sorted(Comparator.comparing(VisitTrendVO::getDate))
                .collect(Collectors.toList());
        return result;
    }

    @Override
    public List<WardUsageVO> getWardUsage() {
        if (!securityContext.isAdmin()) {
            return Collections.emptyList();
        }
        List<WardDO> wards = wardMapper.selectList(new QueryWrapper<>());
        List<WardUsageVO> result = new ArrayList<>();
        for (WardDO ward : wards) {
            result.add(WardUsageVO.builder()
                    .wardId(ward.getId())
                    .wardNo(ward.getWardNo())
                    .capacity(ward.getCapacity())
                    .usedBeds(ward.getUsedBeds())
                    .usageRate(ward.getCapacity() > 0
                            ? String.format("%.1f%%", ward.getUsedBeds() * 100.0 / ward.getCapacity()) : "0%")
                    .build());
        }
        return result;
    }

    @Override
    public List<MedicineStockVO> getMedicineStock() {
        if (!securityContext.isAdmin()) {
            return Collections.emptyList();
        }
        List<MedicineDO> medicines = medicineMapper.selectList(new QueryWrapper<MedicineDO>()
                .orderByAsc("stock"));
        List<MedicineStockVO> result = new ArrayList<>();
        for (MedicineDO med : medicines) {
            result.add(MedicineStockVO.builder()
                    .id(med.getId())
                    .name(med.getName())
                    .specification(med.getSpecification())
                    .unit(med.getUnit())
                    .stock(med.getStock())
                    .expiryDate(med.getExpiryDate() != null ? med.getExpiryDate().toString() : "")
                    .stockWarning(med.getStock() != null && med.getStock() < 10)
                    .build());
        }
        return result;
    }
}
