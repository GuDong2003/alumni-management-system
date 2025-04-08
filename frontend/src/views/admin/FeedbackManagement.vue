<template>
  <div class="feedback-management">
    <el-card class="main-card">
      <template #header>
        <div class="card-header">
          <span class="title">反馈管理</span>
        </div>
      </template>

      <!-- 搜索和筛选 -->
      <div class="search-bar">
        <el-form :inline="true" class="search-form">
          <el-form-item>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索反馈内容"
              style="width: 300px"
              clearable
              @clear="fetchFeedbacks"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-select v-model="statusFilter" placeholder="状态筛选" @change="handleStatusFilterChange" style="width: 120px;">
              <el-option label="全部" value="" />
              <el-option label="待处理" value="PENDING" />
              <el-option label="处理中" value="PROCESSING" />
              <el-option label="已回复" value="REPLIED" />
              <el-option label="已解决" value="RESOLVED" />
              <el-option label="已关闭" value="CLOSED" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-select v-model="typeFilter" placeholder="类型筛选" @change="handleTypeFilterChange" style="width: 120px;">
              <el-option label="全部" value="" />
              <el-option label="建议" value="建议" />
              <el-option label="问题" value="问题" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
        </el-form>
      </div>

      <!-- 反馈列表 -->
      <div class="table-container">
        <el-table
          v-loading="loading"
          :data="feedbacks"
          class="feedback-table"
          style="width: 100%"
          border
          stripe
          row-key="id"
        >
          <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
          <el-table-column prop="userName" label="用户" width="120" align="center" />
          <el-table-column prop="type" label="类型" width="100" align="center">
            <template #default="scope">
              <el-tag :type="getTypeType(scope.row.type)" style="width: 90%; text-align: center; display: inline-block; line-height: 24px; height: 24px;">
                {{ scope.row.type }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="140" align="center">
            <template #default="scope">
              <el-dropdown 
                @command="(command) => handleStatusChange(scope.row, command)"
                trigger="click"
              >
                <el-tag :type="getStatusType(scope.row.status)" style="width: 90%; text-align: center; cursor: pointer; display: inline-block; line-height: 24px; height: 24px;">
                  {{ getStatusLabel(scope.row.status) }}<el-icon class="el-icon--right"><arrow-down /></el-icon>
                </el-tag>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item :command="'PENDING'" :disabled="scope.row.status === 'PENDING'">待处理</el-dropdown-item>
                    <el-dropdown-item :command="'PROCESSING'" :disabled="scope.row.status === 'PROCESSING'">处理中</el-dropdown-item>
                    <el-dropdown-item :command="'REPLIED'" :disabled="scope.row.status === 'REPLIED' || !scope.row.reply">已回复</el-dropdown-item>
                    <el-dropdown-item :command="'RESOLVED'" :disabled="scope.row.status === 'RESOLVED' || !scope.row.reply">已解决</el-dropdown-item>
                    <el-dropdown-item :command="'CLOSED'" :disabled="scope.row.status === 'CLOSED'">已关闭</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </el-table-column>
          <el-table-column label="提交时间" width="180" align="center">
            <template #default="scope">
              {{ formatDate(scope.row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="查看" width="70" align="center" fixed="right">
            <template #default="scope">
              <el-button type="primary" link @click="viewFeedback(scope.row)">查看</el-button>
            </template>
          </el-table-column>
          <el-table-column label="操作" min-width="280" fixed="right" align="center">
            <template #default="scope">
              <div class="operation-buttons">
                <el-button
                  type="success"
                  link
                  :disabled="scope.row.status !== 'PENDING'"
                  @click="handleProcess(scope.row)"
                >处理</el-button>
                <el-button
                  type="warning"
                  link
                  :disabled="scope.row.status === 'CLOSED'"
                  @click="openReplyDialog(scope.row)"
                >回复</el-button>
                <el-button
                  type="success"
                  link
                  :disabled="!scope.row.reply || scope.row.status === 'RESOLVED' || scope.row.status === 'CLOSED'"
                  @click="markAsResolved(scope.row)"
                >已解决</el-button>
                <el-button
                  type="info"
                  link
                  :disabled="scope.row.status === 'CLOSED'"
                  @click="markAsClosed(scope.row)"
                >关闭</el-button>
                <el-button
                  type="danger"
                  link
                  @click="handleDelete(scope.row)"
                >删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 查看反馈详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="反馈详情"
      width="60%"
      :close-on-click-modal="false"
    >
      <div class="feedback-detail" v-if="currentFeedback">
        <div class="detail-header">
          <h2>{{ currentFeedback.title }}</h2>
          <div class="detail-meta">
            <span>提交者: {{ currentFeedback.userName }}</span>
            <span>类型: {{ currentFeedback.type }}</span>
            <span>状态: {{ getStatusLabel(currentFeedback.status) }}</span>
            <span>提交时间: {{ formatDate(currentFeedback.createdAt) }}</span>
          </div>
        </div>
        <div class="detail-content">
          <h3>反馈内容:</h3>
          <div class="content-text">{{ currentFeedback.content }}</div>
        </div>
        <div class="detail-reply" v-if="currentFeedback.reply">
          <h3>回复内容:</h3>
          <div class="reply-meta">
            <span>回复人: {{ currentFeedback.replyUserName }}</span>
            <span>回复时间: {{ formatDate(currentFeedback.replyTime) }}</span>
          </div>
          <div class="reply-text">{{ currentFeedback.reply }}</div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="viewDialogVisible = false">关闭</el-button>
          <el-button
            type="primary"
            v-if="!currentFeedback.reply && currentFeedback.status !== 'CLOSED'"
            @click="openReplyDialog(currentFeedback)"
          >回复</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 回复反馈对话框 -->
    <el-dialog
      v-model="replyDialogVisible"
      title="回复反馈"
      width="50%"
      :close-on-click-modal="false"
    >
      <el-form :model="replyForm" ref="replyFormRef" :rules="replyRules">
        <el-form-item prop="reply" label="回复内容" :label-width="'80px'">
          <el-input
            v-model="replyForm.reply"
            type="textarea"
            :rows="5"
            placeholder="请输入回复内容"
          ></el-input>
        </el-form-item>
        <el-form-item label="状态" :label-width="'80px'">
          <el-select v-model="replyForm.status">
            <el-option label="已回复" value="REPLIED" />
            <el-option label="已解决" value="RESOLVED" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="replyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReply">提交回复</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { Search, ArrowDown } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import {
  getAllFeedbacks,
  getFeedbackById,
  getFeedbacksByStatus,
  getFeedbacksByType,
  searchFeedbacks,
  updateFeedbackStatus,
  replyToFeedback,
  deleteFeedback
} from '@/api/feedback'
import { getUserById } from '@/api/user'

// 获取认证存储
const authStore = useAuthStore()

// 数据列表
const feedbacks = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const statusFilter = ref('')
const typeFilter = ref('')

// 用户名缓存，避免重复请求
const userNameCache = ref({})

// 对话框控制
const viewDialogVisible = ref(false)
const replyDialogVisible = ref(false)
const currentFeedback = ref({})

// 回复表单
const replyForm = ref({
  reply: '',
  status: 'REPLIED'
})
const replyFormRef = ref(null)
const replyRules = {
  reply: [
    { required: true, message: '请输入回复内容', trigger: 'blur' },
    { min: 5, message: '回复内容不能少于5个字符', trigger: 'blur' }
  ]
}

// 获取用户名的方法
const getUserName = async (userId) => {
  console.log(`准备获取用户ID=${userId}的信息`)
  
  // 如果缓存中已有该用户名，直接返回
  if (userNameCache.value[userId]) {
    console.log(`从缓存获取用户ID=${userId}的名称: ${userNameCache.value[userId]}`)
    return userNameCache.value[userId]
  }
  
  try {
    console.log(`开始请求用户ID=${userId}的API数据`)
    const response = await getUserById(userId)
    console.log(`获取用户ID=${userId}的完整响应:`, response)
    
    // 根据后端日志，API返回的用户对象中直接包含name字段
    // 服务器日志显示用户ID 4的name是"校友 一"，用户ID 11的name是"校友八"
    if (response) {
      let userName = '未知用户'
      
      if (response.name) {
        // 直接从根对象获取name
        userName = response.name
      } else if (response.data && response.data.name) {
        // 从data对象获取name
        userName = response.data.name
      } else if (typeof response === 'string') {
        // 如果返回的是字符串格式的JSON对象
        try {
          const parsedResponse = JSON.parse(response)
          if (parsedResponse.name) {
            userName = parsedResponse.name
          } else if (parsedResponse.data && parsedResponse.data.name) {
            userName = parsedResponse.data.name
          }
        } catch (e) {
          console.error('解析响应字符串失败:', e)
        }
      }
      
      console.log(`最终解析的用户名: ${userName}`)
      userNameCache.value[userId] = userName
      return userName
    }
    
    return '未知用户'
  } catch (error) {
    console.error(`获取用户ID=${userId}的信息出错:`, error)
    return '未知用户'
  }
}

// 处理反馈数据，添加用户名字段
const processFeedbackData = async (feedbackList) => {
  console.log('开始处理反馈数据列表:', feedbackList.length, '条记录')
  console.log('示例反馈数据:', JSON.stringify(feedbackList[0] || {}))
  
  if (!Array.isArray(feedbackList) || feedbackList.length === 0) return []
  
  const promises = feedbackList.map(async (feedback) => {
    console.log('处理单条反馈:', JSON.stringify(feedback))
    
    // 始终从userId获取用户信息
    if (feedback.userId) {
      try {
        const userName = await getUserName(feedback.userId)
        console.log(`反馈ID=${feedback.id}添加用户名: ${userName}`)
        return { ...feedback, userName }
      } catch (e) {
        console.error(`反馈ID=${feedback.id}添加用户名失败:`, e)
        return { ...feedback, userName: `ID:${feedback.userId}-未知用户` }
      }
    }
    console.warn(`反馈ID=${feedback.id}缺少userId`)
    return { ...feedback, userName: '未知用户' }
  })
  
  const results = await Promise.all(promises)
  console.log('处理完成的反馈列表:', results.length, '条记录')
  return results
}

// 获取数据
const fetchFeedbacks = async () => {
  loading.value = true
  try {
    const headers = getAuthHeaders();
    let response

    if (statusFilter.value) {
      response = await getFeedbacksByStatus(statusFilter.value, currentPage.value, pageSize.value, headers)
    } else {
      response = await getAllFeedbacks(currentPage.value, pageSize.value, headers)
    }

    console.log('获取到的反馈数据:', response)
    
    if (response && response.code === 200) {
      if (response.data && response.data.list) {
        // 从用户表获取用户名
        feedbacks.value = await processFeedbackData(response.data.list)
        total.value = response.data.total
      } else if (Array.isArray(response.data)) {
        // 处理可能直接返回数组的情况
        // 从用户表获取用户名
        feedbacks.value = await processFeedbackData(response.data)
        total.value = response.data.length
      } else {
        feedbacks.value = []
        total.value = 0
        console.error('获取的数据格式不正确:', response.data)
      }
    } else {
      ElMessage.error(response.message || '获取反馈列表失败')
      feedbacks.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取反馈列表出错:', error)
    ElMessage.error('获取反馈列表失败: ' + (error.message || '未知错误'))
    feedbacks.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 搜索反馈
const searchFeedbackList = async () => {
  if (!searchKeyword.value.trim()) {
    fetchFeedbacks()
    return
  }
  
  loading.value = true
  try {
    const headers = getAuthHeaders();
    const response = await searchFeedbacks(searchKeyword.value, headers)
    
    console.log('搜索反馈结果:', response)
    
    if (response && response.code === 200) {
      if (Array.isArray(response.data)) {
        // 添加用户名处理
        feedbacks.value = await processFeedbackData(response.data)
        total.value = response.data.length
      } else {
        feedbacks.value = []
        total.value = 0
        console.error('搜索返回的数据格式不正确:', response.data)
      }
    } else {
      ElMessage.error(response.message || '搜索反馈失败')
      feedbacks.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('搜索反馈出错:', error)
    ElMessage.error('搜索反馈失败: ' + (error.message || '未知错误'))
    feedbacks.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 查看反馈详情
const viewFeedback = async (row) => {
  try {
    // 添加请求头用于认证
    const headers = getAuthHeaders();
    
    const response = await getFeedbackById(row.id, headers);
    console.log('获取反馈详情结果:', response);
    
    if (response && response.code === 200) {
      // 获取反馈详情后，确保添加用户名
      const feedback = response.data;
      console.log('详细反馈数据:', feedback);
      
      // 从userId获取用户名，使用改进后的getUserName方法
      if (feedback.userId) {
        feedback.userName = await getUserName(feedback.userId);
      } else {
        feedback.userName = '未知用户';
      }
      
      currentFeedback.value = feedback;
      viewDialogVisible.value = true;
    } else {
      ElMessage.error(response.message || '获取反馈详情失败');
    }
  } catch (error) {
    console.error('获取反馈详情出错:', error);
    ElMessage.error('获取反馈详情失败: ' + (error.message || '未知错误'));
  }
}

// 辅助函数：获取认证请求头
const getAuthHeaders = () => {
  // 尝试获取当前用户信息
  const currentUsername = authStore.userInfo?.username
  
  return {
    // 传递当前用户名，后端会检查是否为admin或superadmin
    'X-Current-Username': currentUsername || 'admin'
  };
}

// 处理反馈（设置状态为处理中）
const handleProcess = async (row) => {
  try {
    const headers = getAuthHeaders();
    const response = await updateFeedbackStatus(row.id, 'PROCESSING', headers);
    console.log('处理反馈结果:', response);
    
    if (response && response.code === 200) {
      ElMessage.success('已开始处理该反馈');
      fetchFeedbacks();
    } else {
      ElMessage.error(response.message || '操作失败');
    }
  } catch (error) {
    console.error('处理反馈出错:', error);
    ElMessage.error('操作失败: ' + (error.message || '未知错误'));
  }
}

// 标记为已解决
const markAsResolved = async (feedback) => {
  try {
    const headers = getAuthHeaders();
    const response = await updateFeedbackStatus(feedback.id, 'RESOLVED', headers);
    console.log('标记为已解决结果:', response);
    
    if (response && response.code === 200) {
      ElMessage.success('已标记为已解决');
      viewDialogVisible.value = false;
      fetchFeedbacks();
    } else {
      ElMessage.error(response.message || '操作失败');
    }
  } catch (error) {
    console.error('标记反馈出错:', error);
    ElMessage.error('操作失败: ' + (error.message || '未知错误'));
  }
}

// 关闭反馈
const markAsClosed = async (feedback) => {
  try {
    const headers = getAuthHeaders();
    const response = await updateFeedbackStatus(feedback.id, 'CLOSED', headers);
    console.log('关闭反馈结果:', response);
    
    if (response && response.code === 200) {
      ElMessage.success('已关闭反馈');
      viewDialogVisible.value = false;
      fetchFeedbacks();
    } else {
      ElMessage.error(response.message || '操作失败');
    }
  } catch (error) {
    console.error('关闭反馈出错:', error);
    ElMessage.error('操作失败: ' + (error.message || '未知错误'));
  }
}

// 删除反馈
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除该反馈吗？删除后无法恢复',
    '删除确认',
    {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'warning',
      confirmButtonClass: 'el-button--danger',
      closeOnClickModal: false,
      distinguishCancelAndClose: true,
      icon: 'Warning'
    }
  ).then(async () => {
    try {
      const loadingInstance = ElMessage({
        message: '正在删除...',
        type: 'info',
        duration: 0
      });
      
      const headers = getAuthHeaders();
      const response = await deleteFeedback(row.id, headers);
      
      loadingInstance.close();
      console.log('删除反馈结果:', response);
      
      if (response && response.code === 200) {
        ElMessage.success('删除成功');
        fetchFeedbacks();
      } else {
        ElMessage.error(response.message || '删除失败');
      }
    } catch (error) {
      console.error('删除反馈出错:', error);
      ElMessage.error('删除失败: ' + (error.message || '未知错误'));
    }
  }).catch((action) => {
    // 用户取消删除
    if (action === 'cancel') {
      ElMessage.info('已取消删除');
    }
  });
}

// 打开回复对话框
const openReplyDialog = (feedback) => {
  currentFeedback.value = feedback;
  replyForm.value.reply = '';
  replyForm.value.status = 'REPLIED';
  replyDialogVisible.value = true;
}

// 提交回复
const submitReply = async () => {
  replyFormRef.value.validate(async (valid) => {
    if (!valid) return;
    
    try {
      // 显示提交状态
      const loadingInstance = ElMessage({
        message: '正在提交回复...',
        type: 'info',
        duration: 0
      });
      
      // 尝试获取当前用户信息
      const currentUsername = authStore.userInfo?.username;
      
      // 构建请求头
      const headers = {
        // 传递当前用户名，后端会检查是否为admin或superadmin
        'X-Current-Username': currentUsername || 'admin'
      };
      
      console.log('提交回复请求:', {
        feedbackId: currentFeedback.value.id,
        reply: replyForm.value.reply,
        status: replyForm.value.status,
        headers
      });
      
      const response = await replyToFeedback(
        currentFeedback.value.id,
        replyForm.value.reply,
        replyForm.value.status,
        headers
      );
      
      // 关闭加载提示
      loadingInstance.close();
      
      console.log('回复反馈结果:', response);
      
      if (response && response.code === 200) {
        ElMessage.success('回复成功');
        replyDialogVisible.value = false;
        viewDialogVisible.value = false;
        fetchFeedbacks();
      } else {
        ElMessage.error(response.message || '回复失败');
      }
    } catch (error) {
      console.error('回复反馈出错:', error);
      ElMessage.error('回复失败: ' + (error.message || '未知错误'));
    }
  });
}

// 状态筛选变化
const handleStatusFilterChange = () => {
  currentPage.value = 1
  fetchFeedbacks()
}

// 类型筛选变化
const handleTypeFilterChange = async () => {
  if (!typeFilter.value) {
    fetchFeedbacks()
    return
  }
  
  loading.value = true
  try {
    const headers = getAuthHeaders();
    const response = await getFeedbacksByType(typeFilter.value, headers)
    console.log('类型筛选结果:', response)
    
    if (response && response.code === 200) {
      if (Array.isArray(response.data)) {
        // 添加用户名处理
        feedbacks.value = await processFeedbackData(response.data)
        total.value = response.data.length
      } else {
        feedbacks.value = []
        total.value = 0
        console.error('筛选返回的数据格式不正确:', response.data)
      }
    } else {
      ElMessage.error(response.message || '筛选反馈失败')
    }
  } catch (error) {
    console.error('筛选反馈出错:', error)
    ElMessage.error('筛选反馈失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  fetchFeedbacks()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchFeedbacks()
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  searchFeedbackList()
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取状态标签
const getStatusLabel = (status) => {
  const statusMap = {
    'PENDING': '待处理',
    'PROCESSING': '处理中',
    'REPLIED': '已回复',
    'RESOLVED': '已解决',
    'CLOSED': '已关闭'
  }
  return statusMap[status] || status
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'PROCESSING': 'info',
    'REPLIED': 'success',
    'RESOLVED': 'success',
    'CLOSED': 'info'
  }
  return typeMap[status] || ''
}

// 处理状态变化
const handleStatusChange = async (row, value) => {
  // 如果尝试设置为已回复但没有回复内容，则提示并退出
  if (value === 'REPLIED' && !row.reply) {
    ElMessage.warning('该反馈还没有回复内容，无法设置为已回复状态');
    return;
  }
  
  // 如果尝试设置为已解决但没有回复内容，则提示并退出
  if (value === 'RESOLVED' && !row.reply) {
    ElMessage.warning('该反馈还没有回复内容，无法设置为已解决状态');
    return;
  }
  
  try {
    const headers = getAuthHeaders();
    const response = await updateFeedbackStatus(row.id, value, headers);
    console.log('状态更新结果:', response);
    
    if (response && response.code === 200) {
      ElMessage.success('状态更新成功');
      fetchFeedbacks();
    } else {
      ElMessage.error(response.message || '状态更新失败');
    }
  } catch (error) {
    console.error('状态更新出错:', error);
    ElMessage.error('状态更新失败: ' + (error.message || '未知错误'));
  }
}

// 获取类型标签类型
const getTypeType = (type) => {
  const typeMap = {
    '建议': 'success',
    '问题': 'warning',
    '其他': 'info'
  }
  return typeMap[type] || ''
}

// 页面加载时获取数据
onMounted(() => {
  fetchFeedbacks()
})
</script>

<style scoped>
.feedback-management {
  padding: 20px;
  width: 100%;
}

.main-card {
  margin-bottom: 20px;
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 16px;
  font-weight: normal;
}

.search-bar {
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.table-container {
  width: 100%;
}

.feedback-table {
  width: 100%;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 分页样式优化 */
:deep(.el-pagination) {
  padding: 0;
  margin-top: 20px;
}

:deep(.el-pagination .el-select) {
  width: 100px !important;
}

:deep(.el-pagination .el-select .el-input) {
  width: 100px !important;
}

:deep(.el-pagination .el-select .el-input__wrapper) {
  width: 100px !important;
}

:deep(.el-pagination .btn-prev),
:deep(.el-pagination .btn-next),
:deep(.el-pagination .el-pager li) {
  background-color: #fff;
  color: #606266;
  min-width: 30px;
  border-radius: 2px;
  border: 1px solid #dcdfe6;
}

:deep(.el-pagination .el-pager li.is-active) {
  background-color: #409eff;
  color: #fff;
  border-color: #409eff;
}

/* 优化表格样式 */
:deep(.el-table) {
  width: 100% !important;
}

:deep(.el-table__body) {
  width: 100% !important;
}

/* 表头优化 */
:deep(.el-table__header) {
  width: 100% !important;
}

:deep(.el-table__header-wrapper th) {
  word-break: keep-all;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  background-color: #f5f7fa;
  font-weight: bold;
  text-align: center !important;
}

:deep(.el-table__header-wrapper th .cell) {
  text-align: center !important;
  padding: 0 5px;
}

/* 列宽自适应 */
:deep(.el-table__cell) {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 优化表格文字溢出显示 */
:deep(.el-table .cell) {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 确保列表容器自适应 */
:deep(.el-card__body) {
  padding: 20px;
}

/* 优化固定列显示 */
:deep(.el-table__fixed-right) {
  box-shadow: -2px 0 5px rgba(0, 0, 0, 0.1);
}

/* 确保表格行展示美观 */
:deep(.el-table__row) {
  height: 48px;
}

/* 文本居中样式 */
.centered-text {
  text-align: center;
  white-space: normal;
  word-break: break-word;
  line-height: 1.4;
  width: 100%;
  display: -webkit-box;
  line-clamp: 2;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.operation-buttons {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 5px;
  margin: -2px 0;
}

.operation-buttons .el-button {
  margin: 0;
  padding: 2px 6px;
}

.operation-buttons .el-button[disabled] {
  color: #909399;
  cursor: not-allowed;
  text-decoration: none;
}

.feedback-detail {
  padding: 0 20px;
}

.detail-header {
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
  margin-bottom: 15px;
}

.detail-header h2 {
  margin-top: 0;
  margin-bottom: 10px;
}

.detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  color: #666;
  font-size: 14px;
}

.detail-content, .detail-reply {
  margin-bottom: 20px;
}

.content-text, .reply-text {
  background: #f9f9f9;
  border-radius: 4px;
  padding: 15px;
  white-space: pre-wrap;
  word-break: break-word;
}

.reply-meta {
  display: flex;
  gap: 15px;
  color: #666;
  font-size: 14px;
  margin-bottom: 10px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.el-tag {
  cursor: pointer;
  transition: all 0.3s;
}

.el-tag:hover {
  opacity: 0.8;
}

.el-icon--right {
  margin-left: 2px;
  font-size: 12px;
}
</style> 