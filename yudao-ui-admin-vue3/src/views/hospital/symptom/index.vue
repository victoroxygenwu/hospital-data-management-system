<template>
  <div class="p-20px">
    <el-form :inline="true" :model="queryParams" class="mb-15px">
      <el-form-item label="症状名称">
        <el-input v-model="queryParams.name" placeholder="请输入症状名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><Icon icon="ep:search" class="mr-5px" />搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" />重置</el-button>
      </el-form-item>
    </el-form>

    <div class="mb-15px">
      <el-button type="primary" v-hasPermi="['hospital:symptom:create']" @click="openForm('create')">
        <Icon icon="ep:plus" class="mr-5px" />新增
      </el-button>
    </div>

    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column label="ID" prop="id" width="70" />
      <el-table-column label="症状名称" prop="name" width="120" />
      <el-table-column label="部位" prop="location" width="100" />
      <el-table-column label="类型" prop="type" width="120" />
      <el-table-column label="描述" prop="description" min-width="200" show-overflow-tooltip />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" v-hasPermi="['hospital:symptom:update']" @click="openForm('update', row.id)">编辑</el-button>
          <el-button link type="danger" v-hasPermi="['hospital:symptom:delete']" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="mt-15px" v-model:current-page="queryParams.pageNo" v-model:page-size="queryParams.pageSize"
      :total="total" layout="total, sizes, prev, pager, next" @size-change="getList" @current-change="getList" />

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="formData" label-width="90px">
        <el-form-item label="症状名称" required><el-input v-model="formData.name" /></el-form-item>
        <el-form-item label="部位"><el-input v-model="formData.location" placeholder="全身/头部/胸腹部" /></el-form-item>
        <el-form-item label="类型"><el-input v-model="formData.type" placeholder="主观感受/客观体征" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="formData.description" type="textarea" :rows="3" /></el-form-item>
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
import { getSymptomPage, getSymptom, createSymptom, updateSymptom, deleteSymptom } from '@/api/hospital/symptom'

defineOptions({ name: 'HospitalSymptom' })

const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({ pageNo: 1, pageSize: 10, name: undefined })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitting = ref(false)
const formData = reactive<any>({ id: undefined, name: '', location: '', type: '', description: '' })

const getList = async () => {
  loading.value = true
  try {
    const res = await getSymptomPage(queryParams)
    list.value = res.list || []
    total.value = res.total || 0
  } finally { loading.value = false }
}

const handleQuery = () => { queryParams.pageNo = 1; getList() }
const resetQuery = () => { queryParams.name = undefined; handleQuery() }

const openForm = async (type: string, id?: number) => {
  dialogTitle.value = type === 'create' ? '新增症状' : '编辑症状'
  if (type === 'update' && id) Object.assign(formData, await getSymptom(id))
  else Object.assign(formData, { id: undefined, name: '', location: '', type: '', description: '' })
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formData.name) { ElMessage.warning('请输入症状名称'); return }
  submitting.value = true
  try {
    if (formData.id) await updateSymptom(formData)
    else await createSymptom(formData)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    getList()
  } finally { submitting.value = false }
}

const handleDelete = async (id: number) => {
  await ElMessageBox.confirm('确定删除该症状吗？', '提示', { type: 'warning' })
  await deleteSymptom(id)
  ElMessage.success('删除成功')
  getList()
}

onMounted(() => getList())
</script>
