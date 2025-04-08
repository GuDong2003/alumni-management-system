<template>
  <div class="activities-container">
    <el-card class="activities-card">
      <template #header>
        <div class="card-header">
          <span>活动参与</span>
          <el-radio-group v-model="activeTab" size="small">
            <el-radio-button label="all">全部活动</el-radio-button>
            <el-radio-button label="joined">已参与</el-radio-button>
            <el-radio-button label="available">可参与</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      
      <div class="activities-content">
        <el-table
          v-loading="loading"
          :data="filteredActivities"
          style="width: 100%"
          border
        >
          <el-table-column prop="title" label="活动名称" min-width="180" />
          <el-table-column prop="description" label="活动描述" min-width="250" show-overflow-tooltip />
          <el-table-column prop="startTime" label="开始时间" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.startTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="endTime" label="结束时间" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.endTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="location" label="活动地点" width="150" show-overflow-tooltip />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getActivityStatusType(scope.row.status)">
                {{ getActivityStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="scope">
              <el-button
                type="primary"
                size="small"
                :disabled="!canJoinActivity(scope.row)"
                @click="handleJoinActivity(scope.row)"
              >
                {{ getJoinButtonText(scope.row) }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div v-if="filteredActivities.length === 0 && !loading" class="no-data">
          <el-empty description="暂无活动数据" />
        </div>
      </div>
    </el-card>
    
    <!-- 活动详情对话框 -->
    <el-dialog
      v-model="activityDialogVisible"
      :title="currentActivity.title"
      width="60%"
      :close-on-click-modal="true"
    >
      <div class="activity-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="活动名称">{{ currentActivity.title }}</el-descriptions-item>
          <el-descriptions-item label="活动状态">
            <el-tag :type="getActivityStatusType(currentActivity.status)">
              {{ getActivityStatusText(currentActivity.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ formatDate(currentActivity.startTime) }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{ formatDate(currentActivity.endTime) }}</el-descriptions-item>
          <el-descriptions-item label="活动地点">{{ currentActivity.location }}</el-descriptions-item>
          <el-descriptions-item label="参与人数">{{ currentActivity.participantCount || 0 }}人</el-descriptions-item>
          <el-descriptions-item label="活动描述" :span="2">{{ currentActivity.description }}</el-descriptions-item>
        </el-descriptions>
        
        <div class="activity-actions">
          <el-button
            type="primary"
            :disabled="!canJoinActivity(currentActivity)"
            @click="handleJoinActivity(currentActivity)"
          >
            {{ getJoinButtonText(currentActivity) }}
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import dayjs from 'dayjs'

// 活动列表
const activities = ref([])
const loading = ref(false)
const activeTab = ref('all')
const activityDialogVisible = ref(false)
const currentActivity = ref({})

// 获取活动列表
const fetchActivities = async () => {
  loading.value = true
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
  } finally {
    loading.value = false
  }
}

// 获取我参与的活动
const fetchMyActivities = async () => {
  try {
    const response = await request({
      url: '/api/activities/my',
      method: 'get'
    })
    if (response && response.success) {
      // 标记已参与的活动
      const myActivityIds = response.data.map(activity => activity.id)
      activities.value = activities.value.map(activity => ({
        ...activity,
        isJoined: myActivityIds.includes(activity.id)
      }))
    }
  } catch (error) {
    console.error('获取我参与的活动失败:', error)
    ElMessage.error('获取我参与的活动失败')
  }
}

// 根据标签过滤活动
const filteredActivities = computed(() => {
  if (activeTab.value === 'all') {
    return activities.value
  } else if (activeTab.value === 'joined') {
    return activities.value.filter(activity => activity.isJoined)
  } else if (activeTab.value === 'available') {
    return activities.value.filter(activity => 
      (activity.status === 'PUBLISHED' || activity.status === 'ONGOING') && !activity.isJoined
    )
  }
  return activities.value
})

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
  return (activity.status === 'PUBLISHED' || activity.status === 'ONGOING') && !activity.isJoined
}

// 获取参加按钮文本
const getJoinButtonText = (activity) => {
  if (activity.isJoined) {
    return '已参与'
  } else if (activity.status === 'COMPLETED') {
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
  if (!canJoinActivity(activity)) {
    return
  }
  
  try {
    const response = await request({
      url: `/api/activities/${activity.id}/join`,
      method: 'post'
    })
    if (response && response.success) {
      ElMessage.success('成功参加活动')
      // 刷新活动列表
      await fetchActivities()
      await fetchMyActivities()
    }
  } catch (error) {
    console.error('参加活动失败:', error)
    ElMessage.error('参加活动失败')
  }
}

// 查看活动详情
const viewActivityDetail = (activity) => {
  currentActivity.value = activity
  activityDialogVisible.value = true
}

onMounted(() => {
  fetchActivities().then(() => {
    fetchMyActivities()
  })
})
</script>

<style scoped>
.activities-container {
  padding: 20px;
}

.activities-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.activities-content {
  margin-top: 20px;
}

.no-data {
  margin-top: 20px;
  text-align: center;
}

.activity-detail {
  padding: 20px;
}

.activity-actions {
  margin-top: 20px;
  text-align: center;
}
</style> 