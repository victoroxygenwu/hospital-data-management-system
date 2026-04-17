<template>
  <div class="p-20px">
    <el-form :inline="true" :model="queryParams" class="mb-15px">
      <el-form-item label="床位号">
        <el-input v-model="queryParams.bedNo" placeholder="请输入床位号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="病房">
        <el-select v-model="queryParams.wardId" placeholder="全部病房" clearable filterable>
          <el-option v-for="w in wardOptions" :key="w.id" :label="w.wardNo" :value="w.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option v-for="opt in getIntDictOptions('hospital_bed_status')" :key="opt.value" :label="opt.label" :value="opt.value" />
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
      <el-table-column label="病房" width="120">
        <template #default="{ row }">{{ getWardName(row.wardId) }}</template>
      </el-table-column>
      <el-table-column label="床位号" prop="bedNo" width="100" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="getDictColorType('hospital_bed_status', row.status)">
            {{ getDictLabel('hospital_bed_status', row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="患者ID" prop="patientId" width="80">
        <template #default="{ row }">{{ row.patientId || '-' }}</template>
      </el-table-column>
      <el-table-column label="入住时间" prop="admissionTime" width="180">
        <template #default="{ row }">{{ row.admissionTime || '-' }}</template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openForm('update', row.id)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
          <el-button v-if="row.status === 0 || row.status === '0'" link type="success" @click="handleAssign(row.id)">分配</el-button>
          <el-button v-if="row.status === 1 || row.status === '1'" link type="warning" @click="handleRelease(row.id)">释放</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="mt-15px" v-model:current-page="queryParams.pageNo" v-model:page-size="queryParams.pageSize"
      :total="total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
      @size-change="getList" @current-change="getList" />

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="病房" required>
          <el-select v-model="formData.wardId" placeholder="请选择病房" filterable style="width:100%;">
            <el-option v-for="w in wardOptions" :key="w.id" :label="w.wardNo" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="床位号" required><el-input v-model="formData.bedNo" placeholder="如: A-101-1" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配弹窗 -->
    <el-dialog v-model="assignVisible" title="床位分配" width="400px">
      <el-form label-width="80px">
        <el-form-item label="患者ID" required><el-input v-model.number="assignPatientId" placeholder="请输入患者ID" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssign" :loading="assigning">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBedPage, getBed, createBed, updateBed, deleteBed, assignBed, releaseBed } from '@/api/hospital/bed'
import { getWardPage } from '@/api/hospital/ward'
import { getIntDictOptions, getDictLabel, getDictColorType } from '@/utils/hospitalDict'

defineOptions({ name: 'HospitalBed' })

// 病房下拉
const wardOptions = ref<{ id: number; wardNo: string }[]>([])
const loadWards = async () => {
  try {
    const res = await getWardPage({ pageNo: 1, pageSize: 200 })
    wardOptions.value = (res.list || []).map((w: any) => ({ id: w.id, wardNo: w.wardNo }))
  } catch { /* ignore */ }
}
const getWardName = (wardId: number) => wardOptions.value.find(w => w.id === wardId)?.wardNo || String(wardId || '')

const loading = ref(false)
const list = ref([])
const total = ref(0)
const queryParams = reactive({ pageNo: 1, pageSize: 10, bedNo: undefined, wardId: undefined as any, status: undefined })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitting = ref(false)
const formData = reactive({ id: undefined as any, wardId: undefined as any, bedNo: '' })
const assignVisible = ref(false)
const assignBedId = ref<number>()
const assignPatientId = ref<number>()
const assigning = ref(false)

const getList = async () => {
  loading.value = true
  try {
    const res = await getBedPage(queryParams)
    list.value = res.list || []
    total.value = res.total || 0
  } finally { loading.value = false }
}

const handleQuery = () => { queryParams.pageNo = 1; getList() }
const resetQuery = () => { queryParams.bedNo = undefined; queryParams.wardId = undefined; queryParams.status = undefined; handleQuery() }

const openForm = async (type: string, id?: number) => {
  dialogTitle.value = type === 'create' ? '新增床位' : '编辑床位'
  if (type === 'update' && id) {
    const res = await getBed(id)
    Object.assign(formData, res)
  } else {
    Object.assign(formData, { id: undefined, wardId: undefined, bedNo: '' })
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formData.bedNo) { ElMessage.warning('请输入床位号'); return }
  submitting.value = true
  try {
    if (formData.id) { await updateBed(formData as any) } else { await createBed(formData as any) }
    ElMessage.success(formData.id ? '修改成功' : '新增成功')
    dialogVisible.value = false; getList()
  } finally { submitting.value = false }
}

const handleDelete = async (id: number) => {
  await ElMessageBox.confirm('确定删除该床位吗？', '提示', { type: 'warning' })
  await deleteBed(id)
  ElMessage.success('删除成功'); getList()
}

const handleAssign = (bedId: number) => {
  assignBedId.value = bedId
  assignPatientId.value = undefined
  assignVisible.value = true
}

const submitAssign = async () => {
  if (!assignPatientId.value) { ElMessage.warning('请输入患者ID'); return }
  assigning.value = true
  try {
    await assignBed(assignBedId.value!, assignPatientId.value!)
    ElMessage.success('分配成功')
    assignVisible.value = false; getList()
  } finally { assigning.value = false }
}

const handleRelease = async (bedId: number) => {
  await ElMessageBox.confirm('确定释放该床位吗？', '提示', { type: 'warning' })
  await releaseBed(bedId)
  ElMessage.success('释放成功'); getList()
}

onMounted(async () => {
  await loadWards()
  getList()
})
</script>
