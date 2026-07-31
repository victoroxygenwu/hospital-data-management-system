<template>
  <div class="p-20px">
    <h3 class="mb-15px">数据统计分析</h3>

    <!-- 就诊趋势（按月聚合，避免每日过密） -->
    <el-card class="mb-15px">
      <template #header><span>就诊趋势统计（按月）</span></template>
      <div ref="trendChartRef" v-loading="trendLoading" class="stat-chart"></div>
      <el-empty v-if="!trendLoading && monthlyTrendData.length === 0" description="暂无就诊趋势数据" />
    </el-card>

    <!-- 床位使用率 -->
    <el-card class="mb-15px">
      <template #header>
        <span>床位使用率统计</span>
        <span class="header-tip">（&gt;80% 红 / &gt;50% 黄 / 其余绿）</span>
      </template>
      <div ref="usageChartRef" v-loading="usageLoading" class="stat-chart"></div>
      <el-empty v-if="!usageLoading && wardUsageData.length === 0" description="暂无床位使用率数据" />
    </el-card>

    <!-- 药品库存（明细表，内部滚动） -->
    <el-card>
      <template #header>
        <span>药品库存统计</span>
        <span class="header-tip">（共 {{ medicineStockData.length }} 种，预警标红）</span>
      </template>
      <el-table
        v-loading="stockLoading"
        :data="medicineStockData"
        border
        stripe
        max-height="420"
        class="medicine-table"
      >
        <el-table-column label="药品名称" prop="name" width="150" />
        <el-table-column label="规格" prop="specification" width="120" />
        <el-table-column label="单位" prop="unit" width="60" />
        <el-table-column label="库存量" prop="stock" width="80" sortable />
        <el-table-column label="有效期" prop="expiryDate" width="120" />
        <el-table-column label="库存预警" width="100">
          <template #default="{ row }">
            <el-tag :type="row.stockWarning ? 'danger' : 'success'">{{ row.stockWarning ? '库存不足' : '正常' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!stockLoading && medicineStockData.length === 0" description="暂无药品库存数据" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getVisitTrend, getWardUsage, getMedicineStock } from '@/api/hospital/stats'

defineOptions({ name: 'HospitalStats' })

const monthlyTrendData = ref<{ month: string; count: number }[]>([])
const wardUsageData = ref<any[]>([])
const medicineStockData = ref<any[]>([])
const trendLoading = ref(false)
const usageLoading = ref(false)
const stockLoading = ref(false)

const trendChartRef = ref<HTMLElement>()
const usageChartRef = ref<HTMLElement>()
let trendChart: echarts.ECharts | null = null
let usageChart: echarts.ECharts | null = null
const resizeObservers: ResizeObserver[] = []

const parseRate = (v: any): number => {
  if (v == null) return 0
  const s = String(v).replace('%', '').trim()
  const n = parseFloat(s)
  return isNaN(n) ? 0 : n
}

// 按 YYYY-MM 聚合每日就诊量，避免横轴过密
const aggregateMonthly = (list: any[]) => {
  const map = new Map<string, number>()
  for (const i of list) {
    const month = String(i.date || '').slice(0, 7)
    if (!month) continue
    map.set(month, (map.get(month) || 0) + (Number(i.count) || 0))
  }
  return Array.from(map.entries()).map(([month, count]) => ({ month, count }))
}

const loadData = async () => {
  trendLoading.value = true
  try {
    const rawTrend = (await getVisitTrend()) || []
    monthlyTrendData.value = aggregateMonthly(rawTrend)
    await nextTick()
    renderTrendChart()
  } catch { monthlyTrendData.value = [] }
  finally { trendLoading.value = false }

  usageLoading.value = true
  try {
    wardUsageData.value = (await getWardUsage()) || []
    await nextTick()
    renderUsageChart()
  } catch { wardUsageData.value = [] }
  finally { usageLoading.value = false }

  stockLoading.value = true
  try {
    medicineStockData.value = (await getMedicineStock()) || []
  } catch { medicineStockData.value = [] }
  finally { stockLoading.value = false }
}

const renderTrendChart = () => {
  if (!trendChartRef.value || monthlyTrendData.value.length === 0) return
  if (!trendChart) trendChart = echarts.init(trendChartRef.value)
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 50, right: 20, top: 30, bottom: 40 },
    xAxis: {
      type: 'category',
      data: monthlyTrendData.value.map(i => i.month),
      axisLabel: { interval: 'auto' }
    },
    yAxis: { type: 'value', name: '就诊人次' },
    series: [{
      type: 'bar',
      data: monthlyTrendData.value.map(i => i.count),
      itemStyle: { color: '#409eff', borderRadius: [4, 4, 0, 0] }
    }]
  }, true)
}

const renderUsageChart = () => {
  if (!usageChartRef.value || wardUsageData.value.length === 0) return
  if (!usageChart) usageChart = echarts.init(usageChartRef.value)
  const rates = wardUsageData.value.map(i => parseRate(i.usageRate))
  usageChart.setOption({
    tooltip: { trigger: 'axis', formatter: (p: any) => `${p[0].name}<br/>使用率：${p[0].value}%` },
    grid: { left: 50, right: 20, top: 30, bottom: 40 },
    xAxis: {
      type: 'category',
      data: wardUsageData.value.map(i => i.wardNo),
      axisLabel: { rotate: 30, interval: 'auto' }
    },
    yAxis: { type: 'value', name: '使用率(%)', max: 100 },
    series: [{
      type: 'bar',
      data: rates.map(r => ({
        value: r,
        itemStyle: { color: r > 80 ? '#f56c6c' : r > 50 ? '#e6a23c' : '#67c23a', borderRadius: [4, 4, 0, 0] }
      }))
    }]
  }, true)
}

const observeResize = (el: HTMLElement | undefined, chart: echarts.ECharts | null) => {
  if (!el) return
  const ro = new ResizeObserver(() => chart?.resize())
  ro.observe(el)
  resizeObservers.push(ro)
}

onMounted(async () => {
  await loadData()
  observeResize(trendChartRef.value, trendChart)
  observeResize(usageChartRef.value, usageChart)
})

onBeforeUnmount(() => {
  resizeObservers.forEach(ro => ro.disconnect())
  trendChart?.dispose()
  usageChart?.dispose()
})
</script>

<style scoped>
.stat-chart {
  width: 100%;
  height: 360px;
}
.header-tip {
  font-size: 12px;
  color: #909399;
  margin-left: 8px;
  font-weight: normal;
}
.medicine-table {
  margin-top: 4px;
}
</style>
