<template>
  <div class="p-20px">
    <el-form :inline="true" :model="queryParams" class="mb-15px">
      <el-form-item label="患者ID">
        <el-input v-model.number="queryParams.patientId" placeholder="请输入患者ID" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="医生ID">
        <el-input v-model.number="queryParams.doctorId" placeholder="请输入医生ID" clearable @keyup.enter="handleQuery" />
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
      <el-table-column label="患者ID" prop="patientId" width="80" />
      <el-table-column label="医生ID" prop="doctorId" width="80" />
      <el-table-column label="科室ID" prop="deptId" width="80" />
      <el-table-column label="就诊日期" prop="visitDate" width="120" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="getDictColorType('hospital_visit_status', row.status)">
            {{ getDictLabel('hospital_visit_status', row.status) }}
          </el-tag>
        </template>
      </el-table-column>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="患者ID" required><el-input v-model.number="formData.patientId" placeholder="请输入患者ID" /></el-form-item>
        <el-form-item label="医生ID" required><el-input v-model.number="formData.doctorId" placeholder="请输入医生ID" /></el-form-item>
        <el-form-item label="科室ID" required><el-input v-model.number="formData.deptId" placeholder="请输入科室ID" /></el-form-item>
        <el-form-item label="就诊日期" required><el-date-picker v-model="formData.visitDate" type="date" value-format="YYYY-MM-DD" style="width:100%;" /></el-form-item>
        <el-form-item label="诊断"><el-input v-model="formData.diagnosis" type="textarea" :rows="3" /></el-form-item>
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
import { getIntDictOptions, getDictLabel, getDictColorType } from '@/utils/hospitalDict'

defineOptions({ name: 'HospitalVisit' })

const loading = ref(false)
const list = ref([])
const total = ref(0)
const queryParams = reactive({ pageNo: 1, pageSize: 10, patientId: undefined, doctorId: undefined, status: undefined })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitting = ref(false)
const formData = reactive({ id: undefined as any, patientId: undefined as any, doctorId: undefined as any, deptId: undefined as any, visitDate: '', status: 0, diagnosis: '' })

const getList = async () => {
  loading.value = true
  try {
    const res = await getVisitPage(queryParams)
    list.value = res.list || []
    total.value = res.total || 0
  } finally { loading.value = false }
}

const handleQuery = () => { queryParams.pageNo = 1; getList() }
const resetQuery = () => { queryParams.patientId = undefined; queryParams.doctorId = undefined; queryParams.status = undefined; handleQuery() }

const openForm = async (type: string, id?: number) => {
  dialogTitle.value = type === 'create' ? '新增就诊' : '编辑就诊'
  if (type === 'update' && id) {
    const res = await getVisit(id)
    Object.assign(formData, res)
  } else {
    Object.assign(formData, { id: undefined, patientId: undefined, doctorId: undefined, deptId: undefined, visitDate: '', status: 0, diagnosis: '' })
  }
  dialogVisible.value = true
}

const submitForm = async () => {
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

onMounted(() => { getList() })
</script>
