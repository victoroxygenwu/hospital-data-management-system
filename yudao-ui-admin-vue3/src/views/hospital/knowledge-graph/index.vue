<template>
  <div class="p-20px">
    <el-form :inline="true" class="mb-15px">
      <el-form-item label="选择疾病">
        <el-select v-model="selectedDiseaseId" filterable placeholder="请选择疾病" style="width:280px" @change="loadNetwork">
          <el-option v-for="d in diseaseOptions" :key="d.id" :label="d.name" :value="d.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadNetwork" :disabled="!selectedDiseaseId">刷新图谱</el-button>
      </el-form-item>
    </el-form>
    <div ref="chartRef" v-loading="loading" style="width:100%;height:600px;border:1px solid #eee;border-radius:8px;"></div>
    <el-empty v-if="!loading && !hasData" description="请选择疾病查看关联图谱" class="mt-20px" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getDiseaseNetwork } from '@/api/hospital/knowledge'
import { getDiseasePage } from '@/api/hospital/disease'

defineOptions({ name: 'HospitalKnowledgeGraph' })

const chartRef = ref<HTMLElement>()
const loading = ref(false)
const hasData = ref(false)
const selectedDiseaseId = ref<number>()
const diseaseOptions = ref<any[]>([])
let chart: echarts.ECharts | null = null

const loadNetwork = async () => {
  if (!selectedDiseaseId.value) return
  loading.value = true
  try {
    const data = await getDiseaseNetwork(selectedDiseaseId.value)
    hasData.value = !!(data?.nodes?.length)
    await nextTick()
    renderChart(data)
  } finally { loading.value = false }
}

const renderChart = (data: any) => {
  if (!chartRef.value) return
  if (!chart) chart = echarts.init(chartRef.value)
  const categories = [{ name: '疾病' }, { name: '症状' }, { name: '药品' }]
  chart.setOption({
    tooltip: {},
    legend: [{ data: categories.map(c => c.name) }],
    series: [{
      type: 'graph',
      layout: 'force',
      roam: true,
      label: { show: true, position: 'right' },
      force: { repulsion: 200, edgeLength: 120 },
      categories,
      data: (data.nodes || []).map((n: any) => ({
        id: String(n.id),
        name: n.name,
        category: n.category,
        symbolSize: n.category === 0 ? 50 : 30
      })),
      links: (data.links || []).map((l: any) => ({
        source: String(l.source),
        target: String(l.target),
        label: { show: true, formatter: l.label }
      }))
    }]
  }, true)
}

const handleResize = () => chart?.resize()

onMounted(async () => {
  diseaseOptions.value = (await getDiseasePage({ pageNo: 1, pageSize: 100 })).list || []
  if (diseaseOptions.value.length) {
    selectedDiseaseId.value = diseaseOptions.value[0].id
    loadNetwork()
  }
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
})
</script>
