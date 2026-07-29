<template>
  <div class="p-20px">
    <el-card>
      <template #header><span>科室 × 时段接诊热力图</span></template>
      <div ref="chartRef" v-loading="loading" style="width:100%;height:520px;"></div>
      <el-empty v-if="!loading && !hasData" description="暂无接诊数据" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getHeatmap } from '@/api/hospital/visual'

defineOptions({ name: 'HospitalVisualHeatmap' })

const chartRef = ref<HTMLElement>()
const loading = ref(false)
const hasData = ref(false)
let chart: echarts.ECharts | null = null

const loadData = async () => {
  loading.value = true
  try {
    const res = await getHeatmap()
    const list = (res || []).filter((d: any) => d.hour >= 8 && d.hour <= 19)
    hasData.value = list.length > 0
    await nextTick()
    renderChart(list)
  } finally {
    loading.value = false
  }
}

const renderChart = (list: any[]) => {
  if (!chartRef.value) return
  if (!chart) chart = echarts.init(chartRef.value)
  const depts = [...new Set(list.map((d: any) => d.deptName))]
  const hours = Array.from({ length: 12 }, (_, i) => `${i + 8}:00`)
  const data = list.map((d: any) => [depts.indexOf(d.deptName), d.hour - 8, d.count])
  const maxCount = Math.max(...list.map((d: any) => d.count), 1)
  chart.setOption({
    tooltip: { position: 'top', formatter: (p: any) => `${depts[p.data[0]]} ${hours[p.data[1]]}: ${p.data[2]}人` },
    grid: { height: '60%', top: '10%' },
    xAxis: { type: 'category', data: depts, splitArea: { show: true } },
    yAxis: { type: 'category', data: hours, splitArea: { show: true } },
    visualMap: {
      min: 0, max: maxCount, calculable: true, orient: 'horizontal', left: 'center', bottom: '5%',
      inRange: { color: ['#e8f5e9', '#fff176', '#ff8f00', '#d32f2f'] }
    },
    series: [{ type: 'heatmap', data, label: { show: true }, emphasis: { itemStyle: { shadowBlur: 10 } } }]
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
