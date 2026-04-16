package cn.iocoder.yudao.module.hospital.service.stats;

import java.util.List;
import java.util.Map;

public interface StatsService {
    /** 就诊趋势统计：按日期统计就诊量 */
    List<Map<String, Object>> getVisitTrend(String startDate, String endDate);
    /** 床位使用率统计 */
    List<Map<String, Object>> getWardUsage();
    /** 药品库存统计 */
    List<Map<String, Object>> getMedicineStock();
}
