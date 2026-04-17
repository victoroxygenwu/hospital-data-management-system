<template>
  <div class="prescription-list-container">
    <el-card>
      <template #header>
        <span>处方列表</span>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="就诊ID">
          <el-input v-model.number="searchForm.visitId" placeholder="请输入就诊ID" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option v-for="opt in getIntDictOptions('hospital_prescription_status')" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchPrescriptions">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="prescriptionList" border stripe v-loading="loading">
        <el-table-column prop="id" label="处方ID" width="80" />
        <el-table-column prop="visitId" label="就诊ID" width="80" />
        <el-table-column prop="doctorId" label="医生ID" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getDictColorType('hospital_prescription_status', row.status)">
              {{ getDictLabel('hospital_prescription_status', row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="notes" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="viewDetail(row)">查看详情</el-button>
            <el-button v-if="row.status === 0 || row.status === '0'" type="success" size="small" link @click="handleDispense(row)">发药</el-button>
            <el-button v-if="row.status === 0 || row.status === '0'" type="danger" size="small" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination v-model:current-page="pageParams.pageNo" v-model:page-size="pageParams.pageSize"
          :total="total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadPrescriptionList" @current-change="loadPrescriptionList" />
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="处方详情" width="600px">
      <div v-if="currentPrescription">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="处方ID">{{ currentPrescription.id }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getDictColorType('hospital_prescription_status', currentPrescription.status)">
              {{ getDictLabel('hospital_prescription_status', currentPrescription.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="就诊ID">{{ currentPrescription.visitId }}</el-descriptions-item>
          <el-descriptions-item label="医生ID">{{ currentPrescription.doctorId }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ currentPrescription.notes || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentPrescription.createTime }}</el-descriptions-item>
        </el-descriptions>
        <el-divider>药品明细</el-divider>
        <el-table :data="currentPrescription.items || []" border size="small">
          <el-table-column prop="medicineId" label="药品ID" width="80" />
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="price" label="单价" width="80">
            <template #default="{ row }">¥{{ row.price }}</template>
          </el-table-column>
          <el-table-column prop="instructions" label="用法说明" min-width="120" />
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPrescriptionPage, getPrescription, deletePrescription, dispensePrescription } from '@/api/hospital/prescription'
import { getIntDictOptions, getDictLabel, getDictColorType } from '@/utils/hospitalDict'

const searchForm = reactive({ visitId: undefined as any, status: undefined as any })
const pageParams = reactive({ pageNo: 1, pageSize: 10 })
const total = ref(0)
const loading = ref(false)
const prescriptionList = ref<any[]>([])
const detailVisible = ref(false)
const currentPrescription = ref<any>(null)

const loadPrescriptionList = async () => {
  loading.value = true
  try {
    const res = await getPrescriptionPage({ ...searchForm, ...pageParams })
    prescriptionList.value = res.list || []
    total.value = res.total || 0
  } catch (e) {
    console.error('加载处方列表失败', e)
    ElMessage.error('加载处方列表失败')
  } finally { loading.value = false }
}

const searchPrescriptions = () => { pageParams.pageNo = 1; loadPrescriptionList() }
const resetSearch = () => { searchForm.visitId = undefined; searchForm.status = undefined; searchPrescriptions() }

const viewDetail = async (row: any) => {
  try {
    const res = await getPrescription(row.id)
    currentPrescription.value = res
    detailVisible.value = true
  } catch (e) { ElMessage.error('获取处方详情失败') }
}

const handleDispense = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定要为处方 #${row.id} 发药吗？`, '提示', { type: 'warning' })
    await dispensePrescription(row.id)
    ElMessage.success('发药成功')
    loadPrescriptionList()
  } catch { /* cancelled */ }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定要删除处方 #${row.id} 吗？`, '提示', { type: 'warning' })
    await deletePrescription(row.id)
    ElMessage.success('删除成功')
    loadPrescriptionList()
  } catch { /* cancelled */ }
}

onMounted(() => { loadPrescriptionList() })
</script>

<style scoped>
.prescription-list-container { padding: 20px; }
.search-form { margin-bottom: 20px; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
