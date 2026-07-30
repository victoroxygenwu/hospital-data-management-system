<template>
  <div class="p-20px">
    <el-card>
      <template #header>
        <div class="flex justify-between items-center flex-wrap gap-10px">
          <span>药品联合使用关联网络</span>
          <div class="flex items-center gap-16px flex-wrap">
            <span class="text-13px text-gray-500">最小共现次数</span>
            <el-slider v-model="minCo" :min="1" :max="maxCo" :step="1" style="width:160px"
                       @input="renderChart" />
            <span class="text-13px text-gray-500 w-28px">{{ minCo }}</span>
            <el-divider direction="vertical" />
            <span class="text-13px text-gray-500">展示药品数 (TopN)</span>
            <el-slider v-model="topN" :min="5" :max="maxNodes" :step="1" style="width:160px"
                       @input="renderChart" />
            <span class="text-13px text-gray-500 w-28px">{{ topN }}</span>
          </div>
        </div>
      </template>
      <div ref="chartRef" v-loading="loading" style="width:100%;height:480px;cursor:grab;"></div>
      <el-empty v-if="!loading && !hasData" description="暂无高频共现药品对" />
      <div v-if="!loading && hasData" class="text-12px text-gray-400 mt-8px">
        节点颜色越深、越大代表该药关联强度越高；连线越粗、越深代表两种药联合使用次数越多。拖单个节点可调整布局，拖空白处平移整图，滚轮缩放。当前阈值≥{{ minCo }}、展示关联强度最高的前 {{ topN }} 种药品。
      </div>
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
const rawList = ref<any[]>([])
const maxCo = ref(30)
const maxNodes = ref(80)
const minCo = ref(3)
const topN = ref(30)
let chart: echarts.ECharts | null = null
let eventsBound = false
let resizeObserver: ResizeObserver | null = null

// 自定义 roam 状态（缩放与平移），保证整块画布（含空白）都可操作
const roamZoom = ref(1)
const roamCenter = ref<[number, number]>([0, 0])
let dragging = false
let lastX = 0
let lastY = 0
// 用 ECharts 系列 mousedown 先行判断“是否按在节点上”，据此放行单节点拖拽、不与背景平移冲突
let nodePressed = false

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMedicineCooccurrence()
    rawList.value = res || []
    if (rawList.value.length) {
      maxCo.value = Math.max(...rawList.value.map(d => d.coCount), 1)
      const uniq = new Set(rawList.value.flatMap(d => [d.medicineA, d.medicineB]))
      maxNodes.value = uniq.size
      minCo.value = Math.min(3, maxCo.value)
      topN.value = Math.min(30, maxNodes.value)
    }
    hasData.value = rawList.value.length > 0
    await nextTick()
    renderChart()
  } finally {
    loading.value = false
  }
}

const lerp = (a: number, b: number, t: number) => Math.round(a + (b - a) * t)
const NODE_LOW = [186, 212, 255]
const NODE_HIGH = [26, 79, 180]
const nodeColor = (t: number) =>
  `rgb(${lerp(NODE_LOW[0], NODE_HIGH[0], t)},${lerp(NODE_LOW[1], NODE_HIGH[1], t)},${lerp(NODE_LOW[2], NODE_HIGH[2], t)})`

const buildGraph = (list: any[], threshold: number, topN: number) => {
  const edgesRaw = list.filter(d => d.coCount >= threshold)
  const degree: Record<string, number> = {}
  edgesRaw.forEach(d => {
    degree[d.medicineA] = (degree[d.medicineA] || 0) + d.coCount
    degree[d.medicineB] = (degree[d.medicineB] || 0) + d.coCount
  })
  const topMeds = Object.keys(degree)
    .sort((a, b) => degree[b] - degree[a])
    .slice(0, topN)
  const topSet = new Set(topMeds)
  const minDeg = Math.min(...topMeds.map(m => degree[m]), 1)
  const maxDeg = Math.max(...topMeds.map(m => degree[m]), 1)
  const nodes = topMeds.map(name => {
    const t = maxDeg === minDeg ? 1 : (degree[name] - minDeg) / (maxDeg - minDeg)
    return {
      name,
      symbolSize: 12 + Math.sqrt(degree[name]) * 1.4,
      value: degree[name],
      itemStyle: {
        color: nodeColor(t),
        borderColor: 'rgba(255,255,255,0.85)',
        borderWidth: 1.5,
        shadowBlur: 8,
        shadowColor: 'rgba(26,79,180,0.35)'
      }
    }
  })
  const coVals = edgesRaw.map(d => d.coCount)
  const minCo2 = Math.min(...coVals, 1)
  const maxCo2 = Math.max(...coVals, 1)
  const links = edgesRaw
    .filter(d => topSet.has(d.medicineA) && topSet.has(d.medicineB))
    .map(d => {
      const t = maxCo2 === minCo2 ? 1 : (d.coCount - minCo2) / (maxCo2 - minCo2)
      return {
        source: d.medicineA,
        target: d.medicineB,
        value: d.coCount,
        lineStyle: {
          width: 1 + t * 5,
          color: `rgba(120,140,200,${0.18 + t * 0.55})`,
          curveness: 0.18,
          opacity: 0.9
        }
      }
    })
  return { nodes, links }
}

