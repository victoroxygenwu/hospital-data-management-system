<template>
  <div class="p-20px">
    <el-card>
      <template #header><span>就诊量月度趋势</span></template>
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
  const months = list.map(d => `${d.month}月`)
  const counts = list.map(d => d.count)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: months },
    yAxis: { type: 'value', name: '就诊量' },
    series: [{
      name: '就诊量', type: 'line', smooth: true, data: counts,
      areaStyle: { color: 'rgba(84,112,198,0.2)' },
      itemStyle: { color: '#5470c6' }
    }]
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
