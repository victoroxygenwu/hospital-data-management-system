<template>
  <div class="register-container">
    <el-card>
      <template #header>
        <span>挂号管理</span>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="患者ID">
          <el-input v-model.number="searchForm.patientId" placeholder="请输入患者ID" clearable />
        </el-form-item>
        <el-form-item label="医生ID">
          <el-input v-model.number="searchForm.doctorId" placeholder="请输入医生ID" clearable />
        </el-form-item>
        <el-form-item label="科室ID">
          <el-input v-model.number="searchForm.deptId" placeholder="请输入科室ID" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchVisits">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 新增挂号按钮 -->
      <div style="margin-bottom: 16px;">
        <el-button type="primary" @click="showCreateDialog">新增挂号</el-button>
      </div>

      <!-- 就诊记录表格 -->
      <el-table :data="visitList" border stripe v-loading="loading">
        <el-table-column prop="id" label="就诊ID" width="80" />
        <el-table-column prop="patientId" label="患者ID" width="80" />
        <el-table-column prop="doctorId" label="医生ID" width="80" />
        <el-table-column prop="deptId" label="科室ID" width="80" />
        <el-table-column prop="visitDate" label="就诊日期" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="diagnosis" label="诊断" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="viewDetail(row)">详情</el-button>
            <el-button
              v-if="row.status === 0"
              type="danger"
              size="small"
              link
              @click="handleDelete(row)"
            >取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pageParams.pageNo"
          v-model:page-size="pageParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadVisitList"
          @current-change="loadVisitList"
        />
      </div>
    </el-card>

    <!-- 新增挂号弹窗 -->
    <el-dialog v-model="createDialogVisible" title="新增挂号" width="500px">
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="患者ID" required>
          <el-input v-model.number="createForm.patientId" placeholder="请输入患者ID" />
        </el-form-item>
        <el-form-item label="医生ID" required>
          <el-input v-model.number="createForm.doctorId" placeholder="请输入医生ID" />
        </el-form-item>
        <el-form-item label="科室ID" required>
          <el-input v-model.number="createForm.deptId" placeholder="请输入科室ID" />
        </el-form-item>
        <el-form-item label="就诊日期" required>
          <el-date-picker v-model="createForm.visitDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate" :loading="creating">确定挂号</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="就诊详情" width="500px">
      <div v-if="currentVisit">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="就诊ID">{{ currentVisit.id }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentVisit.status)">{{ getStatusText(currentVisit.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="患者ID">{{ currentVisit.patientId }}</el-descriptions-item>
          <el-descriptions-item label="医生ID">{{ currentVisit.doctorId }}</el-descriptions-item>
          <el-descriptions-item label="科室ID">{{ currentVisit.deptId }}</el-descriptions-item>
          <el-descriptions-item label="就诊日期">{{ currentVisit.visitDate }}</el-descriptions-item>
          <el-descriptions-item label="诊断结果" :span="2">{{ currentVisit.diagnosis || '暂无' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getVisitPage, getVisit, createVisit, deleteVisit } from '@/api/hospital/visit'

// 搜索表单
const searchForm = reactive({
  patientId: undefined,
  doctorId: undefined,
  deptId: undefined
})

// 分页参数
const pageParams = reactive({
  pageNo: 1,
  pageSize: 10
})

const total = ref(0)
const loading = ref(false)
const visitList = ref([])
const detailVisible = ref(false)
const currentVisit = ref(null)
const createDialogVisible = ref(false)
const creating = ref(false)

const createForm = reactive({
  patientId: undefined,
  doctorId: undefined,
  deptId: undefined,
  visitDate: ''
})

// 获取状态文本
const getStatusText = (status) => {
  const map = { 0: '待就诊', 1: '就诊中', 2: '已完成', 3: '已取消' }
  return map[status] ?? '未知'
}

// 获取状态标签类型
const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'danger' }
  return map[status] ?? 'info'
}

// 加载就诊记录列表
const loadVisitList = async () => {
  loading.value = true
  try {
    const res = await getVisitPage({ ...searchForm, ...pageParams })
    visitList.value = res.list || []
    total.value = res.total || 0
  } catch (e) {
    console.error('加载就诊记录失败', e)
    ElMessage.error('加载就诊记录失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const searchVisits = () => {
  pageParams.pageNo = 1
  loadVisitList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.patientId = undefined
  searchForm.doctorId = undefined
  searchForm.deptId = undefined
  searchVisits()
}

// 查看详情
const viewDetail = async (row) => {
  try {
    const res = await getVisit(row.id)
    currentVisit.value = res
    detailVisible.value = true
  } catch (e) {
    console.error('获取就诊详情失败', e)
    ElMessage.error('获取就诊详情失败')
  }
}

// 显示新增挂号弹窗
const showCreateDialog = () => {
  createForm.patientId = undefined
  createForm.doctorId = undefined
  createForm.deptId = undefined
  createForm.visitDate = ''
  createDialogVisible.value = true
}

// 提交新增挂号
const submitCreate = async () => {
  if (!createForm.patientId) {
    ElMessage.warning('请填写患者ID')
    return
  }
  if (!createForm.doctorId) {
    ElMessage.warning('请填写医生ID')
    return
  }
  if (!createForm.deptId) {
    ElMessage.warning('请填写科室ID')
    return
  }
  if (!createForm.visitDate) {
    ElMessage.warning('请选择就诊日期')
    return
  }

  try {
    creating.value = true
    await createVisit(createForm)
    ElMessage.success('挂号成功！')
    createDialogVisible.value = false
    loadVisitList()
  } catch (e) {
    console.error('挂号失败', e)
    ElMessage.error('挂号失败，请重试')
  } finally {
    creating.value = false
  }
}

// 取消挂号
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消该挂号吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteVisit(row.id)
    ElMessage.success('已取消')
    loadVisitList()
  } catch {
    // 取消操作
  }
}

// 初始化
onMounted(() => {
  loadVisitList()
})
</script>

<style scoped>
.register-container {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
