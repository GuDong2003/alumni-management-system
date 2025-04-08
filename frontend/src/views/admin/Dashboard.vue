<template>
  <div class="dashboard-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="statistics-row">
      <el-col :span="6">
        <el-card class="statistics-card">
          <template #header>
            <div class="card-header">
              <span>用户总数</span>
              <el-icon><User /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ statistics.totalUsers || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="statistics-card">
          <template #header>
            <div class="card-header">
              <span>活动总数</span>
              <el-icon><Calendar /></el-icon>
            </div>
          </template>
          <div class="card-value">
            {{ statistics.totalActivities || 0 }}
            <div class="card-sub-value">进行中: {{ statistics.activeActivities || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="statistics-card">
          <template #header>
            <div class="card-header">
              <span>捐赠总额</span>
              <el-icon><Money /></el-icon>
            </div>
          </template>
          <div class="card-value">¥{{ formatAmount(statistics.totalDonationAmount) }}</div>
          <div class="card-sub-value">捐赠次数: {{ statistics.totalDonations || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="statistics-card">
          <template #header>
            <div class="card-header">
              <span>反馈总数</span>
              <el-icon><Message /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ statistics.totalFeedback || 0 }}</div>
          <div class="card-sub-value">待处理: {{ statistics.pendingFeedback || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 公告轮播 -->
    <el-card class="announcement-card">
      <template #header>
        <div class="card-header">
          <span>最新公告</span>
        </div>
      </template>
      <div class="announcements-container">
        <div v-if="publishedAnnouncements.length > 0" class="announcements-list">
          <div v-for="announcement in publishedAnnouncements" 
               :key="announcement.id" 
               class="announcement-item"
               :class="{ 'important': isImportantAnnouncement(announcement) }">
            <div class="announcement-header">
              <div class="announcement-title">
                <el-tag 
                  :type="isImportantAnnouncement(announcement) ? 'danger' : 'info'"
                  size="small"
                  class="announcement-tag"
                >
                  {{ isImportantAnnouncement(announcement) ? '重要' : '通知' }}
                </el-tag>
                <h3>{{ announcement.title }}</h3>
              </div>
              <div class="announcement-time">
                {{ formatDateTime(announcement.publishTime) }}
              </div>
            </div>
            <div class="announcement-content">
              <p>{{ announcement.content }}</p>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无公告" />
      </div>
    </el-card>

    <!-- 进行中的活动 -->
    <el-card class="activity-card">
      <template #header>
        <div class="card-header">
          <span>进行中的活动</span>
        </div>
      </template>
      <el-table :data="ongoingActivities" style="width: 100%" v-loading="loading">
        <el-table-column prop="title" label="活动名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getActivityTypeTag(row.type).type" effect="plain">
              {{ getActivityTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="160" align="center">
          <template #default="{ row }">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="160" align="center">
          <template #default="{ row }">
            {{ formatDateTime(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="location" label="地点" min-width="150" show-overflow-tooltip />
        <el-table-column prop="maxParticipants" label="人数限制" width="100" align="center" />
      </el-table>
      <el-empty v-if="ongoingActivities.length === 0" description="暂无进行中的活动" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { User, Calendar, Money, Message } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

// 统计数据
const statistics = ref({})
const loading = ref(false)

// 公告列表
const announcements = ref([])
const publishedAnnouncements = computed(() => {
  return announcements.value
    .filter(a => a.status === 'PUBLISHED')
    .sort((a, b) => new Date(b.publishTime) - new Date(a.publishTime))
})

// 活动列表
const activities = ref([])
const ongoingActivities = computed(() => {
  return activities.value
    .filter(a => a.status === 'ONGOING')
    .sort((a, b) => new Date(a.startTime) - new Date(b.startTime))
})

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const response = await request({
      url: '/api/statistics',
      method: 'get'
    })
    statistics.value = response
  } catch (error) {
    console.error('获取统计数据失败:', error)
    ElMessage.error('获取统计数据失败')
  }
}

// 获取公告列表
const fetchAnnouncements = async () => {
  try {
    const response = await request({
      url: '/api/announcements',
      method: 'get'
    })
    announcements.value = Array.isArray(response) ? response : []
  } catch (error) {
    console.error('获取公告列表失败:', error)
    ElMessage.error('获取公告列表失败')
    announcements.value = []
  }
}

// 获取活动列表
const fetchActivities = async () => {
  loading.value = true
  try {
    const response = await request({
      url: '/api/activities',
      method: 'get'
    })
    activities.value = Array.isArray(response) ? response : []
  } catch (error) {
    console.error('获取活动列表失败:', error)
    ElMessage.error('获取活动列表失败')
    activities.value = []
  } finally {
    loading.value = false
  }
}

// 格式化金额
const formatAmount = (amount) => {
  if (!amount) return '0.00'
  return Number(amount).toFixed(2)
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取活动类型标签
const getActivityTypeTag = (type) => {
  const typeMap = {
    'REUNION': { type: 'danger', color: '#ffcdd2', textColor: '#c62828' },
    'SHARING': { type: 'success', color: '#c8e6c9', textColor: '#2e7d32' },
    'CAREER': { type: 'warning', color: '#fff9c4', textColor: '#f57f17' },
    'SPORTS': { type: 'primary', color: '#bbdefb', textColor: '#1565c0' },
    'MENTORING': { type: 'info', color: '#e1bee7', textColor: '#6a1b9a' },
    'DONATION': { type: 'warning', color: '#ffe0b2', textColor: '#e65100' },
    'CULTURE': { type: 'success', color: '#b2dfdb', textColor: '#00695c' },
    'FORUM': { type: 'primary', color: '#b3e5fc', textColor: '#01579b' },
    'WELCOME': { type: 'info', color: '#d1c4e9', textColor: '#4527a0' },
    'ANNUAL': { type: '', color: '#cfd8dc', textColor: '#263238' }
  }
  return typeMap[type] || { type: 'info', color: '#f5f5f5', textColor: '#757575' }
}

// 获取活动类型文本
const getActivityTypeText = (type) => {
  const typeMap = {
    'REUNION': '返校日',
    'SHARING': '分享会',
    'CAREER': '招聘会',
    'SPORTS': '体育赛事',
    'MENTORING': '导师计划',
    'DONATION': '捐赠仪式',
    'CULTURE': '文化活动',
    'FORUM': '论坛',
    'WELCOME': '迎新晚会',
    'ANNUAL': '年度大会'
  }
  return typeMap[type] || type
}

// 在 script setup 部分添加
const isImportantAnnouncement = (announcement) => {
  // 根据标题或内容判断是否为重要公告
  const importantKeywords = ['重要', '紧急', '通知', '公告'];
  return importantKeywords.some(keyword => 
    announcement.title.includes(keyword) || 
    announcement.content.includes(keyword)
  );
}

// 页面加载时获取数据
onMounted(() => {
  fetchStatistics()
  fetchAnnouncements()
  fetchActivities()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.statistics-row {
  margin-bottom: 20px;
}

.statistics-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  text-align: center;
  margin: 10px 0;
}

.card-sub-value {
  font-size: 14px;
  color: #909399;
  text-align: center;
}

.announcement-card,
.activity-card {
  margin-bottom: 20px;
}

.announcements-container {
  padding: 10px;
}

.announcements-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.announcement-item {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  border: 1px solid #ebeef5;
}

.announcement-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.1);
}

.announcement-item.important {
  border-left: 4px solid #f56c6c;
}

.announcement-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.announcement-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.announcement-title h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
  font-weight: 600;
}

.announcement-tag {
  flex-shrink: 0;
}

.announcement-time {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
}

.announcement-content {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
}

.announcement-content p {
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style> 