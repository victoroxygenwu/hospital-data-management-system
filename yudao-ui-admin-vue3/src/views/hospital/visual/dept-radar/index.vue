<template>
  <div class="p-20px">
    <el-card>
      <template #header><span>科室综合雷达图</span></template>
      <div ref="chartRef" v-loading="loading" style="width:100%;height:520px;"></div>
      <el-empty v-if="!loading && !hasData" description="暂无科室数据" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getDeptRadar } from '@/api/hospital/visual'

defineOptions({ name: 'HospitalVisualDeptRadar' })

const chartRef = ref<HTMLElement>()
const loading = ref(false)
const hasData = ref(false)
let chart: echarts.ECharts | null = null

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDeptRadar()
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
  const maxVisit = Math.max(...list.map(d => d.visitCount), 1)
  const maxFee = Math.max(...list.map(d => d.avgFee || 0), 1)
  chart.setOption({
    tooltip: {},
    legend: { data: list.map(d => d.deptName), bottom: 0 },
    radar: {
      indicator: [
        { name: '接诊量', max: maxVisit },
        { name: '治愈率', max: 1 },
        { name: '平均费用', max: maxFee }
      ]
    },
    series: [{
      type: 'radar',
      data: list.map(d => ({
        name: d.deptName,
        value: [d.visitCount, d.cureRate, d.avgFee || 0]
      }))
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
