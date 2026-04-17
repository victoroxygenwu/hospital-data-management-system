<template>
  <div class="p-20px">
    <el-form :inline="true" :model="queryParams" class="mb-15px">
      <el-form-item label="患者ID">
        <el-input v-model.number="queryParams.patientId" placeholder="请输入患者ID" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option v-for="opt in getIntDictOptions('hospital_prescription_status')" :key="opt.value" :label="opt.label" :value="opt.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><Icon icon="ep:search" class="mr-5px" />搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" />重置</el-button>
      </el-form-item>
    </el-form>

    <div class="mb-15px">
      <el-button type="primary" @click="openForm('create')"><Icon icon="ep:plus" class="mr-5px" />新增</el-button>
    </div>

    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column label="ID" prop="id" width="80" />
      <el-table-column label="就诊ID" prop="visitId" width="80" />
      <el-table-column label="患者ID" prop="patientId" width="80" />
      <el-table-column label="医生ID" prop="doctorId" width="80" />
      <el-table-column label="诊断" prop="diagnosis" min-width="150" show-overflow-tooltip />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="getDictColorType('hospital_prescription_status', row.status)">
            {{ getDictLabel('hospital_prescription_status', row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openForm('update', row.id)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
          <el-button v-if="row.status === 0" link type="success" @click="handleDispense(row.id)">发药</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="mt-15px" v-model:current-page="queryParams.pageNo" v-model:page-size="queryParams.pageSize"
      :total="total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
      @size-change="getList" @current-change="getList" />

    <!-- 新增/编辑处方弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="750px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="就诊ID" required><el-input v-model.number="formData.visitId" placeholder="请输入就诊ID" /></el-form-item>
        <el-form-item label="患者ID" required><el-input v-model.number="formData.patientId" placeholder="请输入患者ID" /></el-form-item>
        <el-form-item label="医生ID" required><el-input v-model.number="formData.doctorId" placeholder="请输入医生ID" /></el-form-item>
        <el-form-item label="诊断"><el-input v-model="formData.diagnosis" type="textarea" :rows="2" /></el-form-item>

        <!-- 处方明细 -->
        <el-divider content-position="left">处方明细</el-divider>
        <el-table :data="formData.items" border size="small" style="margin-bottom: 10px;">
          <el-table-column label="药品ID" width="100">
            <template #default="{ row }">
              <el-input-number v-model="row.medicineId" :min="1" :controls="false" size="small" placeholder="药品ID" />
            </template>
          </el-table-column>
          <el-table-column label="数量" width="80">
            <template #default="{ row }">
              <el-input-number v-model="row.quantity" :min="1" size="small" :controls="false" />
            </template>
          </el-table-column>
          <el-table-column label="用法" width="100">
            <template #default="{ row }">
              <el-input v-model="row.usage" size="small" placeholder="口服等" />
            </template>
          </el-table-column>
          <el-table-column label="剂量" width="100">
            <template #default="{ row }">
              <el-input v-model="row.dosage" size="small" placeholder="如1片" />
            </template>
          </el-table-column>
          <el-table-column label="频次" width="100">
            <template #default="{ row }">
              <el-input v-model="row.frequency" size="small" placeholder="如3次/日" />
            </template>
          </el-table-column>
          <el-table-column label="天数" width="80">
            <template #default="{ row }">
              <el-input-number v-model="row.days" :min="1" size="small" :controls="false" />
            </template>
          </el-table-column>
          <el-table-column label="备注" min-width="100">
            <template #default="{ row }">
              <el-input v-model="row.remark" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="60" fixed="right">
            <template #default="{ $index }">
              <el-button link type="danger" size="small" @click="formData.items.splice($index, 1)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" size="small" @click="addItem"><Icon icon="ep:plus" class="mr-5px" />添加明细</el-button>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPrescriptionPage, getPrescription, createPrescription, updatePrescription, deletePrescription, dispensePrescription } from '@/api/hospital/prescription'
import { getIntDictOptions, getDictLabel, getDictColorType } from '@/utils/hospitalDict'

defineOptions({ name: 'HospitalPrescription' })

interface PrescriptionItem {
  id?: number
  medicineId: number | undefined
  quantity: number
  usage: string
  dosage: string
  frequency: string
  days: number
  remark: string
}

const loading = ref(false)
const list = ref([])
const total = ref(0)
const queryParams = reactive({ pageNo: 1, pageSize: 10, patientId: undefined, status: undefined })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitting = ref(false)
const formData = reactive({
  id: undefined as any,
  visitId: undefined as any,
  patientId: undefined as any,
  doctorId: undefined as any,
  diagnosis: '',
  items: [] as PrescriptionItem[]
})

const addItem = () => {
  formData.items.push({ medicineId: undefined, quantity: 1, usage: '', dosage: '', frequency: '', days: 1, remark: '' })
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getPrescriptionPage(queryParams)
    list.value = res.list || []
    total.value = res.total || 0
  } finally { loading.value = false }
}

const handleQuery = () => { queryParams.pageNo = 1; getList() }
const resetQuery = () => { queryParams.patientId = undefined; queryParams.status = undefined; handleQuery() }

const openForm = async (type: string, id?: number) => {
  dialogTitle.value = type === 'create' ? '新增处方' : '编辑处方'
  if (type === 'update' && id) {
    const res = await getPrescription(id)
    Object.assign(formData, {
      id: res.id,
      visitId: res.visitId,
      patientId: res.patientId,
      doctorId: res.doctorId,
      diagnosis: res.diagnosis || '',
      items: (res.items || []).map((item: any) => ({
        id: item.id,
        medicineId: item.medicineId,
        quantity: item.quantity,
        usage: item.usage || '',
        dosage: item.dosage || '',
        frequency: item.frequency || '',
        days: item.days || 1,
        remark: item.remark || ''
      }))
    })
  } else {
    Object.assign(formData, { id: undefined, visitId: undefined, patientId: undefined, doctorId: undefined, diagnosis: '', items: [] })
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  submitting.value = true
  try {
    if (formData.id) { await updatePrescription(formData as any) } else { await createPrescription(formData as any) }
    ElMessage.success(formData.id ? '修改成功' : '新增成功')
    dialogVisible.value = false; getList()
  } finally { submitting.value = false }
}

const handleDelete = async (id: number) => {
  await ElMessageBox.confirm('确定删除该处方吗？', '提示', { type: 'warning' })
  await deletePrescription(id)
  ElMessage.success('删除成功'); getList()
}

const handleDispense = async (id: number) => {
  await ElMessageBox.confirm('确定发药吗？', '提示', { type: 'warning' })
  await dispensePrescription(id)
  ElMessage.success('发药成功'); getList()
}

onMounted(() => { getList() })
</script>
