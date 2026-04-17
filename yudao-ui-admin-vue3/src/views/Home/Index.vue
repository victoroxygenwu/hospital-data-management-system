<template>
  <div class="dashboard">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-content">
        <div class="welcome-text">
          <h1 class="welcome-title">欢迎使用智慧医院管理系统</h1>
          <p class="welcome-subtitle">让医疗服务更高效，让就医体验更便捷</p>
        </div>
        <div class="welcome-illustration">
          <el-icon :size="80"><FirstAidKit /></el-icon>
        </div>
      </div>
    </div>

    <!-- 医院公告 / 温馨提醒 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="notice-card">
          <template #header>
            <span>📢 医院公告</span>
          </template>
          <div class="notice-list">
            <div v-for="notice in notices" :key="notice.id" class="notice-item">
              <span class="notice-title">{{ notice.title }}</span>
              <span class="notice-time">{{ notice.time }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="tip-card">
          <template #header>
            <span>💡 温馨提示</span>
          </template>
          <div class="tip-list">
            <div class="tip-item">📋 请按时就诊，过时号源将自动作废</div>
            <div class="tip-item">💊 取药请到药房窗口，凭处方单取药</div>
            <div class="tip-item">🩺 就诊前请准备好身份证和就诊卡</div>
            <div class="tip-item">📞 如有疑问，请拨打服务热线：400-xxx-xxxx</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { FirstAidKit } from '@element-plus/icons-vue'

// 医院公告
const notices = ref([
  { id: 1, title: '关于2026年五一假期门诊安排的通知', time: '2026-04-10' },
  { id: 2, title: '皮肤科新引进专家门诊公告', time: '2026-04-08' },
  { id: 3, title: '医保系统升级维护通知', time: '2026-04-05' },
  { id: 4, title: '儿科夜间急诊延长至22:00', time: '2026-04-01' }
])
</script>

<style scoped>
.dashboard {
  padding: 20px;
  background-color: #f0f2f6;
  min-height: calc(100vh - 100px);
}

/* 欢迎横幅 */
.welcome-banner {
  background: linear-gradient(135deg, #409eff 0%, #66b3ff 100%);
  border-radius: 16px;
  padding: 32px 40px;
  margin-bottom: 24px;
  color: white;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-text {
  flex: 1;
}

.welcome-title {
  font-size: 28px;
  margin-bottom: 12px;
  font-weight: 600;
}

.welcome-subtitle {
  font-size: 14px;
  opacity: 0.9;
}

.welcome-illustration {
  background-color: rgba(255, 255, 255, 0.15);
  width: 120px;
  height: 120px;
  border-radius: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 公告和提醒卡片 */
.notice-card,
.tip-card {
  height: 300px;
}

.notice-list,
.tip-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.notice-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
}

.notice-item:hover {
  color: #409eff;
}

.notice-title {
  font-size: 14px;
}

.notice-time {
  font-size: 12px;
  color: #909399;
}

.tip-item {
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
  font-size: 14px;
  color: #606266;
}

.tip-item:last-child {
  border-bottom: none;
}
</style>