const applyRoam = () => {
  if (!chart) return
  chart.setOption({ series: [{ zoom: roamZoom.value, center: roamCenter.value }] })
}

const renderChart = () => {
  if (!chartRef.value) return
  if (!chart) chart = echarts.init(chartRef.value)
  const { nodes, links } = buildGraph(rawList.value, minCo.value, topN.value)
  if (!nodes.length) {
    chart.clear()
    return
  }
  chart.setOption({
    tooltip: {
      formatter: (p: any) => p.dataType === 'edge'
        ? `${p.data.source} ↔ ${p.data.target}<br/>联合使用 ${p.data.value} 次`
        : `<b>${p.data.name}</b><br/>关联强度 ${p.data.value}`
    },
    series: [{
      type: 'graph',
      layout: 'force',
      roam: false,        // 关闭原生 roam，改用容器 DOM 事件（整块可操作）
      draggable: true,    // 单节点可独立拖拽
      force: { repulsion: 420, edgeLength: [90, 200], gravity: 0.035, friction: 0.14 },
      label: {
        show: true,
        fontSize: 13,
        fontWeight: 500,
        color: '#222',
        position: 'right',
        formatter: '{b}',
        textShadowColor: '#fff',
        textShadowBlur: 4
      },
      emphasis: {
        focus: 'adjacency',
        label: { show: true, fontWeight: 'bold' },
        lineStyle: { width: 6, color: '#5470c6' }
      },
      lineStyle: { curveness: 0.18 },
      data: nodes,
      links
    }]
  }, true)
  chart.resize()
  if (!eventsBound) {
    const rect = chartRef.value!.getBoundingClientRect()
    roamCenter.value = [rect.width / 2, rect.height / 2]
    bindEvents()
    eventsBound = true
  }
  // 重新应用自定义缩放/平移状态（notMerge 后会被重置，这里恢复）
  applyRoam()
}

const clamp = (v: number, lo: number, hi: number) => Math.max(lo, Math.min(hi, v))

const bindEvents = () => {
  if (!chartRef.value || !chart) return
  const el = chartRef.value
  chart.on('mousedown', (params: any) => {
    nodePressed = !!params && params.dataType === 'node'
  })
  el.addEventListener('mousedown', onMouseDown)
  el.addEventListener('wheel', onWheel, { passive: false })
  window.addEventListener('mousemove', onMouseMove)
  window.addEventListener('mouseup', onMouseUp)
}

const onMouseDown = (e: MouseEvent) => {
  if (e.button !== 0) return
  if (nodePressed) return // 按在节点上 → 交给 ECharts 拖节点
  dragging = true
  const rect = chartRef.value!.getBoundingClientRect()
  lastX = e.clientX - rect.left
  lastY = e.clientY - rect.top
  if (chartRef.value) chartRef.value.style.cursor = 'grabbing'
  e.preventDefault()
}

const onWheel = (e: WheelEvent) => {
  e.preventDefault()
  const rect = chartRef.value!.getBoundingClientRect()
  const sx = e.clientX - rect.left
  const sy = e.clientY - rect.top
  const w = rect.width
  const h = rect.height
  const dcx = w / 2
  const dcy = h / 2
  const oldZoom = roamZoom.value
  const factor = e.deltaY < 0 ? 1.12 : 1 / 1.12
  const newZoom = clamp(oldZoom * factor, 0.3, 6)
  if (newZoom === oldZoom) return
  const lx = (sx - roamCenter.value[0]) / oldZoom + dcx
  const ly = (sy - roamCenter.value[1]) / oldZoom + dcy
  let cx = sx - (lx - dcx) * newZoom
  let cy = sy - (ly - dcy) * newZoom
  cx = clamp(cx, -0.4 * w, 1.4 * w)
  cy = clamp(cy, -0.4 * h, 1.4 * h)
  roamZoom.value = newZoom
  roamCenter.value = [cx, cy]
  applyRoam()
}

const onMouseMove = (e: MouseEvent) => {
  if (!dragging || !chartRef.value) return
  const rect = chartRef.value.getBoundingClientRect()
  const x = e.clientX - rect.left
  const y = e.clientY - rect.top
  const dx = x - lastX
  const dy = y - lastY
  lastX = x
  lastY = y
  const w = rect.width
  const h = rect.height
  let cx = roamCenter.value[0] - dx / roamZoom.value
  let cy = roamCenter.value[1] - dy / roamZoom.value
  cx = clamp(cx, -0.4 * w, 1.4 * w)
  cy = clamp(cy, -0.4 * h, 1.4 * h)
  roamCenter.value = [cx, cy]
  applyRoam()
}

const onMouseUp = () => {
  dragging = false
  nodePressed = false
  if (chartRef.value) chartRef.value.style.cursor = 'grab'
}

const handleResize = () => chart?.resize()

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  resizeObserver?.disconnect()
  resizeObserver = null
  if (chartRef.value) {
    chartRef.value.removeEventListener('mousedown', onMouseDown)
    chartRef.value.removeEventListener('wheel', onWheel)
  }
  window.removeEventListener('mousemove', onMouseMove)
  window.removeEventListener('mouseup', onMouseUp)
  chart?.dispose()
})
</script>
