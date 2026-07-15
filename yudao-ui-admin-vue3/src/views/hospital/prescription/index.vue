<template>
  <div class="p-20px">
    <el-form :inline="true" :model="queryParams" class="mb-15px">
      <el-form-item label="就诊ID">
        <el-select v-model="queryParams.visitId" placeholder="全部就诊" clearable filterable>
          <el-option v-for="v in visitOptions" :key="v.id" :label="'#' + v.id + ' - 患者' + v.patientId" :value="v.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option v-for="opt in getIntDictOptions('hospital_prescription_status')" :key="opt.value" :label="opt.label" :value="opt.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><Icon icon="ep:search" class="mr-5px" />搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" />重置</el-button>
      </el-form-item>
    </el-form>

    <div class="mb-15px">
      <el-button type="primary" v-hasPermi="['hospital:prescription:create']" @click="openForm('create')"><Icon icon="ep:plus" class="mr-5px" />新增</el-button>
    </div>

    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column label="ID" prop="id" width="80" />
      <el-table-column label="就诊ID" prop="visitId" width="80" />
      <el-table-column label="医生ID" prop="doctorId" width="80" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="getDictColorType('hospital_prescription_status', row.status)">
            {{ getDictLabel('hospital_prescription_status', row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="notes" min-width="150" show-overflow-tooltip />
      <el-table-column label="创建时间" width="180"><template #default="{ row }">{{ formatTs(row.createTime) }}</template></el-table-column>
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" v-hasPermi="['hospital:prescription:update']" @click="openForm('update', row.id)">编辑</el-button>
          <el-button link type="danger" v-hasPermi="['hospital:prescription:delete']" @click="handleDelete(row.id)">删除</el-button>
          <el-button v-if="row.status === 0 || row.status === '0'" v-hasPermi="['hospital:prescription:dispense']" link type="success" @click="handleDispense(row.id)">发药</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="mt-15px" v-model:current-page="queryParams.pageNo" v-model:page-size="queryParams.pageSize"
      :total="total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
      @size-change="getList" @current-change="getList" />

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="750px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="就诊" required>
          <el-select v-model="formData.visitId" placeholder="请选择就诊" filterable style="width:100%;">
            <el-option v-for="v in visitOptions" :key="v.id" :label="'#' + v.id + ' - 患者' + v.patientId + ' - ' + (v.reason || '')" :value="v.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="formData.status" style="width:100%;">
            <el-option v-for="opt in getIntDictOptions('hospital_prescription_status')" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="formData.notes" type="textarea" :rows="2" placeholder="请输入处方备注" /></el-form-item>
        <el-divider content-position="left">处方明细</el-divider>
        <el-table :data="formData.items" border size="small" style="margin-bottom: 10px;">
          <el-table-column label="药品" width="180">
            <template #default="{ row }">
              <el-select v-model="row.medicineId" placeholder="选择药品" size="small" filterable style="width:160px;">
                <el-option v-for="m in medicineOptions" :key="m.id" :label="m.name" :value="m.id" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="数量" width="80">
            <template #default="{ row }">
              <el-input-number v-model="row.quantity" :min="1" size="small" :controls="false" />
            </template>
          </el-table-column>
          <el-table-column label="用法说明" min-width="120">
            <template #default="{ row }">
              <el-input v-model="row.instructions" size="small" placeholder="如: 口服" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="60" fixed="right">
            <template #default="{ $index }">
              <el-button link type="danger" size="small" @click="formData.items.splice($index, 1)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" size="small" @click="addItem"><Icon icon="ep:plus" class="mr-5px" />添加明细</el-button>
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
import { getPrescriptionPage, getPrescription, createPrescription, updatePrescription, deletePrescription, dispensePrescription } from '@/api/hospital/prescription'
import { getVisitPage } from '@/api/hospital/visit'
import { getMedicinePage } from '@/api/hospital/medicine'
import { getIntDictOptions, getDictLabel, getDictColorType, formatTs } from '@/utils/hospitalDict'
import { checkPermi } from '@/utils/permission'

defineOptions({ name: 'HospitalPrescription' })

interface PrescriptionItem {
  id?: number
  medicineId: number | undefined
  quantity: number
  instructions: string
}

const loading = ref(false)
const list = ref([])
const total = ref(0)
const queryParams = reactive({ pageNo: 1, pageSize: 10, visitId: undefined, doctorId: undefined, status: undefined })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitting = ref(false)
const formData = reactive({
  id: undefined as any,
  visitId: undefined as any,
  doctorId: undefined as any,
  status: 0,
  notes: '',
  items: [] as PrescriptionItem[]
})

const visitOptions = ref<any[]>([])
const medicineOptions = ref<any[]>([])
// 按权限加载下拉选项：缺对应 :query 权限时跳过，避免 403 弹窗
const loadVisits = async () => {
  if (!checkPermi(['hospital:visit:query'])) return
  try { const res = await getVisitPage({ pageNo: 1, pageSize: 200 }); visitOptions.value = res.list || [] } catch {}
}
const loadMedicines = async () => {
  if (!checkPermi(['hospital:medicine:query'])) return
  try { const res = await getMedicinePage({ pageNo: 1, pageSize: 200 }); medicineOptions.value = res.list || [] } catch {}
}

const addItem = () => {
  formData.items.push({ medicineId: undefined, quantity: 1, instructions: '' })
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getPrescriptionPage(queryParams)
    list.value = res.list || []
    total.value = res.total || 0
  } finally { loading.value = false }
}

const handleQuery = () => { queryParams.pageNo = 1; getList() }
const resetQuery = () => { queryParams.visitId = undefined; queryParams.doctorId = undefined; queryParams.status = undefined; handleQuery() }

const openForm = async (type: string, id?: number) => {
  dialogTitle.value = type === 'create' ? '新增处方' : '编辑处方'
  if (type === 'update' && id) {
    const res = await getPrescription(id)
    Object.assign(formData, {
      id: res.id,
      visitId: res.visitId,
      doctorId: res.doctorId,
      status: res.status ?? 0,
      notes: res.notes || '',
      items: (res.items || []).map((item: any) => ({
        id: item.id,
        medicineId: item.medicineId,
        quantity: item.quantity,
        instructions: item.instructions || ''
      }))
    })
  } else {
    Object.assign(formData, { id: undefined, visitId: undefined, doctorId: undefined, status: 0, notes: '', items: [] })
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formData.visitId) { ElMessage.warning('请输入就诊ID'); return }
  submitting.value = true
  try {
    if (formData.id) { await updatePrescription(formData as any) } else { await createPrescription(formData as any) }
    ElMessage.success(formData.id ? '修改成功' : '新增成功')
    dialogVisible.value = false; getList()
  } finally { submitting.value = false }
}

const handleDelete = async (id: number) => {
  await ElMessageBox.confirm('确定删除该处方吗？', '提示', { type: 'warning' })
  await deletePrescription(id)
  ElMessage.success('删除成功'); getList()
}

const handleDispense = async (id: number) => {
  await ElMessageBox.confirm('确定发药吗？', '提示', { type: 'warning' })
  await dispensePrescription(id)
  ElMessage.success('发药成功'); getList()
}

onMounted(() => { getList(); loadVisits(); loadMedicines() })
</script>
