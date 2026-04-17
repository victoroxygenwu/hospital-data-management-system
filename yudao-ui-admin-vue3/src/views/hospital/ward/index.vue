<template>
  <div class="p-20px">
    <el-form :inline="true" :model="queryParams" class="mb-15px">
      <el-form-item label="病房名称">
        <el-input v-model="queryParams.name" placeholder="请输入病房名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="科室">
        <el-select v-model="queryParams.deptId" placeholder="全部科室" clearable filterable>
          <el-option v-for="dept in deptOptions" :key="dept.id" :label="dept.name" :value="dept.id" />
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
      <el-table-column label="科室" width="120">
        <template #default="{ row }">{{ getDeptName(row.deptId) }}</template>
      </el-table-column>
      <el-table-column label="病房名称" prop="name" width="150" />
      <el-table-column label="病房类型" width="100">
        <template #default="{ row }">{{ getDictLabel('hospital_ward_type', row.wardType) }}</template>
      </el-table-column>
      <el-table-column label="楼层" prop="floor" width="80" />
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
        <el-form-item label="科室" required>
          <el-select v-model="formData.deptId" placeholder="请选择科室" filterable style="width:100%;">
            <el-option v-for="dept in deptOptions" :key="dept.id" :label="dept.name" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="病房名称" required><el-input v-model="formData.name" placeholder="请输入病房名称" /></el-form-item>
        <el-form-item label="病房类型">
          <el-select v-model="formData.wardType" placeholder="请选择病房类型" style="width:100%;">
            <el-option v-for="opt in getIntDictOptions('hospital_ward_type')" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼层"><el-input-number v-model="formData.floor" :min="1" /></el-form-item>
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
import { getWardPage, getWard, createWard, updateWard, deleteWard } from '@/api/hospital/ward'
import { getDepartmentPage } from '@/api/hospital/department'
import { getIntDictOptions, getDictLabel } from '@/utils/hospitalDict'

defineOptions({ name: 'HospitalWard' })

// 科室下拉
const deptOptions = ref<{ id: number; name: string }[]>([])
const loadDepts = async () => {
  try {
    const res = await getDepartmentPage({ pageNo: 1, pageSize: 200 })
    deptOptions.value = (res.list || []).map((d: any) => ({ id: d.id, name: d.name }))
  } catch { /* ignore */ }
}
const getDeptName = (deptId: number) => deptOptions.value.find(d => d.id === deptId)?.name || String(deptId || '')

const loading = ref(false)
const list = ref([])
const total = ref(0)
const queryParams = reactive({ pageNo: 1, pageSize: 10, name: undefined, deptId: undefined })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitting = ref(false)
const formData = reactive({ id: undefined as any, deptId: undefined as any, name: '', wardType: undefined as any, floor: 1 })

const getList = async () => {
  loading.value = true
  try {
    const res = await getWardPage(queryParams)
    list.value = res.list || []
    total.value = res.total || 0
  } finally { loading.value = false }
}

const handleQuery = () => { queryParams.pageNo = 1; getList() }
const resetQuery = () => { queryParams.name = undefined; queryParams.deptId = undefined; handleQuery() }

const openForm = async (type: string, id?: number) => {
  dialogTitle.value = type === 'create' ? '新增病房' : '编辑病房'
  if (type === 'update' && id) {
    const res = await getWard(id)
    Object.assign(formData, res)
  } else {
    Object.assign(formData, { id: undefined, deptId: undefined, name: '', wardType: undefined, floor: 1 })
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formData.name) { ElMessage.warning('请输入病房名称'); return }
  submitting.value = true
  try {
    if (formData.id) { await updateWard(formData as any) } else { await createWard(formData as any) }
    ElMessage.success(formData.id ? '修改成功' : '新增成功')
    dialogVisible.value = false; getList()
  } finally { submitting.value = false }
}

const handleDelete = async (id: number) => {
  await ElMessageBox.confirm('确定删除该病房吗？', '提示', { type: 'warning' })
  await deleteWard(id)
  ElMessage.success('删除成功'); getList()
}

onMounted(async () => {
  await loadDepts()
  getList()
})
</script>
