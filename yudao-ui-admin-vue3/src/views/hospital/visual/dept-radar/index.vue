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
  // 量纲归一化：接诊量/平均费用量纲差异巨大，按各自最大值归一到 0~100，避免雷达被单一轴主导；
  // tooltip 仍展示真实数值。
  const top = list.slice(0, 8)
  const maxVisit = Math.max(...top.map(d => d.visitCount), 1)
  const maxFee = Math.max(...top.map(d => d.avgFee || 0), 1)
  const palette = ['#5470c6', '#ee6666', '#fac858', '#91cc75', '#73c0de', '#3ba272', '#fc8452', '#9a60b4']
  chart.setOption({
    tooltip: {
      formatter: (p: any) => {
        const r = p.data.real
        return `${p.name}<br/>接诊量：${r[0]}<br/>治愈率：${(r[1] * 100).toFixed(1)}%<br/>平均费用：¥${r[2].toFixed(2)}`
      }
    },
    legend: { data: top.map(d => d.deptName), bottom: 0, type: 'scroll' },
    radar: {
      indicator: [
        { name: '接诊量', max: 100 },
        { name: '治愈率', max: 100 },
        { name: '平均费用', max: 100 }
      ],
      radius: '62%'
    },
    series: [{
      type: 'radar',
      data: top.map((d, idx) => ({
        name: d.deptName,
        value: [
          Math.round((d.visitCount / maxVisit) * 100),
          Math.round(d.cureRate * 100),
          Math.round(((d.avgFee || 0) / maxFee) * 100)
        ],
        real: [d.visitCount, d.cureRate, d.avgFee || 0],
        lineStyle: { color: palette[idx % palette.length], width: 2 },
        itemStyle: { color: palette[idx % palette.length] },
        areaStyle: { color: palette[idx % palette.length], opacity: 0.08 }
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
