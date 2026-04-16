package cn.iocoder.yudao.module.hospital.service.stats;

import cn.iocoder.yudao.module.hospital.dal.mysql.*;
import cn.iocoder.yudao.module.hospital.dal.dataobject.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsServiceImpl implements StatsService {

    @Resource
    private VisitMapper visitMapper;
    @Resource
    private WardMapper wardMapper;
    @Resource
    private BedMapper bedMapper;
    @Resource
    private MedicineMapper medicineMapper;

    @Override
    public List<Map<String, Object>> getVisitTrend(String startDate, String endDate) {
        // 按就诊日期分组统计数量
        List<VisitDO> visits = visitMapper.selectList(new QueryWrapper<VisitDO>()
                .ge(startDate != null, "visit_date", startDate + " 00:00:00")
                .le(endDate != null, "visit_date", endDate + " 23:59:59")
                .orderByAsc("visit_date"));
        // 按日期分组
        Map<String, Long> grouped = visits.stream()
                .collect(Collectors.groupingBy(v -> v.getVisitDate().toLocalDate().toString(), Collectors.counting()));
        List<Map<String, Object>> result = new ArrayList<>();
        grouped.forEach((date, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("date", date);
            item.put("count", count);
            result.add(item);
        });
        result.sort(Comparator.comparing(m -> m.get("date").toString()));
        return result;
    }

    @Override
    public List<Map<String, Object>> getWardUsage() {
        List<WardDO> wards = wardMapper.selectList(new QueryWrapper<>());
        List<Map<String, Object>> result = new ArrayList<>();
        for (WardDO ward : wards) {
            Map<String, Object> item = new HashMap<>();
            item.put("wardId", ward.getId());
            item.put("wardNo", ward.getWardNo());
            item.put("capacity", ward.getCapacity());
            item.put("usedBeds", ward.getUsedBeds());
            item.put("usageRate", ward.getCapacity() > 0
                    ? String.format("%.1f%%", ward.getUsedBeds() * 100.0 / ward.getCapacity()) : "0%");
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getMedicineStock() {
        List<MedicineDO> medicines = medicineMapper.selectList(new QueryWrapper<MedicineDO>()
                .orderByAsc("stock"));
        List<Map<String, Object>> result = new ArrayList<>();
        for (MedicineDO med : medicines) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", med.getId());
            item.put("name", med.getName());
            item.put("specification", med.getSpecification());
            item.put("unit", med.getUnit());
            item.put("stock", med.getStock());
            item.put("expiryDate", med.getExpiryDate() != null ? med.getExpiryDate().toString() : "");
            item.put("stockWarning", med.getStock() != null && med.getStock() < 10);
            result.add(item);
        }
        return result;
    }
}
