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
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="评论" width="100" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewComments(row)">查看评论</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
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

    <!-- 评论对话框 -->
    <el-dialog
      v-model="commentDialogVisible"
      title="活动评论"
      width="60%"
      :close-on-click-modal="false"
    >
      <ActivityComment :activity-id="currentActivityId" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatDateTime } from '@/utils/date'
import request from '@/utils/request'
import ActivityComment from '@/components/ActivityComment.vue'

const loading = ref(false)
const activities = ref([])
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const commentDialogVisible = ref(false)
const currentActivityId = ref(null)

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
    { required: true, message: '请输入活动标题', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入活动描述', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  location: [
    { required: true, message: '请输入活动地点', trigger: 'blur' }
  ],
  maxParticipants: [
    { required: true, message: '请输入最大参与人数', trigger: 'blur' }
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

const getStatusType = (status) => {
  switch (status) {
    case 'DRAFT':
      return 'info'
    case 'PUBLISHED':
      return 'warning'
    case 'ONGOING':
      return 'success'
    case 'COMPLETED':
      return 'info'
    case 'CANCELLED':
      return 'danger'
    default:
      return 'info'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 'DRAFT':
      return '草稿'
    case 'PUBLISHED':
      return '已发布'
    case 'ONGOING':
      return '进行中'
    case 'COMPLETED':
      return '已结束'
    case 'CANCELLED':
      return '已取消'
    case 'NOT_STARTED':
      return '未开始'
    case 'IN_PROGRESS':
      return '进行中'
    default:
      return status
  }
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
    await formRef.value.validate()
    
    const url = dialogType.value === 'add' ? '/api/activities' : `/api/activities/${form.value.id}`
    const method = dialogType.value === 'add' ? 'post' : 'put'
    
    await request({
      url,
      method,
      data: form.value
    })
    
    ElMessage.success(dialogType.value === 'add' ? '添加成功' : '更新成功')
    dialogVisible.value = false
    fetchActivities()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(error.message || '提交失败')
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

const handleViewComments = (row) => {
  currentActivityId.value = row.id
  commentDialogVisible.value = true
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
</style> 