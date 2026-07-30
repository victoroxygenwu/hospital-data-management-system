<template>
  <div class="p-20px">
    <el-card>
      <template #header><span>重点病种季节性趋势（TOP 8）</span></template>
      <div ref="chartRef" v-loading="loading" style="width:100%;height:480px;"></div>
      <el-empty v-if="!loading && !hasData" description="暂无趋势数据" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getDiseaseSeasonal } from '@/api/hospital/visual'

defineOptions({ name: 'HospitalVisualDiseaseSeasonal' })

const chartRef = ref<HTMLElement>()
const loading = ref(false)
const hasData = ref(false)
let chart: echarts.ECharts | null = null

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDiseaseSeasonal()
    hasData.value = (res || []).length > 0
    await nextTick()
    renderChart(res || [])
  } finally {
    loading.value = false
  }
}

const renderChart = (list: any[]) => {
  if (!chartRef.value || !list.length) return
  if (!chart) chart = echarts.init(chartRef.value)
  // 月份轴：取数据中出现的月份，按数值排序（1..12），跨年数据按自然月对齐
  const monthSet = [...new Set(list.map(d => d.month))].sort((a, b) => a - b)
  const months = monthSet.map(m => `${m}月`)
  // 按病种拆分为多条 series
  const diseaseMap: Record<string, Record<number, number>> = {}
  list.forEach(d => {
    if (!diseaseMap[d.diagnosis]) diseaseMap[d.diagnosis] = {}
    diseaseMap[d.diagnosis][d.month] = d.count
  })
  const palette = ['#5470c6', '#ee6666', '#fac858', '#91cc75', '#73c0de', '#3ba272', '#fc8452', '#9a60b4']
  const series = Object.keys(diseaseMap).map((dis, idx) => ({
    name: dis,
    type: 'line',
    smooth: true,
    showSymbol: false,
    data: monthSet.map(m => diseaseMap[dis][m] || 0),
    itemStyle: { color: palette[idx % palette.length] }
  }))
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { type: 'scroll', bottom: '0%' },
    grid: { left: '3%', right: '4%', top: '8%', bottom: '12%', containLabel: true },
    xAxis: { type: 'category', data: months, boundaryGap: false },
    yAxis: { type: 'value', name: '就诊量' },
    series
  }, true)
}

const handleResize = () => chart?.resize()

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
})
</script>
