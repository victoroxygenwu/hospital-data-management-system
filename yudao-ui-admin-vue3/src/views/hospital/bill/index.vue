<template>
  <div class="p-20px">
    <el-form :inline="true" :model="queryParams" class="mb-15px">
      <el-form-item label="患者">
        <el-select v-model="queryParams.patientId" placeholder="全部患者" clearable filterable>
          <el-option v-for="p in patientOptions" :key="p.id" :label="p.name" :value="p.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="支付状态">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option v-for="opt in getIntDictOptions('hospital_bill_pay_status')" :key="opt.value" :label="opt.label" :value="opt.value" />
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
      <el-table-column label="就诊ID" prop="visitId" width="80" />
      <el-table-column label="总金额" prop="totalAmount" width="100">
        <template #default="{ row }"><span style="color: #f56c6c; font-weight: bold">¥{{ row.totalAmount }}</span></template>
      </el-table-column>
      <el-table-column label="已付金额" prop="payAmount" width="100">
        <template #default="{ row }">¥{{ row.payAmount || 0 }}</template>
      </el-table-column>
      <el-table-column label="支付状态" width="80">
        <template #default="{ row }">
          <el-tag :type="getDictColorType('hospital_bill_pay_status', row.status)">
            {{ getDictLabel('hospital_bill_pay_status', row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="支付方式" width="80">
        <template #default="{ row }">{{ row.payMethod ? getDictLabel('hospital_bill_pay_method', row.payMethod) : '-' }}</template>
      </el-table-column>
      <el-table-column label="支付时间" prop="payTime" width="180">
        <template #default="{ row }">{{ row.payTime || '-' }}</template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openForm('update', row.id)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
          <el-button v-if="row.status === 0 || row.status === '0'" link type="success" @click="handlePay(row.id)">支付</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="mt-15px" v-model:current-page="queryParams.pageNo" v-model:page-size="queryParams.pageSize"
      :total="total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
      @size-change="getList" @current-change="getList" />

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="患者" required>
          <el-select v-model="formData.patientId" placeholder="请选择患者" filterable style="width:100%;">
            <el-option v-for="p in patientOptions" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="就诊ID"><el-input v-model.number="formData.visitId" placeholder="请输入就诊ID" /></el-form-item>
        <el-form-item label="总金额"><el-input-number v-model="formData.totalAmount" :min="0" :precision="2" style="width:100%;" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="payVisible" title="支付账单" width="400px">
      <el-form label-width="80px">
        <el-form-item label="支付方式" required>
          <el-select v-model="payMethod" style="width:100%;">
            <el-option v-for="opt in getIntDictOptions('hospital_bill_pay_method')" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="payVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPay" :loading="paying">确定支付</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBillPage, getBill, createBill, updateBill, deleteBill, payBill } from '@/api/hospital/bill'
import { getPatientPage } from '@/api/hospital/patient'
import { getIntDictOptions, getDictLabel, getDictColorType } from '@/utils/hospitalDict'

defineOptions({ name: 'HospitalBill' })

const patientOptions = ref<{ id: number; name: string }[]>([])
const loadPatients = async () => {
  try {
    const res = await getPatientPage({ pageNo: 1, pageSize: 200 })
    patientOptions.value = (res.list || []).map((p: any) => ({ id: p.id, name: p.name }))
  } catch { /* ignore */ }
}
const getPatientName = (id: number) => patientOptions.value.find(p => p.id === id)?.name || String(id || '')

const loading = ref(false)
const list = ref([])
const total = ref(0)
const queryParams = reactive({ pageNo: 1, pageSize: 10, patientId: undefined, status: undefined })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitting = ref(false)
const formData = reactive({ id: undefined as any, patientId: undefined as any, visitId: undefined as any, totalAmount: 0 })
const payVisible = ref(false)
const payBillId = ref<number>()
const payMethod = ref<string | number>('')
const paying = ref(false)

const getList = async () => {
  loading.value = true
  try {
    const res = await getBillPage(queryParams)
    list.value = res.list || []
    total.value = res.total || 0
  } finally { loading.value = false }
}

const handleQuery = () => { queryParams.pageNo = 1; getList() }
const resetQuery = () => { queryParams.patientId = undefined; queryParams.status = undefined; handleQuery() }

const openForm = async (type: string, id?: number) => {
  dialogTitle.value = type === 'create' ? '新增账单' : '编辑账单'
  if (type === 'update' && id) {
    const res = await getBill(id)
    Object.assign(formData, res)
  } else {
    Object.assign(formData, { id: undefined, patientId: undefined, visitId: undefined, totalAmount: 0 })
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  submitting.value = true
  try {
    if (formData.id) { await updateBill(formData as any) } else { await createBill(formData as any) }
    ElMessage.success(formData.id ? '修改成功' : '新增成功')
    dialogVisible.value = false; getList()
  } finally { submitting.value = false }
}

const handleDelete = async (id: number) => {
  await ElMessageBox.confirm('确定删除该账单吗？', '提示', { type: 'warning' })
  await deleteBill(id)
  ElMessage.success('删除成功'); getList()
}

const handlePay = (id: number) => {
  payBillId.value = id
  payMethod.value = ''
  payVisible.value = true
}

const submitPay = async () => {
  if (!payMethod.value && payMethod.value !== 0) { ElMessage.warning('请选择支付方式'); return }
  paying.value = true
  try {
    await payBill(payBillId.value!, payMethod.value)
    ElMessage.success('支付成功')
    payVisible.value = false; getList()
  } finally { paying.value = false }
}

onMounted(async () => {
  await loadPatients()
  getList()
})
</script>
