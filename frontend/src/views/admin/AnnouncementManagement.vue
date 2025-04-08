<template>
  <div class="announcement-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="header-text">公告管理</span>
          <el-button type="primary" @click="handleAdd">新增公告</el-button>
        </div>
      </template>

      <el-table
        :data="announcements"
        style="width: 100%"
        border
        stripe
        v-loading="loading"
      >
        <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="content" label="内容" min-width="250" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="centered-text">{{ row.content }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-dropdown 
              @command="(command) => handleStatusChange(row, command)"
              trigger="click"
            >
              <el-tag
                :type="getStatusType(row.status)"
                style="cursor: pointer; width: 90%; text-align: center; display: inline-block; line-height: 24px; height: 24px;"
              >
                {{ getStatusLabel(row.status) }}<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-tag>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item
                    v-for="option in statusOptions"
                    :key="option.value"
                    :command="option.value"
                  >
                    {{ option.label }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="160" align="center">
          <template #default="{ row }">
            {{ formatDateTime(row.publishTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160" align="center">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <div class="operation-buttons">
              <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
              <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

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

      <el-dialog
        v-model="dialogVisible"
        :title="dialogType === 'add' ? '新增公告' : '编辑公告'"
        width="600px"
        :close-on-click-modal="false"
        destroy-on-close
      >
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="80px"
          class="announcement-form"
        >
          <el-form-item label="标题" prop="title">
            <el-input v-model="form.title" placeholder="请输入公告标题" />
          </el-form-item>
          <el-form-item label="内容" prop="content">
            <el-input
              v-model="form.content"
              type="textarea"
              :rows="6"
              placeholder="请输入公告内容"
            />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="form.status" placeholder="请选择状态" class="status-select">
              <el-option
                v-for="option in statusOptions"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              />
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSubmit">确定</el-button>
          </div>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { ArrowDown } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const loading = ref(false)
const announcements = ref([])
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const form = ref({
  title: '',
  content: '',
  status: 'DRAFT'
})

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const rules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入内容', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

const statusOptions = [
  { label: '草稿', value: 'DRAFT' },
  { label: '已发布', value: 'PUBLISHED' },
  { label: '已取消', value: 'CANCELLED' }
]

const authStore = useAuthStore()

const getAuthHeaders = () => {
  const currentUsername = authStore.userInfo?.username
  
  return {
    'X-Current-Username': currentUsername || 'admin'
  };
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const fetchAnnouncements = async () => {
  loading.value = true
  try {
    const response = await request({
      url: '/api/announcements',
      method: 'get'
    })
    
    console.log('Fetch announcements response:', response)
    
    if (Array.isArray(response)) {
      // 后端直接返回数组
      total.value = response.length
      const startIndex = (currentPage.value - 1) * pageSize.value
      const endIndex = startIndex + pageSize.value
      announcements.value = response.slice(startIndex, endIndex)
    } else {
      console.error('接口返回的数据格式不正确:', response)
      announcements.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取公告列表失败:', error)
    ElMessage.error('获取公告列表失败')
    announcements.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogType.value = 'add'
  form.value = {
    title: '',
    content: '',
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
    await ElMessageBox.confirm('确定要删除该公告吗？', '提示', {
      type: 'warning'
    })
    
    await request({
      url: `/api/announcements/${row.id}`,
      method: 'delete'
    })
    
    ElMessage.success('删除成功')
    await fetchAnnouncements()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除公告失败:', error)
      ElMessage.error('删除公告失败')
    }
  }
}

const getStatusType = (status) => {
  const typeMap = {
    'DRAFT': 'info',
    'PUBLISHED': 'success',
    'CANCELLED': 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusLabel = (status) => {
  const labelMap = {
    'DRAFT': '草稿',
    'PUBLISHED': '已发布',
    'CANCELLED': '已取消'
  }
  return labelMap[status] || '未知状态'
}

const handleStatusChange = async (row, value) => {
  try {
    console.log('开始状态变更:', {
      currentStatus: row.status,
      newStatus: value,
      rowData: row
    })
    
    const currentTime = value === 'PUBLISHED' 
      ? new Date().toLocaleString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit',
          hour12: false
        }).replace(/\//g, '-')
      : null
    
    const updateData = {
      id: row.id,
      title: row.title,
      content: row.content,
      status: value,
      publishTime: currentTime
    }
    
    console.log('准备发送的更新数据:', updateData)
    
    const response = await request({
      url: `/api/announcements/${row.id}`,
      method: 'put',
      data: updateData
    })
    
    console.log('服务器响应:', response)
    
    if (response) {
      ElMessage.success('状态更新成功')
      await fetchAnnouncements()
    } else {
      console.error('状态更新失败:', response)
      ElMessage.error('状态更新失败')
      await fetchAnnouncements()
    }
  } catch (error) {
    console.error('状态更新出错:', error)
    ElMessage.error('状态更新失败')
    await fetchAnnouncements()
  }
}

const handleSubmit = async () => {
  if (!formRef.value) {
    console.error('Form reference is not available')
    return
  }

  try {
    await formRef.value.validate()
    console.log('Form validation passed, submitting data:', form.value)
    
    const currentTime = form.value.status === 'PUBLISHED' 
      ? new Date().toLocaleString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit',
          hour12: false
        }).replace(/\//g, '-')
      : null
    
    if (form.value.id) {
      const response = await request({
        url: `/api/announcements/${form.value.id}`,
        method: 'put',
        data: {
          id: form.value.id,
          title: form.value.title,
          content: form.value.content,
          status: form.value.status,
          publishTime: currentTime
        }
      })
      
      if (response) {
        ElMessage.success('更新成功')
        dialogVisible.value = false
        await fetchAnnouncements()
      } else {
        ElMessage.error('更新失败')
      }
    } else {
      const response = await request({
        url: '/api/announcements',
        method: 'post',
        data: {
          title: form.value.title,
          content: form.value.content,
          status: form.value.status,
          publishTime: currentTime
        }
      })
      
      if (response) {
        ElMessage.success('创建成功')
        dialogVisible.value = false
        await fetchAnnouncements()
      } else {
        ElMessage.error('创建失败')
      }
    }
  } catch (error) {
    console.error('Form validation or submission error:', error)
    if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('表单验证或提交失败')
    }
  }
}

const handleSizeChange = (size) => {
  pageSize.value = size
  fetchAnnouncements()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchAnnouncements()
}

onMounted(() => {
  fetchAnnouncements()
})
</script>

<style scoped>
.announcement-management {
  padding: 20px;
  width: 100%;
}

.box-card {
  margin-bottom: 20px;
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-text {
  font-size: 16px;
  font-weight: normal;
  color: #303133;
}

.announcement-form {
  padding: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 20px 0 0;
}

/* 分页样式优化 */
:deep(.el-pagination) {
  padding: 0;
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
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
  margin-top: 20px;
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

/* 状态选择器样式 */
.status-select {
  width: 90px;
}

.status-select :deep(.el-input__wrapper) {
  background-color: transparent;
  box-shadow: none !important;
  padding: 0;
}

.status-select :deep(.el-input__inner) {
  color: var(--el-text-color-regular);
  font-size: 13px;
  text-align: center;
  cursor: pointer;
}

:deep(.el-select-dropdown.status-select) {
  min-width: 90px !important;
}

:deep(.el-select-dropdown__item) {
  padding: 8px 12px;
  font-size: 13px;
  display: flex;
  align-items: center;
  justify-content: center;
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

/* 操作按钮样式 */
.operation-buttons {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.operation-buttons .el-button {
  padding: 2px 6px;
  margin: 0;
}
</style> 