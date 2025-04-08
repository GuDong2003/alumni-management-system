<template>
  <div class="feedback-container">
    <el-card class="feedback-card">
      <template #header>
        <div class="card-header">
          <span>意见反馈</span>
          <el-button type="primary" @click="handleAddFeedback">新增反馈</el-button>
        </div>
      </template>
      
      <div class="feedback-content">
        <el-table
          v-loading="loading"
          :data="feedbackList"
          style="width: 100%"
          border
        >
          <el-table-column prop="id" label="反馈编号" width="100" />
          <el-table-column prop="title" label="反馈标题" min-width="180" show-overflow-tooltip />
          <el-table-column prop="content" label="反馈内容" min-width="250" show-overflow-tooltip />
          <el-table-column prop="submitTime" label="提交时间" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.submitTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getFeedbackStatusType(scope.row.status)">
                {{ getFeedbackStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="scope">
              <el-button
                type="primary"
                size="small"
                @click="handleViewFeedback(scope.row)"
              >
                查看
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div v-if="feedbackList.length === 0 && !loading" class="no-data">
          <el-empty description="暂无反馈记录" />
        </div>
      </div>
    </el-card>
    
    <!-- 反馈表单对话框 -->
    <el-dialog
      v-model="feedbackDialogVisible"
      title="新增反馈"
      width="50%"
      :close-on-click-modal="false"
    >
      <el-form
        ref="feedbackFormRef"
        :model="feedbackForm"
        :rules="feedbackRules"
        label-width="100px"
      >
        <el-form-item label="反馈标题" prop="title">
          <el-input
            v-model="feedbackForm.title"
            placeholder="请输入反馈标题"
          />
        </el-form-item>
        
        <el-form-item label="反馈内容" prop="content">
          <el-input
            v-model="feedbackForm.content"
            type="textarea"
            :rows="6"
            placeholder="请输入反馈内容"
          />
        </el-form-item>
        
        <el-form-item label="联系方式" prop="contact">
          <el-input
            v-model="feedbackForm.contact"
            placeholder="请输入联系方式（选填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="feedbackDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitFeedback" :loading="submitting">
            提交
          </el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 反馈详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="反馈详情"
      width="60%"
      :close-on-click-modal="true"
    >
      <div class="feedback-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="反馈编号">{{ currentFeedback.id }}</el-descriptions-item>
          <el-descriptions-item label="反馈状态">
            <el-tag :type="getFeedbackStatusType(currentFeedback.status)">
              {{ getFeedbackStatusText(currentFeedback.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="反馈标题" :span="2">{{ currentFeedback.title }}</el-descriptions-item>
          <el-descriptions-item label="反馈内容" :span="2">{{ currentFeedback.content }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ formatDate(currentFeedback.submitTime) }}</el-descriptions-item>
          <el-descriptions-item label="联系方式">{{ currentFeedback.contact || '无' }}</el-descriptions-item>
        </el-descriptions>
        
        <div v-if="currentFeedback.reply" class="feedback-reply">
          <h3>管理员回复：</h3>
          <p>{{ currentFeedback.reply }}</p>
          <p class="reply-time">回复时间：{{ formatDate(currentFeedback.replyTime) }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import dayjs from 'dayjs'

// 反馈列表
const feedbackList = ref([])
const loading = ref(false)
const feedbackDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const submitting = ref(false)
const feedbackFormRef = ref(null)
const currentFeedback = ref({})

// 反馈表单数据
const feedbackForm = reactive({
  title: '',
  content: '',
  contact: ''
})

// 表单验证规则
const feedbackRules = {
  title: [
    { required: true, message: '请输入反馈标题', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入反馈内容', trigger: 'blur' },
    { min: 10, max: 1000, message: '长度在 10 到 1000 个字符', trigger: 'blur' }
  ]
}

// 获取反馈列表
const fetchFeedbackList = async () => {
  loading.value = true
  try {
    const response = await request({
      url: '/api/feedback/my',
      method: 'get'
    })
    if (response && response.success) {
      feedbackList.value = response.data
    }
  } catch (error) {
    console.error('获取反馈列表失败:', error)
    ElMessage.error('获取反馈列表失败')
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

// 获取反馈状态类型
const getFeedbackStatusType = (status) => {
  const statusMap = {
    'PENDING': 'warning',
    'PROCESSING': 'primary',
    'RESOLVED': 'success',
    'REJECTED': 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取反馈状态文本
const getFeedbackStatusText = (status) => {
  const statusMap = {
    'PENDING': '待处理',
    'PROCESSING': '处理中',
    'RESOLVED': '已解决',
    'REJECTED': '已拒绝'
  }
  return statusMap[status] || status
}

// 处理新增反馈
const handleAddFeedback = () => {
  // 重置表单
  Object.assign(feedbackForm, {
    title: '',
    content: '',
    contact: ''
  })
  feedbackDialogVisible.value = true
}

// 处理查看反馈
const handleViewFeedback = (feedback) => {
  currentFeedback.value = feedback
  detailDialogVisible.value = true
}

// 处理提交反馈
const handleSubmitFeedback = async () => {
  if (!feedbackFormRef.value) return
  
  try {
    // 表单验证
    await feedbackFormRef.value.validate()
    
    // 显示加载指示器
    submitting.value = true
    
    // 准备提交数据
    const submitData = {
      title: feedbackForm.title,
      content: feedbackForm.content,
      contact: feedbackForm.contact
    }
    
    // 发送请求
    const response = await request({
      url: '/api/feedback',
      method: 'post',
      data: submitData
    })
    
    if (response && response.success) {
      ElMessage.success('提交反馈成功')
      feedbackDialogVisible.value = false
      // 刷新反馈列表
      await fetchFeedbackList()
    } else {
      ElMessage.error('提交反馈失败')
    }
  } catch (error) {
    console.error('提交反馈失败:', error)
    ElMessage.error('提交反馈失败：' + error.message)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchFeedbackList()
})
</script>

<style scoped>
.feedback-container {
  padding: 20px;
}

.feedback-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.feedback-content {
  margin-top: 20px;
}

.no-data {
  margin-top: 20px;
  text-align: center;
}

.feedback-detail {
  padding: 20px;
}

.feedback-reply {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.feedback-reply h3 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #409EFF;
}

.reply-time {
  margin-top: 10px;
  font-size: 12px;
  color: #909399;
  text-align: right;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 