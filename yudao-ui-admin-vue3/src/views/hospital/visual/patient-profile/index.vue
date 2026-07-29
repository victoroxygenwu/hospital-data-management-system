<template>
  <div class="p-20px">
    <el-row :gutter="20" v-loading="loading">
      <el-col :span="12">
        <el-card>
          <template #header><span>年龄金字塔</span></template>
          <div ref="ageChartRef" style="width:100%;height:400px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header><span>医保占比</span></template>
          <div ref="insuranceChartRef" style="width:100%;height:400px;"></div>
        </el-card>
      </el-col>
      <el-col :span="24" class="mt-20px">
        <el-card>
          <template #header><span>地区分布 TOP10</span></template>
          <div ref="regionChartRef" style="width:100%;height:360px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getPatientProfile } from '@/api/hospital/visual'

defineOptions({ name: 'HospitalVisualPatientProfile' })

const loading = ref(false)
const ageChartRef = ref<HTMLElement>()
const insuranceChartRef = ref<HTMLElement>()
const regionChartRef = ref<HTMLElement>()
let ageChart: echarts.ECharts | null = null
let insuranceChart: echarts.ECharts | null = null
let regionChart: echarts.ECharts | null = null

const ageGroups = ['0-17', '18-35', '36-50', '51-65', '65+']

const loadData = async () => {
  loading.value = true
  try {
    const res = await getPatientProfile()
    await nextTick()
    renderAgeChart(res?.ageList || [])
    renderInsuranceChart(res?.insuranceList || [])
    renderRegionChart(res?.regionList || [])
  } finally {
    loading.value = false
  }
}

const renderAgeChart = (list: any[]) => {
  if (!ageChartRef.value) return
  if (!ageChart) ageChart = echarts.init(ageChartRef.value)
  const male = ageGroups.map(g => {
    const item = list.find(d => d.gender === '男' && d.ageGroup === g)
    return item ? -item.count : 0
  })
  const female = ageGroups.map(g => {
    const item = list.find(d => d.gender === '女' && d.ageGroup === g)
    return item ? item.count : 0
  })
  ageChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    legend: { data: ['男', '女'] },
    grid: { left: '3%', right: '4%', containLabel: true },
    xAxis: { type: 'value', axisLabel: { formatter: (v: number) => Math.abs(v) } },
    yAxis: { type: 'category', data: ageGroups },
    series: [
      { name: '男', type: 'bar', stack: 'total', data: male, itemStyle: { color: '#5470c6' } },
      { name: '女', type: 'bar', stack: 'total', data: female, itemStyle: { color: '#ee6666' } }
    ]
  }, true)
}

const renderInsuranceChart = (list: any[]) => {
  if (!insuranceChartRef.value) return
  if (!insuranceChart) insuranceChart = echarts.init(insuranceChartRef.value)
  insuranceChart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie', radius: '60%',
      data: list.map(d => ({ name: d.hasInsurance ? '有医保' : '无医保', value: d.count }))
    }]
  }, true)
}

const renderRegionChart = (list: any[]) => {
  if (!regionChartRef.value) return
  if (!regionChart) regionChart = echarts.init(regionChartRef.value)
  regionChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: list.map(d => d.region) },
    yAxis: { type: 'value', name: '人数' },
    series: [{ type: 'bar', data: list.map(d => d.count), itemStyle: { color: '#3ba272' } }]
  }, true)
}

const handleResize = () => {
  ageChart?.resize()
  insuranceChart?.resize()
  regionChart?.resize()
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  ageChart?.dispose()
  insuranceChart?.dispose()
  regionChart?.dispose()
})
</script>
