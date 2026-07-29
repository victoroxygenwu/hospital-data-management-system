<template>
  <div class="p-20px">
    <el-form :inline="true" :model="queryParams" class="mb-15px">
      <el-form-item label="疾病名称">
        <el-input v-model="queryParams.name" placeholder="请输入疾病名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="分类">
        <el-input v-model="queryParams.category" placeholder="如：内科" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><Icon icon="ep:search" class="mr-5px" />搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" />重置</el-button>
      </el-form-item>
    </el-form>

    <div class="mb-15px">
      <el-button type="primary" v-hasPermi="['hospital:disease:create']" @click="openForm('create')">
        <Icon icon="ep:plus" class="mr-5px" />新增
      </el-button>
    </div>

    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column label="ID" prop="id" width="70" />
      <el-table-column label="疾病名称" prop="name" width="160" />
      <el-table-column label="ICD编码" prop="icdCode" width="100" />
      <el-table-column label="分类" prop="category" width="100" />
      <el-table-column label="科室ID" prop="deptId" width="80" />
      <el-table-column label="常见病" width="80">
        <template #default="{ row }">{{ row.isCommon === 1 ? '是' : '否' }}</template>
      </el-table-column>
      <el-table-column label="典型症状" prop="typicalSymptoms" min-width="150" show-overflow-tooltip />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" v-hasPermi="['hospital:disease:update']" @click="openForm('update', row.id)">编辑</el-button>
          <el-button link type="danger" v-hasPermi="['hospital:disease:delete']" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="mt-15px" v-model:current-page="queryParams.pageNo" v-model:page-size="queryParams.pageSize"
      :total="total" layout="total, sizes, prev, pager, next" @size-change="getList" @current-change="getList" />

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="疾病名称" required><el-input v-model="formData.name" /></el-form-item>
        <el-form-item label="ICD编码"><el-input v-model="formData.icdCode" placeholder="如 J06.9" /></el-form-item>
        <el-form-item label="分类"><el-input v-model="formData.category" placeholder="内科/外科/儿科" /></el-form-item>
        <el-form-item label="所属科室">
          <el-select v-model="formData.deptId" placeholder="请选择科室" filterable clearable style="width:100%">
            <el-option v-for="d in deptOptions" :key="d.id" :label="d.deptName" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否常见病">
          <el-radio-group v-model="formData.isCommon">
            <el-radio :value="1">是</el-radio>
            <el-radio :value="0">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="典型症状"><el-input v-model="formData.typicalSymptoms" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="简介"><el-input v-model="formData.description" type="textarea" :rows="2" /></el-form-item>
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
import { getDiseasePage, getDisease, createDisease, updateDisease, deleteDisease } from '@/api/hospital/disease'
import { getDepartmentList } from '@/api/hospital/department'

defineOptions({ name: 'HospitalDisease' })

const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const deptOptions = ref<any[]>([])
const queryParams = reactive({ pageNo: 1, pageSize: 10, name: undefined, category: undefined })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitting = ref(false)
const formData = reactive<any>({ id: undefined, name: '', icdCode: '', category: '', deptId: undefined, isCommon: 1, typicalSymptoms: '', description: '' })

const getList = async () => {
  loading.value = true
  try {
    const res = await getDiseasePage(queryParams)
    list.value = res.list || []
    total.value = res.total || 0
  } finally { loading.value = false }
}

const handleQuery = () => { queryParams.pageNo = 1; getList() }
const resetQuery = () => { queryParams.name = undefined; queryParams.category = undefined; handleQuery() }

const openForm = async (type: string, id?: number) => {
  dialogTitle.value = type === 'create' ? '新增疾病' : '编辑疾病'
  if (type === 'update' && id) {
    Object.assign(formData, await getDisease(id))
  } else {
    Object.assign(formData, { id: undefined, name: '', icdCode: '', category: '', deptId: undefined, isCommon: 1, typicalSymptoms: '', description: '' })
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formData.name) { ElMessage.warning('请输入疾病名称'); return }
  submitting.value = true
  try {
    if (formData.id) await updateDisease(formData)
    else await createDisease(formData)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    getList()
  } finally { submitting.value = false }
}

const handleDelete = async (id: number) => {
  await ElMessageBox.confirm('确定删除该疾病吗？', '提示', { type: 'warning' })
  await deleteDisease(id)
  ElMessage.success('删除成功')
  getList()
}

onMounted(async () => {
  deptOptions.value = (await getDepartmentList()) || []
  getList()
})
</script>
