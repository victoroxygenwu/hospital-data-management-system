<template>
  <div class="p-20px">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="疾病-症状关联" name="symptom">
        <div class="mb-15px">
          <el-button type="primary" v-hasPermi="['hospital:disease-symptom:create']" @click="openSymptomForm">新增关联</el-button>
        </div>
        <el-table v-loading="symptomLoading" :data="symptomList" border stripe>
          <el-table-column label="ID" prop="id" width="70" />
          <el-table-column label="疾病" prop="diseaseName" width="160" />
          <el-table-column label="症状" prop="symptomName" width="120" />
          <el-table-column label="强度" width="100">
            <template #default="{ row }">{{ strengthLabel(row.strength) }}</template>
          </el-table-column>
          <el-table-column label="依据" prop="reference" min-width="150" show-overflow-tooltip />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button link type="danger" v-hasPermi="['hospital:disease-symptom:delete']" @click="deleteSymptom(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination class="mt-15px" v-model:current-page="symptomQuery.pageNo" v-model:page-size="symptomQuery.pageSize"
          :total="symptomTotal" layout="total, prev, pager, next" @current-change="loadSymptomList" />
      </el-tab-pane>

      <el-tab-pane label="疾病-药品关联" name="medicine">
        <div class="mb-15px">
          <el-button type="primary" v-hasPermi="['hospital:disease-medicine:create']" @click="openMedicineForm">新增关联</el-button>
        </div>
        <el-table v-loading="medicineLoading" :data="medicineList" border stripe>
          <el-table-column label="ID" prop="id" width="70" />
          <el-table-column label="疾病" prop="diseaseName" width="160" />
          <el-table-column label="药品" prop="medicineName" min-width="150" />
          <el-table-column label="用药类型" width="100">
            <template #default="{ row }">{{ usageLabel(row.usageType) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button link type="danger" v-hasPermi="['hospital:disease-medicine:delete']" @click="deleteMedicine(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination class="mt-15px" v-model:current-page="medicineQuery.pageNo" v-model:page-size="medicineQuery.pageSize"
          :total="medicineTotal" layout="total, prev, pager, next" @current-change="loadMedicineList" />
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="symptomDialogVisible" title="新增疾病-症状关联" width="480px">
      <el-form :model="symptomForm" label-width="90px">
        <el-form-item label="疾病" required>
          <el-select v-model="symptomForm.diseaseId" filterable placeholder="选择疾病" style="width:100%">
            <el-option v-for="d in diseaseOptions" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="症状" required>
          <el-select v-model="symptomForm.symptomId" filterable placeholder="选择症状" style="width:100%">
            <el-option v-for="s in symptomOptions" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联强度">
          <el-select v-model="symptomForm.strength" style="width:100%">
            <el-option label="主要" :value="1" /><el-option label="次要" :value="2" /><el-option label="偶见" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="依据"><el-input v-model="symptomForm.reference" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="symptomDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitSymptomForm">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="medicineDialogVisible" title="新增疾病-药品关联" width="480px">
      <el-form :model="medicineForm" label-width="90px">
        <el-form-item label="疾病" required>
          <el-select v-model="medicineForm.diseaseId" filterable placeholder="选择疾病" style="width:100%">
            <el-option v-for="d in diseaseOptions" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="药品" required>
          <el-select v-model="medicineForm.medicineId" filterable placeholder="选择药品" style="width:100%">
            <el-option v-for="m in medicineOptions" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="用药类型">
          <el-select v-model="medicineForm.usageType" style="width:100%">
            <el-option label="首选" :value="1" /><el-option label="备选" :value="2" /><el-option label="辅助" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="medicineDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitMedicineForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getDiseaseSymptomPage, createDiseaseSymptom, deleteDiseaseSymptom,
  getDiseaseMedicinePage, createDiseaseMedicine, deleteDiseaseMedicine
} from '@/api/hospital/knowledge'
import { getDiseasePage } from '@/api/hospital/disease'
import { getSymptomPage } from '@/api/hospital/symptom'
import { getMedicinePage } from '@/api/hospital/medicine'

defineOptions({ name: 'HospitalKgRelations' })

const activeTab = ref('symptom')
const strengthLabel = (v: number) => ({ 1: '主要', 2: '次要', 3: '偶见' }[v] || v)
const usageLabel = (v: number) => ({ 1: '首选', 2: '备选', 3: '辅助' }[v] || v)

const diseaseOptions = ref<any[]>([])
const symptomOptions = ref<any[]>([])
const medicineOptions = ref<any[]>([])

const symptomLoading = ref(false)
const symptomList = ref<any[]>([])
const symptomTotal = ref(0)
const symptomQuery = reactive({ pageNo: 1, pageSize: 10 })
const symptomDialogVisible = ref(false)
const symptomForm = reactive<any>({ diseaseId: undefined, symptomId: undefined, strength: 1, reference: '' })

const medicineLoading = ref(false)
const medicineList = ref<any[]>([])
const medicineTotal = ref(0)
const medicineQuery = reactive({ pageNo: 1, pageSize: 10 })
const medicineDialogVisible = ref(false)
const medicineForm = reactive<any>({ diseaseId: undefined, medicineId: undefined, usageType: 1 })

const loadSymptomList = async () => {
  symptomLoading.value = true
  try {
    const res = await getDiseaseSymptomPage(symptomQuery)
    symptomList.value = res.list || []
    symptomTotal.value = res.total || 0
  } finally { symptomLoading.value = false }
}

const loadMedicineList = async () => {
  medicineLoading.value = true
  try {
    const res = await getDiseaseMedicinePage(medicineQuery)
    medicineList.value = res.list || []
    medicineTotal.value = res.total || 0
  } finally { medicineLoading.value = false }
}

const loadOptions = async () => {
  diseaseOptions.value = (await getDiseasePage({ pageNo: 1, pageSize: 100 })).list || []
  symptomOptions.value = (await getSymptomPage({ pageNo: 1, pageSize: 100 })).list || []
  medicineOptions.value = (await getMedicinePage({ pageNo: 1, pageSize: 100 })).list || []
}

const openSymptomForm = () => { symptomDialogVisible.value = true }
const submitSymptomForm = async () => {
  if (!symptomForm.diseaseId || !symptomForm.symptomId) { ElMessage.warning('请选择疾病和症状'); return }
  await createDiseaseSymptom(symptomForm)
  ElMessage.success('新增成功')
  symptomDialogVisible.value = false
  loadSymptomList()
}

const deleteSymptom = async (id: number) => {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await deleteDiseaseSymptom(id)
  ElMessage.success('删除成功')
  loadSymptomList()
}

const openMedicineForm = () => { medicineDialogVisible.value = true }
const submitMedicineForm = async () => {
  if (!medicineForm.diseaseId || !medicineForm.medicineId) { ElMessage.warning('请选择疾病和药品'); return }
  await createDiseaseMedicine(medicineForm)
  ElMessage.success('新增成功')
  medicineDialogVisible.value = false
  loadMedicineList()
}

const deleteMedicine = async (id: number) => {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await deleteDiseaseMedicine(id)
  ElMessage.success('删除成功')
  loadMedicineList()
}

onMounted(async () => {
  await loadOptions()
  loadSymptomList()
  loadMedicineList()
})
</script>
