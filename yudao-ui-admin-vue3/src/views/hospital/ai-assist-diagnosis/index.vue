<template>
  <div class="p-20px">
    <el-card>
      <template #header><span>AI 辅助诊断</span></template>
      <el-input v-model="symptomDescription" type="textarea" :rows="4" placeholder="请描述症状，如：头痛三天，伴发热、咽痛、咳嗽" />
      <div class="mt-15px">
        <el-button type="primary" v-hasPermi="['hospital:ai:assist-diagnosis']" :loading="loading" @click="handleDiagnose">
          开始诊断
        </el-button>
      </div>
    </el-card>

    <el-skeleton v-if="loading" animated class="mt-20px" :rows="5" />

    <el-card v-if="result && !loading" class="mt-20px">
      <template #header>
        <span>诊断结果</span>
        <el-tag v-if="result.offline" type="warning" class="ml-10px">离线模式（知识图谱）</el-tag>
      </template>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="提取症状">{{ (result.extractedSymptoms || []).join('、') || '无' }}</el-descriptions-item>
        <el-descriptions-item label="参考科室">{{ result.referenceDept || '-' }}</el-descriptions-item>
        <el-descriptions-item label="紧急程度">{{ result.urgency || '-' }}</el-descriptions-item>
        <el-descriptions-item label="建议">{{ result.advice || '-' }}</el-descriptions-item>
      </el-descriptions>
      <h4 class="mt-15px mb-10px">匹配疾病</h4>
      <el-table :data="result.matchedDiseases || []" border stripe>
        <el-table-column label="疾病名称" prop="name" />
        <el-table-column label="ICD编码" prop="icdCode" width="120" />
        <el-table-column label="匹配数" prop="matchCount" width="90" />
        <el-table-column label="总症状数" prop="totalSymptoms" width="100" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { assistDiagnosis } from '@/api/hospital/knowledge'

defineOptions({ name: 'HospitalAiAssistDiagnosis' })

const symptomDescription = ref('')
const loading = ref(false)
const result = ref<any>(null)

const handleDiagnose = async () => {
  if (!symptomDescription.value.trim()) {
    ElMessage.warning('请输入症状描述')
    return
  }
  loading.value = true
  result.value = null
  try {
    result.value = await assistDiagnosis({ symptomDescription: symptomDescription.value })
  } finally {
    loading.value = false
  }
}
</script>
