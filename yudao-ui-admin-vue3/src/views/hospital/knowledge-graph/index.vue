<template>
  <div class="p-20px">
    <el-form :inline="true" class="mb-15px" @submit.prevent>
      <el-form-item label="聚焦疾病">
        <el-select
          v-model="focusDiseaseId"
          filterable
          clearable
          placeholder="选择疾病以聚焦其关联子图"
          style="width: 280px"
          @change="onFocusChange"
        >
          <el-option v-for="d in diseaseOptions" :key="d.id" :label="d.name" :value="d.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="renderFull" :disabled="!fullData">显示全图</el-button>
        <el-button @click="zoomReset">重置视图</el-button>
      </el-form-item>
      <el-form-item class="ml-10px">
        <span class="text-gray-500 text-13px">
          共 {{ nodeCount }} 个节点 / {{ linkCount }} 条边 · 滚轮缩放 · 拖空白处平移整图 · 拖单个节点调布局 · 悬停高亮关联
        </span>
      </el-form-item>
    </el-form>

    <div
      ref="chartRef"
      v-loading="loading"
      style="width: 100%; height: 480px; border: 1px solid #eee; border-radius: 8px; background: #fafbfc; cursor: grab"
    ></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getFullNetwork, getDiseaseNetwork } from '@/api/hospital/knowledge'
import { getDiseasePage } from '@/api/hospital/disease'

defineOptions({ name: 'HospitalKnowledgeGraph' })

const chartRef = ref<HTMLElement>()
const loading = ref(false)
const diseaseOptions = ref<any[]>([])
const focusDiseaseId = ref<number>()
const fullData = ref<any>(null)
const currentData = ref<any>(null)
const nodeCount = ref(0)
const linkCount = ref(0)
let chart: echarts.ECharts | null = null
let eventsBound = false
let resizeObserver: ResizeObserver | null = null

// 自定义 roam 状态（缩放与平移）。
// 交互监听绑在容器 DOM 上（整块画布含空白都 100% 触发），保证“空白不可操作”彻底解决。
const roamZoom = ref(1)
const roamCenter = ref<[number, number]>([0, 0])
let dragging = false
let lastX = 0
let lastY = 0
// 关键点：用 ECharts 系列 mousedown 事件先行判断“是否按在节点上”。
// 该事件在图形元素（节点）上一定触发，空白处不触发 → 据此决定是否放行 ECharts 单节点拖拽，避免与背景平移冲突。
let nodePressed = false

const toKey = (id: number | string) => String(id)
const CATEGORIES = [{ name: '疾病' }, { name: '症状' }, { name: '药品' }]
const PALETTE = ['#ee6666', '#5470c6', '#91cc75']

const baseSeries = () => ({
  type: 'graph' as const,
  layout: 'force' as const,
  roam: false,           // 关闭自带 roam，改用容器 DOM 事件（整块可操作）
  draggable: true,       // 单节点可独立拖拽（体验有趣，保留）
  categories: CATEGORIES,
  label: {
    show: false,
    position: 'right' as const,
    fontSize: 13,
    color: '#333',
    fontWeight: 500
  },
  emphasis: {
    focus: 'adjacency' as const,
    label: { show: true },
    lineStyle: { width: 3 }
  },
  force: { repulsion: 320, edgeLength: [120, 220], gravity: 0.04, friction: 0.16 },
  lineStyle: { color: '#bbb', curveness: 0.12, width: 1 },
  data: [] as any[],
  links: [] as any[]
})

const applyOption = (data: any) => {
  if (!chartRef.value) return
  if (!chart) chart = echarts.init(chartRef.value)
  currentData.value = data
  const series = baseSeries()
  series.data = (data.nodes || []).map((n: any) => ({
    id: toKey(n.id),
    name: n.name,
    category: n.category,
    symbolSize: n.category === 0 ? 30 : n.category === 1 ? 18 : 20,
    label: { show: n.category === 0 },
    itemStyle:
      n.category === 0
        ? { color: PALETTE[0] }
        : n.category === 1
          ? { color: PALETTE[1] }
          : { color: PALETTE[2] }
  }))
  series.links = (data.links || []).map((l: any) => ({
    source: toKey(l.source),
    target: toKey(l.target),
    label: { show: false, formatter: l.label, fontSize: 12 },
    lineStyle: { color: '#ccc' }
  }))
  chart.setOption(
    {
      color: PALETTE, // 图例颜色与节点一致（疾病红/症状蓝/药品绿）
      tooltip: { formatter: (p: any) => p.data?.name || '' },
      legend: {
        data: CATEGORIES.map(c => c.name),
        top: 10,
        left: 'center',
        itemWidth: 14,
        itemHeight: 14,
        textStyle: { fontSize: 13, color: '#333' }
      },
      series: [series]
    },
    true
  )
  const rect = chartRef.value.getBoundingClientRect()
  roamZoom.value = 1
  roamCenter.value = [rect.width / 2, rect.height / 2]
  chart.setOption({ series: [{ zoom: 1, center: roamCenter.value }] })
  chart.resize()
  nodeCount.value = series.data.length
  linkCount.value = series.links.length
  if (!eventsBound) {
    bindEvents()
    eventsBound = true
  }
}

// 仅更新缩放/平移（不重建数据，保留力导向布局与单节点拖拽结果）
const applyRoam = () => {
  if (!chart) return
  chart.setOption({ series: [{ zoom: roamZoom.value, center: roamCenter.value }] })
}

const bindEvents = () => {
  if (!chartRef.value || !chart) return
  const el = chartRef.value
  // ECharts 系列事件：按在节点上 → 标记 nodePressed，容器 handler 据此放行单节点拖拽
  chart.on('mousedown', (params: any) => {
    nodePressed = !!params && params.dataType === 'node'
  })
  el.addEventListener('mousedown', onMouseDown)
  el.addEventListener('wheel', onWheel, { passive: false })
  window.addEventListener('mousemove', onMouseMove)
  window.addEventListener('mouseup', onMouseUp)
}

const clamp = (v: number, lo: number, hi: number) => Math.max(lo, Math.min(hi, v))

const onMouseDown = (e: MouseEvent) => {
  if (e.button !== 0) return
  // 按在节点上：交给 ECharts 拖节点，不启动背景平移
  if (nodePressed) return
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
  // 让光标下的点保持不动（以光标为中心缩放）
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

const renderFull = () => {
  if (!fullData.value) return
  focusDiseaseId.value = undefined
  applyOption(fullData.value)
}

const loadFull = async () => {
  loading.value = true
  try {
    const data = await getFullNetwork()
    fullData.value = data
    applyOption(data)
  } finally {
    loading.value = false
  }
}

const onFocusChange = async (id?: number) => {
  if (!id) {
    renderFull()
    return
  }
  loading.value = true
  try {
    const data = await getDiseaseNetwork(id)
    applyOption(data)
  } finally {
    loading.value = false
  }
}

const zoomReset = () => {
  if (currentData.value) applyOption(currentData.value)
}

const handleResize = () => chart?.resize()

onMounted(async () => {
  diseaseOptions.value = (await getDiseasePage({ pageNo: 1, pageSize: 200 })).list || []
  await nextTick()
  await loadFull()
  setTimeout(() => chart?.resize(), 300)
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
