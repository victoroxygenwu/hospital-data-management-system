<template>
  <div class="p-20px">
    <!-- 搜索栏 -->
    <el-form :inline="true" :model="queryParams" class="mb-15px">
      <el-form-item label="科室名称">
        <el-input v-model="queryParams.name" placeholder="请输入科室名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="科室编码">
        <el-input v-model="queryParams.code" placeholder="请输入科室编码" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><Icon icon="ep:search" class="mr-5px" />搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" />重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作栏 -->
    <div class="mb-15px">
      <el-button type="primary" @click="openForm('create')"><Icon icon="ep:plus" class="mr-5px" />新增</el-button>
    </div>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column label="ID" prop="id" width="80" />
      <el-table-column label="科室名称" prop="name" width="150" />
      <el-table-column label="科室编码" prop="code" width="120" />
      <el-table-column label="描述" prop="description" min-width="200" show-overflow-tooltip />
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openForm('update', row.id)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination class="mt-15px" v-model:current-page="queryParams.pageNo" v-model:page-size="queryParams.pageSize"
      :total="total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
      @size-change="getList" @current-change="getList" />

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="科室名称" required>
          <el-input v-model="formData.name" placeholder="请输入科室名称" />
        </el-form-item>
        <el-form-item label="科室编码" required>
          <el-input v-model="formData.code" placeholder="请输入科室编码" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入描述" />
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
import { getDepartmentPage, getDepartment, createDepartment, updateDepartment, deleteDepartment } from '@/api/hospital/department'

defineOptions({ name: 'HospitalDepartment' })

const loading = ref(false)
const list = ref([])
const total = ref(0)
const queryParams = reactive({ pageNo: 1, pageSize: 10, name: undefined, code: undefined })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitting = ref(false)
const formData = reactive({ id: undefined, name: '', code: '', description: '' })

const getList = async () => {
  loading.value = true
  try {
    const res = await getDepartmentPage(queryParams)
    list.value = res.list || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

const handleQuery = () => { queryParams.pageNo = 1; getList() }
const resetQuery = () => { queryParams.name = undefined; queryParams.code = undefined; handleQuery() }

const openForm = async (type: string, id?: number) => {
  dialogTitle.value = type === 'create' ? '新增科室' : '编辑科室'
  if (type === 'update' && id) {
    const res = await getDepartment(id)
    Object.assign(formData, res)
  } else {
    Object.assign(formData, { id: undefined, name: '', code: '', description: '' })
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formData.name) { ElMessage.warning('请输入科室名称'); return }
  if (!formData.code) { ElMessage.warning('请输入科室编码'); return }
  submitting.value = true
  try {
    if (formData.id) { await updateDepartment(formData as any) } else { await createDepartment(formData as any) }
    ElMessage.success(formData.id ? '修改成功' : '新增成功')
    dialogVisible.value = false
    getList()
  } finally { submitting.value = false }
}

const handleDelete = async (id: number) => {
  await ElMessageBox.confirm('确定删除该科室吗？', '提示', { type: 'warning' })
  await deleteDepartment(id)
  ElMessage.success('删除成功')
  getList()
}

onMounted(() => getList())
</script>
