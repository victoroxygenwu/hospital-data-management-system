<template>
  <div class="p-20px">
    <el-card>
      <template #header><span>药品联合使用共现矩阵</span></template>
      <div ref="chartRef" v-loading="loading" style="width:100%;height:520px;"></div>
      <el-empty v-if="!loading && !hasData" description="暂无高频共现药品对" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getMedicineCooccurrence } from '@/api/hospital/visual'

defineOptions({ name: 'HospitalVisualMedicineCooccurrence' })

const chartRef = ref<HTMLElement>()
const loading = ref(false)
const hasData = ref(false)
let chart: echarts.ECharts | null = null

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMedicineCooccurrence()
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
  const medicines = [...new Set(list.flatMap(d => [d.medicineA, d.medicineB]))]
  const data: any[] = []
  list.forEach(d => {
    data.push([medicines.indexOf(d.medicineA), medicines.indexOf(d.medicineB), d.coCount])
    data.push([medicines.indexOf(d.medicineB), medicines.indexOf(d.medicineA), d.coCount])
  })
  const maxCount = Math.max(...list.map(d => d.coCount), 1)
  chart.setOption({
    tooltip: { position: 'top' },
    grid: { height: '70%', top: '5%' },
    xAxis: { type: 'category', data: medicines, axisLabel: { rotate: 30, fontSize: 10 } },
    yAxis: { type: 'category', data: medicines, axisLabel: { fontSize: 10 } },
    visualMap: { min: 0, max: maxCount, calculable: true, orient: 'horizontal', left: 'center', bottom: '0%' },
    series: [{ type: 'heatmap', data, label: { show: true } }]
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
