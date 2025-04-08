<template>
  <div class="dashboard-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="statistics-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="statistics-card">
          <template #header>
            <div class="card-header">
              <span>我的活动</span>
            </div>
          </template>
          <div class="card-content">
            <div class="statistics-value">{{ statistics.myActivities || 0 }}</div>
            <div class="statistics-label">参与的活动数</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="statistics-card">
          <template #header>
            <div class="card-header">
              <span>我的捐赠</span>
            </div>
          </template>
          <div class="card-content">
            <div class="statistics-value">{{ statistics.myDonations || 0 }}</div>
            <div class="statistics-label">捐赠次数</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="statistics-card">
          <template #header>
            <div class="card-header">
              <span>捐赠总额</span>
            </div>
          </template>
          <div class="card-content">
            <div class="statistics-value">¥{{ statistics.myDonationAmount || 0 }}</div>
            <div class="statistics-label">累计捐赠金额</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="statistics-card">
          <template #header>
            <div class="card-header">
              <span>我的反馈</span>
            </div>
          </template>
          <div class="card-content">
            <div class="statistics-value">{{ statistics.myFeedback || 0 }}</div>
            <div class="statistics-label">提交的反馈数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 公告和活动区域 -->
    <el-row :gutter="20" class="main-content">
      <!-- 公告区域 -->
      <el-col :span="12">
        <el-card class="announcement-card">
          <template #header>
            <div class="card-header">
              <span>最新公告</span>
            </div>
          </template>
          <div class="announcement-list">
            <div v-if="announcements.length === 0" class="no-data">
              暂无公告
            </div>
            <div
              v-for="announcement in announcements"
              :key="announcement.id"
              class="announcement-item"
              :class="{ 'important': isImportantAnnouncement(announcement) }"
            >
              <div class="announcement-header">
                <h3 class="announcement-title">{{ announcement.title }}</h3>
                <el-tag
                  :type="isImportantAnnouncement(announcement) ? 'danger' : 'info'"
                  size="small"
                >
                  {{ isImportantAnnouncement(announcement) ? '重要' : '通知' }}
                </el-tag>
              </div>
              <div class="announcement-content">{{ announcement.content }}</div>
              <div class="announcement-footer">
                <span class="announcement-time">
                  发布时间：{{ formatDate(announcement.publishTime) }}
                </span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 活动区域 -->
      <el-col :span="12">
        <el-card class="activity-card">
          <template #header>
            <div class="card-header">
              <span>最新活动</span>
            </div>
          </template>
          <div class="activity-list">
            <div v-if="activities.length === 0" class="no-data">
              暂无活动
            </div>
            <div
              v-for="activity in activities"
              :key="activity.id"
              class="activity-item"
            >
              <div class="activity-header">
                <h3 class="activity-title">{{ activity.title }}</h3>
                <el-tag
                  :type="getActivityStatusType(activity.status)"
                  size="small"
                >
                  {{ getActivityStatusText(activity.status) }}
                </el-tag>
              </div>
              <div class="activity-content">{{ activity.description }}</div>
              <div class="activity-footer">
                <span class="activity-time">
                  活动时间：{{ formatDate(activity.startTime) }}
                </span>
                <el-button
                  type="primary"
                  size="small"
                  :disabled="!canJoinActivity(activity)"
                  @click="handleJoinActivity(activity)"
                >
                  {{ getJoinButtonText(activity) }}
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import dayjs from 'dayjs'

// 统计数据
const statistics = ref({
  myActivities: 0,
  myDonations: 0,
  myDonationAmount: 0,
  myFeedback: 0
})

// 公告列表
const announcements = ref([])

// 活动列表
const activities = ref([])

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const response = await request({
      url: '/api/statistics/my',
      method: 'get'
    })
    if (response && response.success) {
      statistics.value = response.data
    }
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
    if (response && response.success) {
      announcements.value = response.data
    }
  } catch (error) {
    console.error('获取公告列表失败:', error)
    ElMessage.error('获取公告列表失败')
  }
}

// 获取活动列表
const fetchActivities = async () => {
  try {
    const response = await request({
      url: '/api/activities',
      method: 'get'
    })
    if (response && response.success) {
      activities.value = response.data
    }
  } catch (error) {
    console.error('获取活动列表失败:', error)
    ElMessage.error('获取活动列表失败')
  }
}

// 判断是否为重要公告
const isImportantAnnouncement = (announcement) => {
  const importantKeywords = ['重要', '紧急', '通知', '公告']
  return importantKeywords.some(keyword => 
    announcement.title.includes(keyword) || 
    announcement.content.includes(keyword)
  )
}

// 格式化日期
const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

// 获取活动状态类型
const getActivityStatusType = (status) => {
  const statusMap = {
    'DRAFT': 'info',
    'PUBLISHED': 'success',
    'ONGOING': 'warning',
    'COMPLETED': 'info',
    'CANCELLED': 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取活动状态文本
const getActivityStatusText = (status) => {
  const statusMap = {
    'DRAFT': '草稿',
    'PUBLISHED': '已发布',
    'ONGOING': '进行中',
    'COMPLETED': '已结束',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

// 判断是否可以参加活动
const canJoinActivity = (activity) => {
  return activity.status === 'PUBLISHED' || activity.status === 'ONGOING'
}

// 获取参加按钮文本
const getJoinButtonText = (activity) => {
  if (activity.status === 'COMPLETED') {
    return '已结束'
  } else if (activity.status === 'CANCELLED') {
    return '已取消'
  } else if (activity.status === 'ONGOING') {
    return '参加中'
  } else {
    return '立即参加'
  }
}

// 处理参加活动
const handleJoinActivity = async (activity) => {
  try {
    const response = await request({
      url: `/api/activities/${activity.id}/join`,
      method: 'post'
    })
    if (response && response.success) {
      ElMessage.success('成功参加活动')
      // 刷新活动列表
      await fetchActivities()
    }
  } catch (error) {
    console.error('参加活动失败:', error)
    ElMessage.error('参加活动失败')
  }
}

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

.statistics-cards {
  margin-bottom: 20px;
}

.statistics-card {
  height: 180px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100px;
}

.statistics-value {
  font-size: 36px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 10px;
}

.statistics-label {
  font-size: 14px;
  color: #909399;
}

.main-content {
  margin-top: 20px;
}

.announcement-card,
.activity-card {
  height: 600px;
}

.announcement-list,
.activity-list {
  height: 500px;
  overflow-y: auto;
}

.announcement-item,
.activity-item {
  padding: 15px;
  border-bottom: 1px solid #EBEEF5;
  transition: all 0.3s;
}

.announcement-item:hover,
.activity-item:hover {
  background-color: #F5F7FA;
}

.announcement-item.important {
  border-left: 4px solid #f56c6c;
}

.announcement-header,
.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.announcement-title,
.activity-title {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
}

.announcement-content,
.activity-content {
  color: #606266;
  margin-bottom: 10px;
  line-height: 1.5;
}

.announcement-footer,
.activity-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #909399;
  font-size: 12px;
}

.no-data {
  text-align: center;
  color: #909399;
  padding: 20px;
}
</style> 