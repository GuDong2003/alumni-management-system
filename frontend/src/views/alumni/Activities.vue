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
          <el-table-column prop="status" label="活动状态" width="100">
            <template #default="scope">
              <el-tag :type="getActivityStatusType(scope.row.status)">
                {{ getActivityStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="participationStatus" label="参与状态" width="100">
            <template #default="scope">
              <el-tag :type="getParticipationStatusType(scope.row.participationStatus)">
                {{ getParticipationStatusText(scope.row.participationStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="scope">
              <el-button
                v-if="canJoinActivity(scope.row)"
                type="primary"
                size="small"
                @click="handleJoinActivity(scope.row)"
              >
                参与
              </el-button>
              <el-button
                v-if="canCancelJoin(scope.row)"
                type="danger"
                size="small"
                @click="handleCancelJoin(scope.row)"
              >
                取消参与
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
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
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
  loading.value = true;
  try {
    console.log('开始获取活动列表...');
    const response = await request({
      url: '/api/activities',
      method: 'get'
    });
    console.log('活动列表响应:', response);
    
    if (Array.isArray(response)) {
      console.log('活动数据:', response);
      activities.value = response;
      await fetchParticipationStatus();
    } else {
      console.error('获取活动列表失败: 响应格式不正确', response);
      ElMessage.error('获取活动列表失败: 数据格式不正确');
    }
  } catch (error) {
    console.error('获取活动列表失败:', error);
    ElMessage.error('获取活动列表失败: ' + (error.message || '未知错误'));
  } finally {
    loading.value = false;
  }
}

// 获取参与状态
const fetchParticipationStatus = async () => {
  try {
    console.log('开始获取参与状态...');
    const currentUserId = getCurrentUserId();
    const allActivities = activities.value;
    
    // 批量获取所有活动的参与状态
    const participationPromises = allActivities.map(activity => 
      request({
        url: `/api/activities/${activity.id}/participants`,
        method: 'get'
      }).then(response => ({
        activityId: activity.id,
        participants: Array.isArray(response) ? response : []
      }))
    );
    
    const participationResults = await Promise.all(participationPromises);
    console.log('参与状态结果:', participationResults);
    
    // 更新活动列表中的参与状态
    activities.value = allActivities.map(activity => {
      const result = participationResults.find(r => r.activityId === activity.id);
      const participant = result?.participants.find(p => p.userId === parseInt(currentUserId));
      return {
        ...activity,
        participationStatus: participant?.status || null
      };
    });
    
    console.log('更新后的活动列表:', activities.value);
  } catch (error) {
    console.error('获取参与状态失败:', error);
    ElMessage.error('获取参与状态失败: ' + (error.message || '未知错误'));
  }
}

// 根据标签过滤活动
const filteredActivities = computed(() => {
  console.log('当前活动列表:', activities.value);
  console.log('当前标签:', activeTab.value);
  
  const filtered = activities.value.filter(activity => {
    const isStatusValid = activity.status === 'PUBLISHED' || activity.status === 'ONGOING';
    const isJoined = activity.participationStatus === 'REGISTERED' || activity.participationStatus === 'CHECKED_IN';
    
    if (activeTab.value === 'all') {
      return isStatusValid;
    } else if (activeTab.value === 'joined') {
      return isStatusValid && isJoined;
    } else if (activeTab.value === 'available') {
      return isStatusValid && !isJoined;
    }
    return true;
  });
  
  console.log('过滤后的活动列表:', filtered);
  return filtered;
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

// 获取参与状态类型
const getParticipationStatusType = (status) => {
  const typeMap = {
    'REGISTERED': 'primary',    // 已报名 - 蓝色
    'CHECKED_IN': 'success',    // 已签到 - 绿色
    'CANCELLED': 'danger',      // 已取消 - 红色
    'COMPLETED': 'info'         // 已完成 - 灰色
  }
  return typeMap[status] || 'info'
}

// 获取参与状态文本
const getParticipationStatusText = (status) => {
  const statusMap = {
    'REGISTERED': '已报名',
    'CHECKED_IN': '已签到',
    'CANCELLED': '已取消',
    'COMPLETED': '已完成'
  }
  return statusMap[status] || '未参与'
}

// 判断是否可以参加活动
const canJoinActivity = (activity) => {
  // 活动状态必须是已发布或进行中
  const isActivityValid = activity.status === 'PUBLISHED' || activity.status === 'ONGOING';
  
  // 用户未参与或状态为已取消时可以参与
  const canJoin = !activity.participationStatus || activity.participationStatus === 'CANCELLED';
  
  return isActivityValid && canJoin;
}

// 判断是否可以取消参与活动
const canCancelJoin = (activity) => {
  // 活动状态必须是已发布或进行中
  const isActivityValid = activity.status === 'PUBLISHED' || activity.status === 'ONGOING';
  
  // 用户状态为已报名时可以取消
  const canCancel = activity.participationStatus === 'REGISTERED';
  
  return isActivityValid && canCancel;
}

// 获取当前用户ID
const getCurrentUserId = () => {
  // 从localStorage中获取用户ID
  const userId = localStorage.getItem('userId');
  console.log('当前用户ID:', userId); // 添加日志
  
  if (!userId) {
    throw new Error('无法获取用户ID，请重新登录');
  }
  return userId;
}

// 处理参与活动
const handleJoinActivity = async (activity) => {
  try {
    const userId = getCurrentUserId();
    console.log('准备参与活动，用户ID:', userId, '活动ID:', activity.id);
    
    const response = await request({
      url: `/api/activities/${activity.id}/participants`,
      method: 'post',
      data: {
        userId: parseInt(userId) // 确保userId是数字类型
      }
    });
    
    console.log('参与活动响应:', response);
    
    if (response) {
      ElMessage.success('参与成功');
      // 重新获取活动列表和参与状态
      await fetchActivities();
      // 如果当前在"可参与"标签页，切换到"全部活动"标签页
      if (activeTab.value === 'available') {
        activeTab.value = 'all';
      }
    } else {
      ElMessage.error('参与失败');
    }
  } catch (error) {
    console.error('参与活动失败:', error);
    ElMessage.error(error.message || '参与活动失败');
  }
}

// 处理取消参与
const handleCancelJoin = async (activity) => {
  try {
    const userId = getCurrentUserId();
    await ElMessageBox.confirm('确定要取消参与该活动吗？', '提示', {
      type: 'warning'
    })
    
    const response = await request({
      url: `/api/activities/${activity.id}/participants/${userId}/status/CANCELLED`,
      method: 'put'
    })
    
    if (response) {
      ElMessage.success('取消参与成功')
      // 重新获取活动列表和参与状态
      await fetchActivities()
      await fetchParticipationStatus()
      // 如果当前在"已参与"标签页，切换到"全部活动"标签页
      if (activeTab.value === 'joined') {
        activeTab.value = 'all'
      }
    } else {
      ElMessage.error('取消参与失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消参与失败:', error)
      ElMessage.error(error.message || '取消参与失败')
    }
  }
}

// 获取参与按钮文本
const getJoinButtonText = (activity) => {
  if (activity.participationStatus === 'REGISTERED' || activity.participationStatus === 'CHECKED_IN') {
    return '取消参与'
  }
  return '参与活动'
}

// 处理参与按钮点击
const handleJoinButtonClick = (activity) => {
  if (activity.participationStatus === 'REGISTERED' || activity.participationStatus === 'CHECKED_IN') {
    handleCancelJoin(activity)
  } else {
    handleJoinActivity(activity)
  }
}

// 查看活动详情
const viewActivityDetail = (activity) => {
  currentActivity.value = activity
  activityDialogVisible.value = true
}

onMounted(() => {
  fetchActivities()
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