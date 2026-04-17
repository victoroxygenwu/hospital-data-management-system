<template>
  <div class="p-20px">
    <el-form :inline="true" :model="queryParams" class="mb-15px">
      <el-form-item label="药品名称">
        <el-input v-model="queryParams.name" placeholder="请输入药品名称" clearable @keyup.enter="handleQuery" />
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
      <el-table-column label="药品名称" prop="name" width="150" />
      <el-table-column label="规格" prop="specification" width="120" />
      <el-table-column label="单位" prop="unit" width="60" />
      <el-table-column label="单价" prop="price" width="80">
        <template #default="{ row }">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column label="库存" prop="stock" width="80" />
      <el-table-column label="生产厂家" prop="manufacturer" min-width="150" show-overflow-tooltip />
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
        <el-form-item label="药品名称" required><el-input v-model="formData.name" placeholder="请输入药品名称" /></el-form-item>
        <el-form-item label="规格"><el-input v-model="formData.specification" placeholder="如: 0.25g*20粒" /></el-form-item>
        <el-form-item label="单位"><el-input v-model="formData.unit" placeholder="如: 盒/瓶" /></el-form-item>
        <el-form-item label="单价"><el-input-number v-model="formData.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="formData.stock" :min="0" /></el-form-item>
        <el-form-item label="生产厂家"><el-input v-model="formData.manufacturer" placeholder="请输入生产厂家" /></el-form-item>
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
import { getMedicinePage, getMedicine, createMedicine, updateMedicine, deleteMedicine } from '@/api/hospital/medicine'

defineOptions({ name: 'HospitalMedicine' })

const loading = ref(false)
const list = ref([])
const total = ref(0)
const queryParams = reactive({ pageNo: 1, pageSize: 10, name: undefined })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitting = ref(false)
const formData = reactive({ id: undefined, name: '', specification: '', unit: '', price: 0, stock: 0, manufacturer: '' })

const getList = async () => {
  loading.value = true
  try {
    const res = await getMedicinePage(queryParams)
    list.value = res.list || []
    total.value = res.total || 0
  } finally { loading.value = false }
}

const handleQuery = () => { queryParams.pageNo = 1; getList() }
const resetQuery = () => { queryParams.name = undefined; handleQuery() }

const openForm = async (type: string, id?: number) => {
  dialogTitle.value = type === 'create' ? '新增药品' : '编辑药品'
  if (type === 'update' && id) {
    const res = await getMedicine(id)
    Object.assign(formData, res)
  } else {
    Object.assign(formData, { id: undefined, name: '', specification: '', unit: '', price: 0, stock: 0, manufacturer: '' })
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formData.name) { ElMessage.warning('请输入药品名称'); return }
  submitting.value = true
  try {
    if (formData.id) { await updateMedicine(formData as any) } else { await createMedicine(formData as any) }
    ElMessage.success(formData.id ? '修改成功' : '新增成功')
    dialogVisible.value = false; getList()
  } finally { submitting.value = false }
}

const handleDelete = async (id: number) => {
  await ElMessageBox.confirm('确定删除该药品吗？', '提示', { type: 'warning' })
  await deleteMedicine(id)
  ElMessage.success('删除成功'); getList()
}

onMounted(() => getList())
</script>
