<template>
  <div class="my-registration-container">
    <el-card>
      <template #header>
        <span>我的挂号记录</span>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="患者ID">
          <el-input v-model.number="searchForm.patientId" placeholder="请输入患者ID" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待就诊" :value="0" />
            <el-option label="就诊中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchRegistrations">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 挂号记录表格 -->
      <el-table :data="registrationList" border stripe v-loading="loading">
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
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="viewDetail(row)">查看详情</el-button>
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
          @size-change="loadRegistrationList"
          @current-change="loadRegistrationList"
        />
      </div>
    </el-card>

    <!-- 就诊详情弹窗 -->
    <el-dialog v-model="detailVisible" title="就诊详情" width="500px">
      <div v-if="currentRegistration">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="就诊ID">{{ currentRegistration.id }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentRegistration.status)">{{ getStatusText(currentRegistration.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="患者ID">{{ currentRegistration.patientId }}</el-descriptions-item>
          <el-descriptions-item label="医生ID">{{ currentRegistration.doctorId }}</el-descriptions-item>
          <el-descriptions-item label="科室ID">{{ currentRegistration.deptId }}</el-descriptions-item>
          <el-descriptions-item label="就诊日期">{{ currentRegistration.visitDate }}</el-descriptions-item>
          <el-descriptions-item label="诊断结果" :span="2">{{ currentRegistration.diagnosis || '暂无' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">{{ currentRegistration.createTime }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getVisitPage, getVisit } from '@/api/hospital/visit'

// 搜索表单
const searchForm = reactive({
  patientId: undefined,
  status: undefined
})

// 分页参数
const pageParams = reactive({
  pageNo: 1,
  pageSize: 10
})

const total = ref(0)
const loading = ref(false)
const registrationList = ref([])
const detailVisible = ref(false)
const currentRegistration = ref(null)

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

// 加载挂号记录列表
const loadRegistrationList = async () => {
  loading.value = true
  try {
    const res = await getVisitPage({ ...searchForm, ...pageParams })
    registrationList.value = res.list || []
    total.value = res.total || 0
  } catch (e) {
    console.error('加载挂号记录失败', e)
    ElMessage.error('加载挂号记录失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const searchRegistrations = () => {
  pageParams.pageNo = 1
  loadRegistrationList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.patientId = undefined
  searchForm.status = undefined
  searchRegistrations()
}

// 查看详情
const viewDetail = async (row) => {
  try {
    const res = await getVisit(row.id)
    currentRegistration.value = res
    detailVisible.value = true
  } catch (e) {
    console.error('获取就诊详情失败', e)
    ElMessage.error('获取就诊详情失败')
  }
}

// 初始化
onMounted(() => {
  loadRegistrationList()
})
</script>

<style scoped>
.my-registration-container {
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
