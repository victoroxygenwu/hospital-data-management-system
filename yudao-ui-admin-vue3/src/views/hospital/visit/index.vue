<template>
  <div class="p-20px">
    <el-form :inline="true" :model="queryParams" class="mb-15px">
      <el-form-item label="患者">
        <el-select v-model="queryParams.patientId" placeholder="全部患者" clearable filterable>
          <el-option v-for="p in patientOptions" :key="p.id" :label="p.name" :value="p.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="医生">
        <el-select v-model="queryParams.doctorId" placeholder="全部医生" clearable filterable>
          <el-option v-for="d in doctorOptions" :key="d.id" :label="d.name" :value="d.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="科室">
        <el-select v-model="queryParams.deptId" placeholder="全部科室" clearable filterable>
          <el-option v-for="dept in deptOptions" :key="dept.id" :label="dept.deptName" :value="dept.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option v-for="opt in getIntDictOptions('hospital_visit_status')" :key="opt.value" :label="opt.label" :value="opt.value" />
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
      <el-table-column label="患者" width="100">
        <template #default="{ row }">{{ getPatientName(row.patientId) }}</template>
      </el-table-column>
      <el-table-column label="医生" width="100">
        <template #default="{ row }">{{ getDoctorName(row.doctorId) }}</template>
      </el-table-column>
      <el-table-column label="科室" width="100">
        <template #default="{ row }">{{ getDeptName(row.deptId) }}</template>
      </el-table-column>
      <el-table-column label="就诊时间" prop="visitDate" width="180" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="getDictColorType('hospital_visit_status', row.status)">
            {{ getDictLabel('hospital_visit_status', row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="就诊原因" prop="reason" min-width="120" show-overflow-tooltip />
      <el-table-column label="诊断" prop="diagnosis" min-width="150" show-overflow-tooltip />
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openForm('update', row.id)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="mt-15px" v-model:current-page="queryParams.pageNo" v-model:page-size="queryParams.pageSize"
      :total="total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
      @size-change="getList" @current-change="getList" />

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="患者" required>
          <el-select v-model="formData.patientId" placeholder="请选择患者" filterable style="width:100%;">
            <el-option v-for="p in patientOptions" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="医生" required>
          <el-select v-model="formData.doctorId" placeholder="请选择医生" filterable style="width:100%;">
            <el-option v-for="d in doctorOptions" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="科室" required>
          <el-select v-model="formData.deptId" placeholder="请选择科室" filterable style="width:100%;">
            <el-option v-for="dept in deptOptions" :key="dept.id" :label="dept.deptName" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="就诊日期" required><el-date-picker v-model="formData.visitDate" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%;" /></el-form-item>
        <el-form-item label="就诊原因"><el-input v-model="formData.reason" type="textarea" :rows="2" placeholder="请输入就诊原因" /></el-form-item>
        <el-form-item label="诊断"><el-input v-model="formData.diagnosis" type="textarea" :rows="2" placeholder="请输入诊断结果" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="formData.notes" type="textarea" :rows="2" placeholder="请输入医生备注" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="formData.status" style="width:100%;">
            <el-option v-for="opt in getIntDictOptions('hospital_visit_status')" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
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
import { getVisitPage, getVisit, createVisit, updateVisit, deleteVisit } from '@/api/hospital/visit'
import { getPatientPage } from '@/api/hospital/patient'
import { getDoctorPage } from '@/api/hospital/doctor'
import { getDepartmentPage } from '@/api/hospital/department'
import { getIntDictOptions, getDictLabel, getDictColorType } from '@/utils/hospitalDict'

defineOptions({ name: 'HospitalVisit' })

const patientOptions = ref<{ id: number; name: string }[]>([])
const doctorOptions = ref<{ id: number; name: string }[]>([])
const deptOptions = ref<{ id: number; deptName: string }[]>([])

const loadOptions = async () => {
  try {
    const [pRes, dRes, deptRes] = await Promise.all([
      getPatientPage({ pageNo: 1, pageSize: 200 }),
      getDoctorPage({ pageNo: 1, pageSize: 200 }),
      getDepartmentPage({ pageNo: 1, pageSize: 200 })
    ])
    patientOptions.value = (pRes.list || []).map((p: any) => ({ id: p.id, name: p.name }))
    doctorOptions.value = (dRes.list || []).map((d: any) => ({ id: d.id, name: d.name }))
    deptOptions.value = (deptRes.list || []).map((d: any) => ({ id: d.id, deptName: d.deptName }))
  } catch { /* ignore */ }
}

const getPatientName = (id: number) => patientOptions.value.find(p => p.id === id)?.name || String(id || '')
const getDoctorName = (id: number) => doctorOptions.value.find(d => d.id === id)?.name || String(id || '')
const getDeptName = (id: number) => deptOptions.value.find(d => d.id === id)?.deptName || String(id || '')

const loading = ref(false)
const list = ref([])
const total = ref(0)
const queryParams = reactive({ pageNo: 1, pageSize: 10, patientId: undefined, doctorId: undefined, deptId: undefined, status: undefined })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitting = ref(false)
const formData = reactive({
  id: undefined as any, patientId: undefined as any, doctorId: undefined as any, deptId: undefined as any,
  visitDate: '', reason: '', diagnosis: '', notes: '', status: 0
})

const getList = async () => {
  loading.value = true
  try {
    const res = await getVisitPage(queryParams)
    list.value = res.list || []
    total.value = res.total || 0
  } finally { loading.value = false }
}

const handleQuery = () => { queryParams.pageNo = 1; getList() }
const resetQuery = () => { queryParams.patientId = undefined; queryParams.doctorId = undefined; queryParams.deptId = undefined; queryParams.status = undefined; handleQuery() }

const openForm = async (type: string, id?: number) => {
  dialogTitle.value = type === 'create' ? '新增就诊' : '编辑就诊'
  if (type === 'update' && id) {
    const res = await getVisit(id)
    Object.assign(formData, res)
  } else {
    Object.assign(formData, { id: undefined, patientId: undefined, doctorId: undefined, deptId: undefined, visitDate: '', reason: '', diagnosis: '', notes: '', status: 0 })
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formData.patientId) { ElMessage.warning('请选择患者'); return }
  if (!formData.doctorId) { ElMessage.warning('请选择医生'); return }
  submitting.value = true
  try {
    if (formData.id) { await updateVisit(formData as any) } else { await createVisit(formData as any) }
    ElMessage.success(formData.id ? '修改成功' : '新增成功')
    dialogVisible.value = false; getList()
  } finally { submitting.value = false }
}

const handleDelete = async (id: number) => {
  await ElMessageBox.confirm('确定删除该就诊记录吗？', '提示', { type: 'warning' })
  await deleteVisit(id)
  ElMessage.success('删除成功'); getList()
}

onMounted(async () => {
  await loadOptions()
  getList()
})
</script>
