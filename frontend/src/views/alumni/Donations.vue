<template>
  <div class="donations-container">
    <el-card class="donations-card">
      <template #header>
        <div class="card-header">
          <span>我的捐赠</span>
          <el-button type="primary" @click="handleAddDonation">新增捐赠</el-button>
        </div>
      </template>
      
      <div class="donations-content">
        <el-table
          v-loading="loading"
          :data="donations"
          style="width: 100%"
          border
        >
          <el-table-column prop="id" label="捐赠编号" width="100" />
          <el-table-column prop="amount" label="捐赠金额" width="120">
            <template #default="scope">
              ¥{{ scope.row.amount }}
            </template>
          </el-table-column>
          <el-table-column prop="purpose" label="捐赠用途" min-width="200" show-overflow-tooltip />
          <el-table-column prop="donationTime" label="捐赠时间" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.donationTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getDonationStatusType(scope.row.status)">
                {{ getDonationStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="scope">
              <el-button
                type="primary"
                size="small"
                @click="handleViewDonation(scope.row)"
              >
                查看
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div v-if="donations.length === 0 && !loading" class="no-data">
          <el-empty description="暂无捐赠记录" />
        </div>
      </div>
    </el-card>
    
    <!-- 捐赠表单对话框 -->
    <el-dialog
      v-model="donationDialogVisible"
      :title="isEdit ? '编辑捐赠' : '新增捐赠'"
      width="50%"
      :close-on-click-modal="false"
    >
      <el-form
        ref="donationFormRef"
        :model="donationForm"
        :rules="donationRules"
        label-width="100px"
      >
        <el-form-item label="捐赠金额" prop="amount">
          <el-input-number
            v-model="donationForm.amount"
            :min="0.01"
            :precision="2"
            :step="100"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="捐赠用途" prop="purpose">
          <el-input
            v-model="donationForm.purpose"
            type="textarea"
            :rows="3"
            placeholder="请输入捐赠用途"
          />
        </el-form-item>
        
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="donationForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息（选填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="donationDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitDonation" :loading="submitting">
            提交
          </el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 捐赠详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="捐赠详情"
      width="50%"
      :close-on-click-modal="true"
    >
      <div class="donation-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="捐赠编号">{{ currentDonation.id }}</el-descriptions-item>
          <el-descriptions-item label="捐赠状态">
            <el-tag :type="getDonationStatusType(currentDonation.status)">
              {{ getDonationStatusText(currentDonation.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="捐赠金额">¥{{ currentDonation.amount }}</el-descriptions-item>
          <el-descriptions-item label="捐赠时间">{{ formatDate(currentDonation.donationTime) }}</el-descriptions-item>
          <el-descriptions-item label="捐赠用途" :span="2">{{ currentDonation.purpose }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ currentDonation.remark || '无' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import dayjs from 'dayjs'

// 捐赠列表
const donations = ref([])
const loading = ref(false)
const donationDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const donationFormRef = ref(null)
const currentDonation = ref({})

// 捐赠表单数据
const donationForm = reactive({
  id: null,
  amount: 100,
  purpose: '',
  remark: ''
})

// 表单验证规则
const donationRules = {
  amount: [
    { required: true, message: '请输入捐赠金额', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '捐赠金额必须大于0', trigger: 'blur' }
  ],
  purpose: [
    { required: true, message: '请输入捐赠用途', trigger: 'blur' },
    { min: 5, max: 500, message: '长度在 5 到 500 个字符', trigger: 'blur' }
  ]
}

// 获取捐赠列表
const fetchDonations = async () => {
  loading.value = true
  try {
    const response = await request({
      url: '/api/donations/my',
      method: 'get'
    })
    if (response && response.success) {
      donations.value = response.data
    }
  } catch (error) {
    console.error('获取捐赠列表失败:', error)
    ElMessage.error('获取捐赠列表失败')
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

// 获取捐赠状态类型
const getDonationStatusType = (status) => {
  const statusMap = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取捐赠状态文本
const getDonationStatusText = (status) => {
  const statusMap = {
    'PENDING': '待审核',
    'APPROVED': '已确认',
    'REJECTED': '已拒绝'
  }
  return statusMap[status] || status
}

// 处理新增捐赠
const handleAddDonation = () => {
  // 重置表单
  Object.assign(donationForm, {
    id: null,
    amount: 100,
    purpose: '',
    remark: ''
  })
  isEdit.value = false
  donationDialogVisible.value = true
}

// 处理查看捐赠
const handleViewDonation = (donation) => {
  currentDonation.value = donation
  detailDialogVisible.value = true
}

// 处理提交捐赠
const handleSubmitDonation = async () => {
  if (!donationFormRef.value) return
  
  try {
    // 表单验证
    await donationFormRef.value.validate()
    
    // 显示加载指示器
    submitting.value = true
    
    // 准备提交数据
    const submitData = {
      amount: donationForm.amount,
      purpose: donationForm.purpose,
      remark: donationForm.remark
    }
    
    // 发送请求
    const response = await request({
      url: '/api/donations',
      method: 'post',
      data: submitData
    })
    
    if (response && response.success) {
      ElMessage.success('提交捐赠成功')
      donationDialogVisible.value = false
      // 刷新捐赠列表
      await fetchDonations()
    } else {
      ElMessage.error('提交捐赠失败')
    }
  } catch (error) {
    console.error('提交捐赠失败:', error)
    ElMessage.error('提交捐赠失败：' + error.message)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchDonations()
})
</script>

<style scoped>
.donations-container {
  padding: 20px;
}

.donations-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.donations-content {
  margin-top: 20px;
}

.no-data {
  margin-top: 20px;
  text-align: center;
}

.donation-detail {
  padding: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 