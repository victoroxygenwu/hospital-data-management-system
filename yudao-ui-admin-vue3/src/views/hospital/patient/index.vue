<template>
  <div class="p-20px">
    <el-form :inline="true" :model="queryParams" class="mb-15px">
      <el-form-item label="患者姓名">
        <el-input v-model="queryParams.name" placeholder="请输入患者姓名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="身份证号">
        <el-input v-model="queryParams.idCard" placeholder="请输入身份证号" clearable @keyup.enter="handleQuery" />
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
      <el-table-column label="性别" width="60">
        <template #default="{ row }">{{ getDictLabel('hospital_patient_gender', row.gender) }}</template>
      </el-table-column>
      <el-table-column label="出生日期" prop="birthDate" width="120" />
      <el-table-column label="手机号" prop="phone" width="130" />
      <el-table-column label="身份证号" prop="idCard" width="180" />
      <el-table-column label="医保卡号" prop="insuranceNo" width="150" />
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
        <el-form-item label="性别">
          <el-select v-model="formData.gender" style="width:100%;">
            <el-option v-for="opt in getIntDictOptions('hospital_patient_gender')" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="出生日期"><el-date-picker v-model="formData.birthDate" type="date" value-format="YYYY-MM-DD" style="width:100%;" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="formData.phone" placeholder="请输入手机号" /></el-form-item>
        <el-form-item label="身份证号"><el-input v-model="formData.idCard" placeholder="请输入身份证号" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="formData.address" placeholder="请输入地址" /></el-form-item>
        <el-form-item label="紧急联系人"><el-input v-model="formData.emergencyContact" placeholder="请输入紧急联系人姓名" /></el-form-item>
        <el-form-item label="紧急电话"><el-input v-model="formData.emergencyPhone" placeholder="请输入紧急联系人电话" /></el-form-item>
        <el-form-item label="医保卡号"><el-input v-model="formData.insuranceNo" placeholder="请输入医保卡号" /></el-form-item>
        <el-form-item label="既往病史"><el-input v-model="formData.medicalHistory" type="textarea" :rows="2" placeholder="请输入既往病史" /></el-form-item>
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
import { getPatientPage, getPatient, createPatient, updatePatient, deletePatient } from '@/api/hospital/patient'
import { getIntDictOptions, getDictLabel } from '@/utils/hospitalDict'

defineOptions({ name: 'HospitalPatient' })

const loading = ref(false)
const list = ref([])
const total = ref(0)
const queryParams = reactive({ pageNo: 1, pageSize: 10, name: undefined, phone: undefined, idCard: undefined })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitting = ref(false)
const formData = reactive({
  id: undefined as any, name: '', gender: 1, birthDate: '', phone: '', idCard: '',
  address: '', emergencyContact: '', emergencyPhone: '', insuranceNo: '', medicalHistory: ''
})

const getList = async () => {
  loading.value = true
  try {
    const res = await getPatientPage(queryParams)
    list.value = res.list || []
    total.value = res.total || 0
  } finally { loading.value = false }
}

const handleQuery = () => { queryParams.pageNo = 1; getList() }
const resetQuery = () => { queryParams.name = undefined; queryParams.phone = undefined; queryParams.idCard = undefined; handleQuery() }

const openForm = async (type: string, id?: number) => {
  dialogTitle.value = type === 'create' ? '新增病人' : '编辑病人'
  if (type === 'update' && id) {
    const res = await getPatient(id)
    Object.assign(formData, res)
  } else {
    Object.assign(formData, { id: undefined, name: '', gender: 1, birthDate: '', phone: '', idCard: '', address: '', emergencyContact: '', emergencyPhone: '', insuranceNo: '', medicalHistory: '' })
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formData.name) { ElMessage.warning('请输入姓名'); return }
  submitting.value = true
  try {
    if (formData.id) { await updatePatient(formData as any) } else { await createPatient(formData as any) }
    ElMessage.success(formData.id ? '修改成功' : '新增成功')
    dialogVisible.value = false; getList()
  } finally { submitting.value = false }
}

const handleDelete = async (id: number) => {
  await ElMessageBox.confirm('确定删除该病人吗？', '提示', { type: 'warning' })
  await deletePatient(id)
  ElMessage.success('删除成功'); getList()
}

onMounted(() => { getList() })
</script>
