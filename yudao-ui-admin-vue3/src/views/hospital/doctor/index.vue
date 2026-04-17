<template>
  <div class="p-20px">
    <el-form :inline="true" :model="queryParams" class="mb-15px">
      <el-form-item label="医生姓名">
        <el-input v-model="queryParams.name" placeholder="请输入医生姓名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="科室">
        <el-select v-model="queryParams.deptId" placeholder="全部科室" clearable filterable>
          <el-option v-for="dept in deptOptions" :key="dept.id" :label="dept.deptName" :value="dept.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="职称">
        <el-select v-model="queryParams.title" placeholder="全部职称" clearable>
          <el-option v-for="opt in getIntDictOptions('hospital_doctor_title')" :key="opt.value" :label="opt.label" :value="opt.value" />
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
      <el-table-column label="姓名" prop="name" width="100" />
      <el-table-column label="科室" width="120">
        <template #default="{ row }">{{ getDeptName(row.deptId) }}</template>
      </el-table-column>
      <el-table-column label="职称" width="120">
        <template #default="{ row }">{{ getDictLabel('hospital_doctor_title', row.title) }}</template>
      </el-table-column>
      <el-table-column label="性别" width="60">
        <template #default="{ row }">{{ getDictLabel('hospital_patient_gender', row.gender) }}</template>
      </el-table-column>
      <el-table-column label="年龄" prop="age" width="60" />
      <el-table-column label="电话" prop="phone" width="130" />
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
        <el-form-item label="姓名" required><el-input v-model="formData.name" placeholder="请输入姓名" /></el-form-item>
        <el-form-item label="科室" required>
          <el-select v-model="formData.deptId" placeholder="请选择科室" filterable style="width:100%;">
            <el-option v-for="dept in deptOptions" :key="dept.id" :label="dept.deptName" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="formData.gender" style="width:100%;">
            <el-option v-for="opt in getIntDictOptions('hospital_patient_gender')" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄"><el-input-number v-model="formData.age" :min="0" style="width:100%;" /></el-form-item>
        <el-form-item label="职称">
          <el-select v-model="formData.title" placeholder="请选择职称" style="width:100%;">
            <el-option v-for="opt in getIntDictOptions('hospital_doctor_title')" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="电话"><el-input v-model="formData.phone" placeholder="请输入电话" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="formData.email" placeholder="请输入邮箱" /></el-form-item>
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
import { getDoctorPage, getDoctor, createDoctor, updateDoctor, deleteDoctor } from '@/api/hospital/doctor'
import { getDepartmentPage } from '@/api/hospital/department'
import { getIntDictOptions, getDictLabel } from '@/utils/hospitalDict'

defineOptions({ name: 'HospitalDoctor' })

// 科室下拉
const deptOptions = ref<{ id: number; deptName: string }[]>([])
const loadDepts = async () => {
  try {
    const res = await getDepartmentPage({ pageNo: 1, pageSize: 200 })
    deptOptions.value = (res.list || []).map((d: any) => ({ id: d.id, deptName: d.deptName }))
  } catch { /* ignore */ }
}
const getDeptName = (deptId: number) => deptOptions.value.find(d => d.id === deptId)?.deptName || String(deptId || '')

const loading = ref(false)
const list = ref([])
const total = ref(0)
const queryParams = reactive({ pageNo: 1, pageSize: 10, name: undefined, deptId: undefined, title: undefined })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitting = ref(false)
const formData = reactive({ id: undefined as any, name: '', deptId: undefined as any, gender: undefined as any, age: undefined as any, title: undefined as any, phone: '', email: '' })

const getList = async () => {
  loading.value = true
  try {
    const res = await getDoctorPage(queryParams)
    list.value = res.list || []
    total.value = res.total || 0
  } finally { loading.value = false }
}

const handleQuery = () => { queryParams.pageNo = 1; getList() }
const resetQuery = () => { queryParams.name = undefined; queryParams.deptId = undefined; queryParams.title = undefined; handleQuery() }

const openForm = async (type: string, id?: number) => {
  dialogTitle.value = type === 'create' ? '新增医生' : '编辑医生'
  if (type === 'update' && id) {
    const res = await getDoctor(id)
    Object.assign(formData, res)
  } else {
    Object.assign(formData, { id: undefined, name: '', deptId: undefined, gender: undefined, age: undefined, title: undefined, phone: '', email: '' })
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formData.name) { ElMessage.warning('请输入姓名'); return }
  submitting.value = true
  try {
    if (formData.id) { await updateDoctor(formData as any) } else { await createDoctor(formData as any) }
    ElMessage.success(formData.id ? '修改成功' : '新增成功')
    dialogVisible.value = false; getList()
  } finally { submitting.value = false }
}

const handleDelete = async (id: number) => {
  await ElMessageBox.confirm('确定删除该医生吗？', '提示', { type: 'warning' })
  await deleteDoctor(id)
  ElMessage.success('删除成功'); getList()
}

onMounted(async () => {
  await loadDepts()
  getList()
})
</script>
