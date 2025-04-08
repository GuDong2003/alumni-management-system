<template>
  <div class="activity-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>活动管理</span>
          <el-button type="primary" @click="handleAdd">添加活动</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <div class="search-form-row">
            <el-form-item label="活动标题">
              <el-input v-model="searchForm.title" placeholder="请输入活动标题" clearable style="width: 200px;" />
            </el-form-item>
            <el-form-item label="活动类型">
              <el-select v-model="searchForm.type" placeholder="请选择活动类型" clearable style="width: 180px;">
                <el-option label="返校日" value="REUNION" />
                <el-option label="分享会" value="SHARING" />
                <el-option label="招聘会" value="CAREER" />
                <el-option label="体育赛事" value="SPORTS" />
                <el-option label="导师计划" value="MENTORING" />
                <el-option label="捐赠仪式" value="DONATION" />
                <el-option label="文化活动" value="CULTURE" />
                <el-option label="论坛" value="FORUM" />
                <el-option label="迎新晚会" value="WELCOME" />
                <el-option label="年度大会" value="ANNUAL" />
              </el-select>
            </el-form-item>
            <el-form-item label="活动状态">
              <el-select v-model="searchForm.status" placeholder="请选择活动状态" clearable style="width: 180px;">
                <el-option label="草稿" value="DRAFT" />
                <el-option label="已发布" value="PUBLISHED" />
                <el-option label="进行中" value="ONGOING" />
                <el-option label="已结束" value="COMPLETED" />
                <el-option label="已取消" value="CANCELLED" />
              </el-select>
            </el-form-item>
            <el-form-item label="日期范围">
              <el-date-picker
                v-model="searchForm.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD"
                clearable
                style="width: 250px;"
              />
            </el-form-item>
            <div class="search-buttons">
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="resetSearch">重置</el-button>
            </div>
          </div>
        </el-form>
      </div>

      <!-- 活动列表 -->
      <el-table
        :data="activities"
        style="width: 100%"
        border
        v-loading="loading"
      >
        <el-table-column prop="title" label="活动标题" width="180" align="center">
          <template #default="{ row }">
            <div class="activity-title">{{ row.title }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="活动类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag 
              :type="typeof getActivityTagType(row.type) === 'string' ? getActivityTagType(row.type) : undefined"
              :color="typeof getActivityTagType(row.type) === 'object' ? getActivityTagType(row.type).color : undefined"
              :style="{ 
                color: typeof getActivityTagType(row.type) === 'object' ? getActivityTagType(row.type).textColor : undefined,
                width: '90%', 
                textAlign: 'center', 
                display: 'inline-block', 
                lineHeight: '24px', 
                height: '24px'
              }"
            >
              {{ getActivityTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="150" align="center">
          <template #default="{ row }">
            {{ formatDate(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="150" align="center">
          <template #default="{ row }">
            <div class="centered-text">{{ formatDate(row.endTime) }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="活动地点" width="150" align="center">
          <template #default="{ row }">
            <div class="centered-text">{{ row.location }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="maxParticipants" label="人数上限" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="120" align="center">
          <template #default="{ row }">
            <el-dropdown 
              @command="(command) => handleUpdateStatus(row, command)"
              trigger="click"
            >
              <el-tag
                :type="getStatusType(row.status)"
                style="cursor: pointer;"
              >
                {{ getStatusLabel(row.status) }}<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-tag>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item
                    v-for="option in activityStatusOptions"
                    :key="option.value"
                    :command="option.value"
                    :disabled="option.value === row.status"
                  >
                    {{ option.label }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link @click="handleViewParticipants(row)">查看参与情况</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 活动表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加活动' : '编辑活动'"
      width="50%"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="activity-form"
      >
        <el-form-item label="活动标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入活动标题" />
        </el-form-item>
        <el-form-item label="活动类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择活动类型" style="width: 100%">
            <el-option label="返校日" value="REUNION" />
            <el-option label="分享会" value="SHARING" />
            <el-option label="招聘会" value="CAREER" />
            <el-option label="体育赛事" value="SPORTS" />
            <el-option label="导师计划" value="MENTORING" />
            <el-option label="捐赠仪式" value="DONATION" />
            <el-option label="文化活动" value="CULTURE" />
            <el-option label="论坛" value="FORUM" />
            <el-option label="迎新晚会" value="WELCOME" />
            <el-option label="年度大会" value="ANNUAL" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="选择开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            placeholder="选择结束时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="活动地点" prop="location">
          <el-input v-model="form.location" placeholder="请输入活动地点" />
        </el-form-item>
        <el-form-item label="活动描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入活动描述"
          />
        </el-form-item>
        <el-form-item label="最大参与人数" prop="maxParticipants">
          <el-input-number
            v-model="form.maxParticipants"
            :min="1"
            :max="1000"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status" v-if="dialogType === 'edit'">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已发布" value="PUBLISHED" />
            <el-option label="进行中" value="ONGOING" />
            <el-option label="已结束" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 参与情况对话框 -->
    <el-dialog
      v-model="participantsDialogVisible"
      title="活动参与情况"
      width="70%"
      :close-on-click-modal="false"
    >
      <el-table
        :data="participants"
        style="width: 100%"
        border
        v-loading="participantsLoading"
      >
        <el-table-column prop="name" label="姓名" width="120" align="center" />
        <el-table-column prop="email" label="邮箱" width="180" align="center" />
        <el-table-column prop="phone" label="电话" width="150" align="center" />
        <el-table-column prop="registerTime" label="报名时间" width="180" align="center">
          <template #default="{ row }">
            {{ formatDateTime(row.registerTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120" align="center">
          <template #default="{ row }">
            <el-dropdown 
              @command="(command) => handleUpdateParticipantStatus(row, command)"
              trigger="click"
            >
              <el-tag
                :type="getParticipantStatusType(row.status)"
                style="cursor: pointer;"
              >
                {{ getParticipantStatusLabel(row.status) }}<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-tag>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item
                    v-for="option in participantStatusOptions"
                    :key="option.value"
                    :command="option.value"
                    :disabled="option.value === row.status"
                  >
                    {{ option.label }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button
              type="danger"
              size="small"
              @click="handleDeleteParticipant(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="participantsDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 备注对话框 -->
    <el-dialog
      v-model="noteDialogVisible"
      title="添加备注"
      width="40%"
    >
      <el-form :model="noteForm" label-width="80px">
        <el-form-item label="备注内容">
          <el-input
            v-model="noteForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入备注内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="noteDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitNote">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 通知对话框 -->
    <el-dialog
      v-model="notificationDialogVisible"
      title="发送通知"
      width="40%"
    >
      <el-form :model="notificationForm" label-width="80px">
        <el-form-item label="通知标题">
          <el-input v-model="notificationForm.title" placeholder="请输入通知标题" />
        </el-form-item>
        <el-form-item label="通知内容">
          <el-input
            v-model="notificationForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入通知内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="notificationDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitNotification">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatDateTime } from '@/utils/date'
import request from '@/utils/request'
import { ArrowDown } from '@element-plus/icons-vue'

const loading = ref(false)
const activities = ref([])
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const participantsDialogVisible = ref(false)
const participants = ref([])
const participantsLoading = ref(false)
const currentActivityId = ref(null)
const noteDialogVisible = ref(false)
const notificationDialogVisible = ref(false)
const noteForm = ref({
  participantId: null,
  content: ''
})
const notificationForm = ref({
  participantId: null,
  title: '',
  content: ''
})

const form = ref({
  id: '',
  title: '',
  type: '',
  startTime: '',
  endTime: '',
  location: '',
  description: '',
  maxParticipants: 1,
  status: ''
})

const rules = {
  title: [
    { required: true, message: '请输入活动标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在2到100个字符之间', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入活动描述', trigger: 'blur' },
    { min: 10, max: 1000, message: '描述长度在10到1000个字符之间', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' },
    { type: 'date', message: '请选择有效的开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' },
    { type: 'date', message: '请选择有效的结束时间', trigger: 'change' }
  ],
  location: [
    { required: true, message: '请输入活动地点', trigger: 'blur' },
    { min: 2, max: 200, message: '地点长度在2到200个字符之间', trigger: 'blur' }
  ],
  maxParticipants: [
    { required: true, message: '请输入最大参与人数', trigger: 'blur' },
    { type: 'number', min: 1, max: 1000, message: '参与人数必须在1到1000之间', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择活动类型', trigger: 'change' }
  ]
}

// 搜索表单
const searchForm = ref({
  title: '',
  type: '',
  status: '',
  dateRange: []
})

// 活动状态选项
const activityStatusOptions = [
  { label: '草稿', value: 'DRAFT' },
  { label: '已发布', value: 'PUBLISHED' },
  { label: '进行中', value: 'ONGOING' },
  { label: '已结束', value: 'COMPLETED' },
  { label: '已取消', value: 'CANCELLED' }
]

// 参与状态选项
const participantStatusOptions = [
  { label: '已报名', value: 'REGISTERED' },
  { label: '已确认', value: 'CONFIRMED' },
  { label: '已取消', value: 'CANCELLED' },
  { label: '已完成', value: 'COMPLETED' }
]

// 获取状态标签
const getStatusLabel = (status) => {
  const statusMap = {
    'DRAFT': '草稿',
    'PUBLISHED': '已发布',
    'ONGOING': '进行中',
    'COMPLETED': '已结束',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    'DRAFT': 'info',
    'PUBLISHED': 'warning',
    'ONGOING': 'success',
    'COMPLETED': 'info',
    'CANCELLED': 'danger'
  }
  return typeMap[status] || 'info'
}

const getActivityTagType = (type) => {
  const typeColorMap = {
    'REUNION': {
      color: '#ffcdd2',       // 浅红色
      textColor: '#c62828'    // 深红色文字
    },
    'SHARING': {
      color: '#c8e6c9',       // 浅绿色
      textColor: '#2e7d32'    // 深绿色文字
    },
    'CAREER': {
      color: '#fff9c4',       // 浅黄色
      textColor: '#f57f17'    // 深黄色文字
    },
    'SPORTS': {
      color: '#bbdefb',       // 浅蓝色
      textColor: '#1565c0'    // 深蓝色文字
    },
    'MENTORING': {
      color: '#e1bee7',       // 浅紫色
      textColor: '#6a1b9a'    // 深紫色文字
    },
    'DONATION': {
      color: '#ffe0b2',       // 浅橙色
      textColor: '#e65100'    // 深橙色文字
    },
    'CULTURE': {
      color: '#b2dfdb',       // 浅青绿色
      textColor: '#00695c'    // 深青绿色文字
    },
    'FORUM': {
      color: '#b3e5fc',       // 浅湖蓝色
      textColor: '#01579b'    // 深湖蓝色文字
    },
    'WELCOME': {
      color: '#d1c4e9',       // 浅淡紫色
      textColor: '#4527a0'    // 深淡紫色文字
    },
    'ANNUAL': {
      color: '#cfd8dc',       // 浅灰蓝色
      textColor: '#263238'    // 深灰蓝色文字
    }
  };

  return typeColorMap[type] || { color: '#f5f5f5', textColor: '#757575' }; // 默认浅灰色
}

const getActivityTypeText = (type) => {
  switch (type) {
    case 'REUNION':
      return '返校日'
    case 'SHARING':
      return '分享会'
    case 'CAREER':
      return '招聘会'
    case 'SPORTS':
      return '体育赛事'
    case 'MENTORING':
      return '导师计划'
    case 'DONATION':
      return '捐赠仪式'
    case 'CULTURE':
      return '文化活动'
    case 'FORUM':
      return '论坛'
    case 'WELCOME':
      return '迎新晚会'
    case 'ANNUAL':
      return '年度大会'
    default:
      return type
  }
}

const formatDate = (date) => {
  if (!date) return ''
  return formatDateTime(date)
}

const fetchActivities = async () => {
  loading.value = true
  try {
    const params = new URLSearchParams()
    
    // 添加分页参数
    params.append('page', currentPage.value - 1) 
    params.append('size', pageSize.value)
    
    const response = await request({
      url: `/api/activities?${params.toString()}`,
      method: 'get'
    })
    
    if (response && response.content !== undefined) {
      // Spring Data 分页响应格式
      activities.value = await processActivitiesData(response.content)
      total.value = response.totalElements || 0
    } else if (Array.isArray(response)) {
      // 非分页响应格式，需要手动处理分页
      const allActivities = await processActivitiesData(response)
      total.value = allActivities.length
      
      // 手动分页
      const startIndex = (currentPage.value - 1) * pageSize.value
      const endIndex = startIndex + pageSize.value
      activities.value = allActivities.slice(startIndex, endIndex)
    } else {
      console.error('接口返回的数据格式不正确:', response)
      activities.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取活动列表失败:', error)
    ElMessage.error(error.message || '获取活动列表失败')
    activities.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 处理活动数据
const processActivitiesData = async (data) => {
  if (!data || !Array.isArray(data)) return [];
  
  // 处理基本数据
  return data.map(item => {
    const processedItem = { ...item };
    
    if (processedItem.title) {
      processedItem.title = String(processedItem.title).replace(/\s+/g, ' ').trim();
    } else {
      processedItem.title = '-';
    }
    
    if (processedItem.location) {
      processedItem.location = String(processedItem.location).replace(/\s+/g, ' ').trim();
    }
    
    if (processedItem.description) {
      if (typeof processedItem.description === 'object') {
        processedItem.description = '详见编辑页面';
      }
    } else {
      processedItem.description = '';
    }
    
    return processedItem;
  });
}

const handleAdd = () => {
  dialogType.value = 'add'
  form.value = {
    id: '',
    title: '',
    type: 'REUNION',
    startTime: '',
    endTime: '',
    location: '',
    description: '',
    maxParticipants: 50,
    status: 'DRAFT'
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogType.value = 'edit'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该活动吗？', '提示', {
      type: 'warning'
    })
    
    await request({
      url: `/api/activities/${row.id}`,
      method: 'delete'
    })
    
    ElMessage.success('删除成功')
    fetchActivities()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除活动失败:', error)
      ElMessage.error(error.message || '删除活动失败')
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    // 验证表单
    await formRef.value.validate()
    
    // 准备提交数据
    const submitData = {
      title: form.value.title,
      type: form.value.type,
      startTime: form.value.startTime ? new Date(form.value.startTime).toISOString().replace('T', ' ').slice(0, 19) : null,
      endTime: form.value.endTime ? new Date(form.value.endTime).toISOString().replace('T', ' ').slice(0, 19) : null,
      location: form.value.location,
      description: form.value.description,
      maxParticipants: Number(form.value.maxParticipants),
      status: 'DRAFT'
    }
    
    // 验证时间
    if (submitData.startTime && submitData.endTime) {
      const start = new Date(submitData.startTime)
      const end = new Date(submitData.endTime)
      if (start >= end) {
        ElMessage.error('结束时间必须晚于开始时间')
        return
      }
    }
    
    console.log('提交的活动数据:', submitData)
    
    const url = dialogType.value === 'add' ? '/api/activities' : `/api/activities/${form.value.id}`
    const method = dialogType.value === 'add' ? 'post' : 'put'
    
    const response = await request({
      url,
      method,
      data: submitData
    })
    
    ElMessage.success(dialogType.value === 'add' ? '添加成功' : '更新成功')
    dialogVisible.value = false
    fetchActivities()
  } catch (error) {
    console.error('提交失败:', error)
    if (error.response) {
      // 处理后端返回的错误信息
      const errorMessage = error.response.data?.message || '提交失败'
      ElMessage.error(errorMessage)
    } else if (error.message) {
      // 处理前端验证错误
      ElMessage.error(error.message)
    } else {
      // 处理其他错误
      console.error('详细错误信息:', error)
      ElMessage.error('提交失败，请检查表单数据')
    }
  }
}

const handleSearch = async () => {
  currentPage.value = 1;
  loading.value = true
  try {
    const params = new URLSearchParams()
    
    params.append('page', currentPage.value - 1)
    params.append('size', pageSize.value)
    
    if (searchForm.value.title) {
      params.append('title', searchForm.value.title)
    }
    if (searchForm.value.type) {
      params.append('type', searchForm.value.type)
    }
    if (searchForm.value.status) {
      params.append('status', searchForm.value.status)
    }
    if (searchForm.value.dateRange && searchForm.value.dateRange.length === 2) {
      const startDate = new Date(searchForm.value.dateRange[0])
      const endDate = new Date(searchForm.value.dateRange[1])
      startDate.setHours(0, 0, 0)
      endDate.setHours(23, 59, 59)
      
      params.append('startTime', startDate.toISOString().slice(0, 19))
      params.append('endTime', endDate.toISOString().slice(0, 19))
    }
    
    // 根据是否有搜索条件决定使用哪个接口
    const url = hasSearchConditions() 
      ? `/api/activities/search` 
      : `/api/activities`;
    
    const response = await request({
      url: `${url}?${params.toString()}`,
      method: 'get'
    })
    
    if (response && response.content !== undefined) {
      activities.value = await processActivitiesData(response.content)
      total.value = response.totalElements || 0
    } else if (Array.isArray(response)) {
      // 非分页响应格式，需要手动处理分页
      const allActivities = await processActivitiesData(response)
      total.value = allActivities.length
      
      // 手动分页
      const startIndex = (currentPage.value - 1) * pageSize.value
      const endIndex = startIndex + pageSize.value
      activities.value = allActivities.slice(startIndex, endIndex)
    } else {
      console.error('接口返回的数据格式不正确:', response)
      activities.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('搜索活动失败:', error)
    ElMessage.error(error.message || '搜索活动失败')
    activities.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 检查是否有搜索条件
const hasSearchConditions = () => {
  return !!(
    searchForm.value.title || 
    searchForm.value.type || 
    searchForm.value.status || 
    (searchForm.value.dateRange && searchForm.value.dateRange.length === 2)
  );
}

const resetSearch = () => {
  searchForm.value = {
    title: '',
    type: '',
    status: '',
    dateRange: []
  }
  currentPage.value = 1;
  fetchActivities();
}

// 分页处理函数
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchActivities()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchActivities()
}

const handleViewParticipants = async (row) => {
  currentActivityId.value = row.id
  participantsLoading.value = true
  try {
    await fetchParticipants()
    participantsDialogVisible.value = true
  } catch (error) {
    console.error('获取参与情况失败:', error)
    ElMessage.error(error.message || '获取参与情况失败')
  } finally {
    participantsLoading.value = false
  }
}

// 获取参与者列表
const fetchParticipants = async () => {
  try {
    const response = await request({
      url: `/api/activities/${currentActivityId.value}/participants`,
      method: 'get'
    })
    participants.value = response
  } catch (error) {
    console.error('获取参与者列表失败:', error)
    ElMessage.error(error.message || '获取参与者列表失败')
    throw error
  }
}

const handleUpdateStatus = async (row, status) => {
  try {
    await request({
      url: `/api/activities/${row.id}/status/${status}`,
      method: 'put'
    })
    ElMessage.success('状态更新成功')
    fetchActivities()
  } catch (error) {
    console.error('更新状态失败:', error)
    ElMessage.error(error.message || '更新状态失败')
  }
}

const handleAddNote = (participant) => {
  noteForm.value.participantId = participant.id
  noteForm.value.content = ''
  noteDialogVisible.value = true
}

const handleSubmitNote = async () => {
  try {
    await request({
      url: `/api/activities/${currentActivityId.value}/participants/${noteForm.value.participantId}/note`,
      method: 'post',
      data: { content: noteForm.value.content }
    })
    ElMessage.success('备注添加成功')
    noteDialogVisible.value = false
  } catch (error) {
    console.error('添加备注失败:', error)
    ElMessage.error(error.message || '添加备注失败')
  }
}

const handleSendNotification = (participant) => {
  notificationForm.value.participantId = participant.id
  notificationForm.value.title = ''
  notificationForm.value.content = ''
  notificationDialogVisible.value = true
}

const handleSubmitNotification = async () => {
  try {
    await request({
      url: `/api/activities/${currentActivityId.value}/participants/${notificationForm.value.participantId}/notification`,
      method: 'post',
      data: {
        title: notificationForm.value.title,
        content: notificationForm.value.content
      }
    })
    ElMessage.success('通知发送成功')
    notificationDialogVisible.value = false
  } catch (error) {
    console.error('发送通知失败:', error)
    ElMessage.error(error.message || '发送通知失败')
  }
}

// 获取参与状态标签
const getParticipantStatusLabel = (status) => {
  const statusMap = {
    'REGISTERED': '已报名',
    'CONFIRMED': '已确认',
    'CANCELLED': '已取消',
    'COMPLETED': '已完成'
  }
  return statusMap[status] || status
}

// 获取参与状态类型
const getParticipantStatusType = (status) => {
  const typeMap = {
    'REGISTERED': 'warning',    // 待处理 -> 已报名（黄色）
    'CONFIRMED': 'primary',     // 处理中 -> 已确认（蓝色）
    'COMPLETED': 'success',     // 已解决 -> 已完成（绿色）
    'CANCELLED': 'info'         // 已关闭 -> 已取消（灰色）
  }
  return typeMap[status] || 'info'
}

const handleUpdateParticipantStatus = async (participant, status) => {
  try {
    await request({
      url: `/api/activities/${currentActivityId.value}/participants/${participant.id}/status/${status}`,
      method: 'put'
    })
    ElMessage.success('状态更新成功')
    // 重新获取参与者列表
    await fetchParticipants()
  } catch (error) {
    console.error('更新状态失败:', error)
    ElMessage.error(error.message || '更新状态失败')
  }
}

const handleDeleteParticipant = async (participant) => {
  try {
    await ElMessageBox.confirm('确定要删除该参与者吗？', '提示', {
      type: 'warning'
    })
    
    await request({
      url: `/api/activities/${currentActivityId.value}/participants/${participant.id}`,
      method: 'delete'
    })
    
    ElMessage.success('删除成功')
    await fetchParticipants()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除参与者失败:', error)
      ElMessage.error(error.message || '删除参与者失败')
    }
  }
}

onMounted(() => {
  fetchActivities()
})
</script>

<style scoped>
.activity-management {
  padding: 20px;
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-bar {
  margin-bottom: 20px;
}

.search-form-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
}

.search-buttons {
  margin-left: auto;
  display: flex;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.activity-form {
  max-width: 600px;
  margin: 0 auto;
}

/* 标签样式统一 */
:deep(.el-tag) {
  display: inline-block;
  height: 24px;
  line-height: 24px;
  padding: 0 8px;
  border-radius: 4px;
  font-size: 12px;
  box-sizing: border-box;
  white-space: nowrap;
}

/* 表单样式 */
:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-input), :deep(.el-select), :deep(.el-date-editor) {
  width: 100%;
}

:deep(.el-select .el-input) {
  width: 100%;
}

:deep(.el-date-editor.el-input__wrapper) {
  width: 100%;
}

/* 表格样式优化 */
:deep(.el-table .cell) {
  padding: 8px;
  line-height: 1.5;
  text-align: center;
}

.activity-title {
  white-space: normal;
  word-break: break-word;
  line-height: 1.4;
  text-align: center;
  max-height: 48px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

:deep(.el-table--border) {
  border-radius: 4px;
  overflow: hidden;
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
}

/* 评论徽章样式 */
:deep(.el-badge__content.el-badge__content--primary) {
  background-color: #409EFF;
}

.status-options {
  padding: 5px;
}

.status-options .el-button {
  justify-content: flex-start;
  padding-left: 10px;
}

.el-icon--right {
  margin-left: 2px;
  font-size: 12px;
}
</style> 