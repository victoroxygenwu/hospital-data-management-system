package cn.iocoder.yudao.module.hospital.service.stats;

import cn.iocoder.yudao.module.hospital.controller.admin.stats.vo.VisitTrendVO;
import cn.iocoder.yudao.module.hospital.controller.admin.stats.vo.WardUsageVO;
import cn.iocoder.yudao.module.hospital.controller.admin.stats.vo.MedicineStockVO;

import java.util.List;

public interface StatsService {
    /** 就诊趋势统计：按日期统计就诊量 */
    List<VisitTrendVO> getVisitTrend(String startDate, String endDate);
    /** 床位使用率统计 */
    List<WardUsageVO> getWardUsage();
    /** 药品库存统计 */
    List<MedicineStockVO> getMedicineStock();
}
