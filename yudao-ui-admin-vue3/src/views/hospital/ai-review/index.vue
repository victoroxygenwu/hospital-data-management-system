<template>
  <div class="p-20px">
    <el-card>
      <template #header><span>AI 处方审核</span></template>
      <el-form :inline="true">
        <el-form-item label="选择处方">
          <el-select v-model="prescriptionId" filterable placeholder="请选择处方" style="width:320px">
            <el-option v-for="p in prescriptionOptions" :key="p.id" :label="'处方#' + p.id + ' (就诊' + p.visitId + ')'" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" v-hasPermi="['hospital:ai:prescription-review']" :loading="loading" @click="handleReview">
            开始审核
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-skeleton v-if="loading" animated class="mt-20px" :rows="4" />

    <el-card v-if="result && !loading" class="mt-20px">
      <template #header>
        <span>审核报告</span>
        <el-tag v-if="result.offline" type="warning" class="ml-10px">离线模式</el-tag>
      </template>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="风险等级">
          <el-tag :type="riskTagType(result.riskLevel)">{{ result.riskLevel }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核建议">{{ result.advice || '-' }}</el-descriptions-item>
      </el-descriptions>
      <h4 class="mt-15px mb-10px">药品相互作用</h4>
      <el-table :data="result.interactions || []" border stripe>
        <el-table-column label="药品A" prop="medicineA" />
        <el-table-column label="药品B" prop="medicineB" />
        <el-table-column label="说明" prop="note" min-width="200" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { prescriptionReview } from '@/api/hospital/knowledge'
import { getPrescriptionPage } from '@/api/hospital/prescription'

defineOptions({ name: 'HospitalAiReview' })

const prescriptionId = ref<number>()
const prescriptionOptions = ref<any[]>([])
const loading = ref(false)
const result = ref<any>(null)

const riskTagType = (level: string) => {
  if (level === 'HIGH') return 'danger'
  if (level === 'MEDIUM') return 'warning'
  return 'success'
}

const handleReview = async () => {
  if (!prescriptionId.value) {
    ElMessage.warning('请选择处方')
    return
  }
  loading.value = true
  result.value = null
  try {
    result.value = await prescriptionReview({ prescriptionId: prescriptionId.value })
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  const res = await getPrescriptionPage({ pageNo: 1, pageSize: 100 })
  prescriptionOptions.value = res.list || []
})
</script>
