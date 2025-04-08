<template>
  <div class="donation-management">
    <el-card class="main-card">
      <template #header>
        <div class="card-header">
          <span class="title">捐赠管理</span>
          <el-button type="primary" @click="handleAdd">新增捐赠</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="捐赠者姓名">
            <el-input v-model="searchForm.donorName" placeholder="请输入捐赠者姓名" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 200px">
              <el-option label="待确认" value="PENDING" />
              <el-option label="已确认" value="APPROVED" />
              <el-option label="已拒绝" value="REJECTED" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 捐赠列表 -->
      <div class="table-container">
        <el-table 
          :data="donations" 
          class="donation-table"
          border
          style="width: 100%;" 
          v-loading="loading"
        >
          <el-table-column prop="donorName" label="捐赠者" width="120" align="center" />
          <el-table-column prop="amount" label="金额" width="100" align="center">
            <template #default="scope">
              <span style="color: #67c23a; font-weight: bold;">¥{{ scope.row.amount }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="donationType" label="捐赠类型" width="90" align="center">
            <template #default="scope">
              <el-tag :type="getDonationTypeTag(scope.row.donationType)" style="width: 90%; text-align: center; display: inline-block; line-height: 24px; height: 24px;">
                {{ scope.row.donationType }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="donationDate" label="捐赠日期" width="150" align="center">
            <template #default="scope">
              {{ formatDate(scope.row.donationDate) }}
            </template>
          </el-table-column>
          <el-table-column prop="description" label="描述" width="150" align="center">
            <template #default="scope">
              <div class="centered-text">{{ scope.row.description }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="80" align="center">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)" style="width: 90%; text-align: center; display: inline-block; line-height: 24px; height: 24px;">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" align="center" fixed="right">
            <template #default="scope">
              <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
              <el-button 
                type="success" 
                link
                @click="handleUpdateStatus(scope.row, 'APPROVED')"
                :disabled="scope.row.status === 'APPROVED'"
              >确认</el-button>
              <el-button 
                type="warning" 
                link
                @click="handleUpdateStatus(scope.row, 'REJECTED')"
                :disabled="scope.row.status === 'REJECTED' || scope.row.status === 'APPROVED'"
              >拒绝</el-button>
              <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 添加分页组件 -->
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
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="捐赠者" prop="donorId">
          <el-select v-model="form.donorId" placeholder="请选择捐赠者" filterable @change="handleDonorChange">
            <el-option 
              v-for="user in userList" 
              :key="user.id" 
              :label="`${user.name} (${user.username})`" 
              :value="user.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="捐赠者姓名" prop="donorName">
          <el-input v-model="form.donorName" placeholder="选择捐赠者后自动填充" disabled />
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number v-model="form.amount" :precision="2" :step="0.1" :min="0" />
        </el-form-item>
        <el-form-item label="捐赠类型" prop="donationType">
          <el-select v-model="form.donationType" placeholder="请选择捐赠类型">
            <el-option label="现金" value="现金" />
            <el-option label="物资" value="物资" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="捐赠日期" prop="donationDate">
          <el-date-picker
            v-model="form.donationDate"
            type="datetime"
            placeholder="选择捐赠日期"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入捐赠描述"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="待确认" value="PENDING" />
            <el-option label="已确认" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import request from '@/utils/request'

// 数据列表
const donations = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const userList = ref([])

// 搜索表单
const searchForm = ref({
  donorName: '',  // 修改为donorName，与后端接口匹配
  status: ''
})

// 表单数据
const form = ref({
  donorId: '',
  donorName: '',
  amount: 0,
  donationType: '',
  donationDate: '',
  description: '',
  status: 'PENDING'
})

// 表单验证规则
const rules = {
  donorId: [
    { required: true, message: '请输入捐赠者ID', trigger: 'blur' }
  ],
  donorName: [
    { required: true, message: '请输入捐赠者姓名', trigger: 'blur' }
  ],
  amount: [
    { required: true, message: '请输入捐赠金额', trigger: 'blur' }
  ],
  donationType: [
    { required: true, message: '请选择捐赠类型', trigger: 'change' }
  ],
  donationDate: [
    { required: true, message: '请选择捐赠日期', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 获取捐赠列表
const fetchDonations = async () => {
  loading.value = true
  try {
    const params = new URLSearchParams()
    
    // 添加分页参数
    params.append('page', currentPage.value - 1) // 后端分页通常从0开始
    params.append('size', pageSize.value)
    
    const response = await request({
      url: `/api/donations?${params.toString()}`,
      method: 'get'
    })
    
    // 按捐赠日期降序排序
    if (response.content) {
      // Spring Data 分页响应格式
      const processedData = await processDonorData(response.content)
      donations.value = processedData.sort((a, b) => {
        return new Date(b.donationDate) - new Date(a.donationDate)
      })
      total.value = response.totalElements || 0
    } else {
      // 非分页响应格式
      const processedData = await processDonorData(response)
      donations.value = processedData.sort((a, b) => {
        return new Date(b.donationDate) - new Date(a.donationDate)
      })
      total.value = response.length
    }
  } catch (error) {
    console.error('获取捐赠列表失败:', error)
    ElMessage.error('获取捐赠列表失败')
    donations.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = async () => {
  // 搜索时重置为第一页
  currentPage.value = 1;
  loading.value = true
  
  try {
    const params = new URLSearchParams()
    
    // 添加分页参数
    params.append('page', currentPage.value - 1)
    params.append('size', pageSize.value)
    
    // 选择合适的API端点
    let url = '/api/donations'
    
    // 处理状态搜索 - 这个API存在
    if (searchForm.value.status) {
      url = `/api/donations/status/${searchForm.value.status}`
    }
    
    console.log('使用API:', url)
    
    const response = await request({
      url: `${url}?${params.toString()}`,
      method: 'get'
    })
    
    // 处理响应数据
    let donationData = []
    let totalCount = 0
    
    if (response && response.content) {
      // 分页响应
      donationData = response.content
      totalCount = response.totalElements || 0
    } else if (Array.isArray(response)) {
      // 数组响应
      donationData = response
      totalCount = response.length
    } else if (response) {
      // 其他情况，尝试使用响应
      donationData = [response]
      totalCount = 1
    }
    
    // 处理捐赠者数据
    let processedData = await processDonorData(donationData)
    
    // 如果有donorName搜索条件，在前端过滤
    if (searchForm.value.donorName && searchForm.value.donorName.trim() !== '') {
      const keyword = searchForm.value.donorName.toLowerCase()
      processedData = processedData.filter(donation => 
        donation.donorName && donation.donorName.toLowerCase().includes(keyword)
      )
      totalCount = processedData.length
    }
    
    // 使用后端返回的顺序
    donations.value = processedData
    total.value = totalCount
    
    if (processedData.length === 0) {
      ElMessage.info('没有找到符合条件的捐赠记录')
    }
  } catch (error) {
    console.error('搜索失败:', error)
    
    // 提供详细的错误信息
    let errorMsg = '搜索失败'
    if (error.response) {
      if (error.response.status === 400) {
        errorMsg = '搜索参数错误，请检查输入'
      } else if (error.response.status === 404) {
        errorMsg = '找不到符合条件的数据'
      } else {
        errorMsg = `搜索失败(${error.response.status}): ${error.response.data?.message || '未知错误'}`
      }
    } else if (error.message) {
      errorMsg = `搜索失败: ${error.message}`
    }
    
    ElMessage.error(errorMsg)
    donations.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    donorName: '',
    status: ''
  }
  currentPage.value = 1;
  fetchDonations() // 直接调用fetchDonations，而不是handleSearch
}

// 新增捐赠
const handleAdd = async () => {
  dialogTitle.value = '新增捐赠'
  form.value = {
    donorId: '',
    donorName: '',
    amount: 0,
    donationType: '',
    donationDate: '',
    description: '',
    status: 'PENDING'
  }
  await fetchUserList()
  dialogVisible.value = true
}

// 编辑捐赠
const handleEdit = async (row) => {
  dialogTitle.value = '编辑捐赠'
  form.value = { ...row }
  await fetchUserList()
  dialogVisible.value = true
}

// 删除捐赠
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该捐赠记录吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await request({
        url: `/api/donations/${row.id}`,
        method: 'delete'
      })
      ElMessage.success('删除成功')
      fetchDonations()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 更新状态
const handleUpdateStatus = async (row, status) => {
  try {
    await request({
      url: `/api/donations/${row.id}/status`,
      method: 'put',
      params: { status }
    })
    ElMessage.success('状态更新成功')
    fetchDonations()
  } catch (error) {
    ElMessage.error('状态更新失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        console.log('提交的表单数据：', form.value)
        // 确保金额是数字类型
        form.value.amount = Number(form.value.amount)
        // 确保捐赠者ID是数字类型
        form.value.donorId = Number(form.value.donorId)
        // 确保捐赠日期格式正确
        if (form.value.donationDate) {
          form.value.donationDate = dayjs(form.value.donationDate).format('YYYY-MM-DD HH:mm:ss')
        }
        
        if (form.value.id) {
          const response = await request({
            url: `/api/donations/${form.value.id}`,
            method: 'put',
            data: form.value
          })
          console.log('更新捐赠响应：', response)
        } else {
          const response = await request({
            url: '/api/donations',
            method: 'post',
            data: form.value
          })
          console.log('新增捐赠响应：', response)
        }
        ElMessage.success('保存成功')
        dialogVisible.value = false
        fetchDonations()
      } catch (error) {
        console.error('保存失败：', error)
        if (error.response) {
          console.error('错误响应：', error.response.data)
          ElMessage.error(error.response.data?.message || '保存失败')
        } else {
          ElMessage.error('保存失败：' + (error.message || '未知错误'))
        }
      }
    }
  })
}

// 格式化日期
const formatDate = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm:ss') : ''
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    PENDING: 'warning',
    APPROVED: 'success',
    REJECTED: 'danger'
  }
  return types[status] || 'info'
}

// 获取捐赠类型标签样式
const getDonationTypeTag = (type) => {
  const types = {
    '现金': 'success',
    '物资': 'primary',
    '其他': 'info'
  }
  return types[type] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    PENDING: '待确认',
    APPROVED: '已确认',
    REJECTED: '已拒绝'
  }
  return texts[status] || status
}

// 分页处理函数
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchDonations()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchDonations()
}

// 根据捐赠者ID获取捐赠者姓名
const processDonorData = async (donations) => {
  if (!donations || !donations.length) return []
  
  try {
    // 收集所有捐赠者ID
    const donorIds = [...new Set(donations.map(d => d.donorId))]
    
    // 批量获取用户信息
    const userPromises = donorIds.map(id => 
      request({
        url: `/api/users/${id}`,
        method: 'get'
      }).catch(err => {
        console.warn(`获取用户ID ${id} 失败:`, err)
        return { id, name: `用户${id}` }  // 不再嵌套在data属性中
      })
    )
    
    const userResponses = await Promise.all(userPromises)
    const userMap = {}
    
    userResponses.forEach(res => {
      if (res) {
        userMap[res.id] = res.name || `用户${res.id}`
      }
    })
    
    // 为每条捐赠记录添加捐赠者姓名
    return donations.map(donation => ({
      ...donation,
      donorName: userMap[donation.donorId] || `用户${donation.donorId}`
    }))
  } catch (error) {
    console.error('处理捐赠者数据失败:', error)
    // 如果出错，至少返回原始数据
    return donations.map(donation => ({
      ...donation,
      donorName: `用户${donation.donorId}`
    }))
  }
}

// 获取用户列表
const fetchUserList = async () => {
  try {
    const response = await request({
      url: '/api/users?size=1000',
      method: 'get'
    })
    if (response && response.content) {  // 去掉data层级
      userList.value = response.content
        // 不再过滤角色，允许所有用户类型
        .sort((a, b) => a.name.localeCompare(b.name, 'zh-CN')) // 按姓名排序
    } else {
      userList.value = []
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    userList.value = []
  }
}

// 当用户选择捐赠者时自动填充捐赠者姓名
const handleDonorChange = (donorId) => {
  const selectedUser = userList.value.find(user => user.id === donorId)
  if (selectedUser) {
    form.value.donorName = selectedUser.name
  } else {
    form.value.donorName = ''
  }
}

onMounted(() => {
  fetchDonations()
})
</script>

<style scoped>
.donation-management {
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

.donation-table {
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
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
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style> 