<template>
  <div class="dashboard-container">
    <div class="welcome-header">
      <h2>院长驾驶舱</h2>
      <p>医院运营数据实时监控</p>
    </div>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- 概览 -->
      <el-tab-pane label="概览" name="overview">
        <el-row :gutter="20" class="stat-cards">
          <el-col :span="6">
            <el-card shadow="hover">
              <div class="stat-card">
                <div class="stat-icon"><el-icon :size="40"><User /></el-icon></div>
                <div class="stat-info">
                  <div class="stat-value">{{ stats.outpatientCount }}</div>
                  <div class="stat-label">今日门诊量</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <div class="stat-card">
                <div class="stat-icon"><el-icon :size="40"><OfficeBuilding /></el-icon></div>
                <div class="stat-info">
                  <div class="stat-value">{{ stats.inpatientCount }}</div>
                  <div class="stat-label">在院人数</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <div class="stat-card">
                <div class="stat-icon"><el-icon :size="40"><Document /></el-icon></div>
                <div class="stat-info">
                  <div class="stat-value">{{ stats.pendingBills }}</div>
                  <div class="stat-label">待处理账单</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <div class="stat-card">
                <div class="stat-icon"><el-icon :size="40"><Money /></el-icon></div>
                <div class="stat-info">
                  <div class="stat-value">¥{{ stats.todayIncome }}</div>
                  <div class="stat-label">今日收入</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-row :gutter="20" class="charts-row">
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header><span>各科室接诊量占比</span></template>
              <div ref="pieChartRef" class="chart-container"></div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header><span>近7天门诊趋势</span></template>
              <div ref="lineChartRef" class="chart-container"></div>
            </el-card>
          </el-col>
        </el-row>

        <el-row :gutter="20" class="charts-row">
          <el-col :span="24">
            <el-card class="chart-card">
              <template #header><span>病房床位使用率</span></template>
              <div ref="barChartRef" class="chart-container"></div>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 数据看板 -->
      <el-tab-pane label="数据看板" name="visual">
        <el-row :gutter="20">
          <el-col :span="12" class="mb-20px">
            <el-card><template #header><span>接诊热力图</span></template><HeatmapPage /></el-card>
          </el-col>
          <el-col :span="12" class="mb-20px">
            <el-card><template #header><span>科室雷达图</span></template><DeptRadarPage /></el-card>
          </el-col>
          <el-col :span="12" class="mb-20px">
            <el-card><template #header><span>患者画像</span></template><PatientProfilePage /></el-card>
          </el-col>
          <el-col :span="12" class="mb-20px">
            <el-card><template #header><span>疾病趋势</span></template><DiseaseSeasonalPage /></el-card>
          </el-col>
          <el-col :span="24">
            <el-card><template #header><span>药品关联</span></template><MedicineCooccurrencePage /></el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 知识图谱 -->
      <el-tab-pane label="知识图谱" name="knowledge">
        <KnowledgeGraphPage />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { User, OfficeBuilding, Money, Document } from '@element-plus/icons-vue'
import { getVisitTrend, getWardUsage, getTodaySummary, getDeptRank } from '@/api/hospital/stats'
import HeatmapPage from '@/views/hospital/visual/heatmap/index.vue'
import DeptRadarPage from '@/views/hospital/visual/dept-radar/index.vue'
import PatientProfilePage from '@/views/hospital/visual/patient-profile/index.vue'
import DiseaseSeasonalPage from '@/views/hospital/visual/disease-seasonal/index.vue'
import MedicineCooccurrencePage from '@/views/hospital/visual/medicine-cooccurrence/index.vue'
import KnowledgeGraphPage from '@/views/hospital/knowledge-graph/index.vue'

defineOptions({ name: 'HospitalDashboard' })

const activeTab = ref('overview')
const stats = ref({ outpatientCount: 0, inpatientCount: 0, pendingBills: 0, todayIncome: 0 })
const pieChartRef = ref<HTMLElement>()
const lineChartRef = ref<HTMLElement>()
const barChartRef = ref<HTMLElement>()
let pieChart: echarts.ECharts | null = null
let lineChart: echarts.ECharts | null = null
let barChart: echarts.ECharts | null = null
let pollTimer: ReturnType<typeof setInterval> | null = null

