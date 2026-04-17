<template>
  <div class="p-20px">
    <h3 class="mb-15px">数据统计分析</h3>

    <!-- 就诊趋势 -->
    <el-card class="mb-15px">
      <template #header><span>就诊趋势统计</span></template>
      <el-table :data="visitTrendData" border stripe v-loading="trendLoading">
        <el-table-column label="日期" prop="date" width="150" />
        <el-table-column label="就诊人次" prop="count" width="100" />
      </el-table>
      <el-empty v-if="!trendLoading && visitTrendData.length === 0" description="暂无就诊趋势数据" />
    </el-card>

    <!-- 床位使用率 -->
    <el-card class="mb-15px">
      <template #header><span>床位使用率统计</span></template>
      <el-table :data="wardUsageData" border stripe v-loading="usageLoading">
        <el-table-column label="病房编号" prop="wardNo" width="150" />
        <el-table-column label="总床位" prop="capacity" width="80" />
        <el-table-column label="已用床位" prop="usedBeds" width="80" />
        <el-table-column label="使用率" width="100">
          <template #default="{ row }">
            <el-tag :type="parseFloat(row.usageRate) > 80 ? 'danger' : parseFloat(row.usageRate) > 50 ? 'warning' : 'success'">
              {{ row.usageRate }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!usageLoading && wardUsageData.length === 0" description="暂无床位使用率数据" />
    </el-card>

    <!-- 药品库存 -->
    <el-card>
      <template #header><span>药品库存统计</span></template>
      <el-table :data="medicineStockData" border stripe v-loading="stockLoading">
        <el-table-column label="药品名称" prop="name" width="150" />
        <el-table-column label="规格" prop="specification" width="120" />
        <el-table-column label="单位" prop="unit" width="60" />
        <el-table-column label="库存量" prop="stock" width="80" />
        <el-table-column label="有效期" prop="expiryDate" width="120" />
        <el-table-column label="库存预警" width="100">
          <template #default="{ row }">
            <el-tag :type="row.stockWarning ? 'danger' : 'success'">{{ row.stockWarning ? '库存不足' : '正常' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!stockLoading && medicineStockData.length === 0" description="暂无药品库存数据" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getVisitTrend, getWardUsage, getMedicineStock } from '@/api/hospital/stats'

defineOptions({ name: 'HospitalStats' })

const visitTrendData = ref<any[]>([])
const wardUsageData = ref<any[]>([])
const medicineStockData = ref<any[]>([])
const trendLoading = ref(false)
const usageLoading = ref(false)
const stockLoading = ref(false)

const loadData = async () => {
  trendLoading.value = true
  try { visitTrendData.value = (await getVisitTrend()) || [] } catch { visitTrendData.value = [] }
  finally { trendLoading.value = false }

  usageLoading.value = true
  try { wardUsageData.value = (await getWardUsage()) || [] } catch { wardUsageData.value = [] }
  finally { usageLoading.value = false }

  stockLoading.value = true
  try { medicineStockData.value = (await getMedicineStock()) || [] } catch { medicineStockData.value = [] }
  finally { stockLoading.value = false }
}

onMounted(() => loadData())
</script>