const deptVisitData = ref<any[]>([])
const weeklyTrendData = ref({ dates: [] as string[], visits: [] as number[] })
const bedUsageData = ref<any[]>([])

const loadSummary = async () => {
  try {
    const summary = await getTodaySummary()
    if (summary) {
      stats.value.outpatientCount = summary.outpatientCount || 0
      stats.value.inpatientCount = summary.inpatientCount || 0
      stats.value.pendingBills = summary.pendingBills || 0
      stats.value.todayIncome = summary.todayIncome || 0
    }
  } catch { /* ignore */ }
}

const loadOverviewCharts = async () => {
  try {
    const end = new Date()
    const start = new Date()
    start.setDate(end.getDate() - 6)
    const fmt = (d: Date) => d.toISOString().slice(0, 10)
    const trendRes = await getVisitTrend({ startDate: fmt(start), endDate: fmt(end) })
    if (trendRes?.length) {
      weeklyTrendData.value = {
        dates: trendRes.map((item: any) => item.date),
        visits: trendRes.map((item: any) => item.count)
      }
    }
    const deptRes = await getDeptRank()
    if (deptRes?.length) {
      deptVisitData.value = deptRes.map((item: any) => ({ name: item.deptName, value: item.visitCount }))
    }
    const wardRes = await getWardUsage()
    if (wardRes?.length) {
      bedUsageData.value = wardRes.map((item: any) => ({
        name: item.wardNo || item.wardName || item.name,
        rate: parseFloat(String(item.usageRate || '0').replace('%', '')) || 0
      }))
    }
  } catch { /* ignore */ }
  await nextTick()
  initPieChart()
  initLineChart()
  initBarChart()
}

const initPieChart = () => {
  if (!pieChartRef.value) return
  if (!pieChart) pieChart = echarts.init(pieChartRef.value)
  pieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
    legend: { orient: 'vertical', left: 'left' },
    series: [{ type: 'pie', radius: '55%', data: deptVisitData.value,
      label: { show: true, formatter: '{b}: {d}%' } }]
  }, true)
}

const initLineChart = () => {
  if (!lineChartRef.value) return
  if (!lineChart) lineChart = echarts.init(lineChartRef.value)
  lineChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: weeklyTrendData.value.dates },
    yAxis: { type: 'value', name: '门诊量' },
    series: [{ type: 'line', data: weeklyTrendData.value.visits, smooth: true, areaStyle: {} }]
  }, true)
}

const initBarChart = () => {
  if (!barChartRef.value) return
  if (!barChart) barChart = echarts.init(barChartRef.value)
  barChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: bedUsageData.value.map(i => i.name) },
    yAxis: { type: 'value', name: '使用率(%)', max: 100 },
    series: [{ type: 'bar', data: bedUsageData.value.map(i => i.rate) }]
  }, true)
}

const handleTabChange = () => {
  setTimeout(() => {
    pieChart?.resize()
    lineChart?.resize()
    barChart?.resize()
    window.dispatchEvent(new Event('resize'))
  }, 200)
}

const handleResize = () => {
  pieChart?.resize()
  lineChart?.resize()
  barChart?.resize()
}

onMounted(async () => {
  await loadSummary()
  await loadOverviewCharts()
  pollTimer = setInterval(loadSummary, 30000)
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  if (pollTimer) clearInterval(pollTimer)
  window.removeEventListener('resize', handleResize)
  pieChart?.dispose()
  lineChart?.dispose()
  barChart?.dispose()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 100px);
}
.welcome-header { margin-bottom: 24px; }
.welcome-header h2 { font-size: 24px; color: #303133; margin-bottom: 8px; }
.welcome-header p { color: #909399; font-size: 14px; }
.stat-cards { margin-bottom: 20px; }
.stat-card { display: flex; align-items: center; justify-content: space-between; }
.stat-icon {
  color: #409eff; background-color: #ecf5ff;
  width: 60px; height: 60px; display: flex; align-items: center; justify-content: center; border-radius: 12px;
}
.stat-info { text-align: right; }
.stat-value { font-size: 28px; font-weight: bold; color: #303133; }
.stat-label { font-size: 14px; color: #909399; margin-top: 4px; }
.charts-row { margin-bottom: 20px; }
.chart-card { height: 400px; }
.chart-container { width: 100%; height: 320px; }
.mb-20px { margin-bottom: 20px; }
</style>
