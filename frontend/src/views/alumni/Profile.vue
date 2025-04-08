<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
          <el-button type="primary" @click="handleEdit">编辑</el-button>
        </div>
      </template>
      
      <div v-if="!isEditing" class="profile-info">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ userInfo.name }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ getGenderText(userInfo.gender) }}</el-descriptions-item>
          <el-descriptions-item label="学号">{{ userInfo.studentId }}</el-descriptions-item>
          <el-descriptions-item label="专业">{{ userInfo.major }}</el-descriptions-item>
          <el-descriptions-item label="毕业年份">{{ userInfo.graduationYear }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ userInfo.email }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ userInfo.phone }}</el-descriptions-item>
          <el-descriptions-item label="当前公司">{{ userInfo.currentCompany || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="当前职位">{{ userInfo.currentPosition || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="所在行业">{{ userInfo.industry || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="所在地">{{ userInfo.location || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="个人简介" :span="2">{{ userInfo.bio || '未填写' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      
      <div v-else class="profile-form">
        <el-form
          ref="profileFormRef"
          :model="profileForm"
          :rules="profileRules"
          label-width="120px"
        >
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="用户名">
                <el-input v-model="profileForm.username" disabled />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="姓名" prop="name">
                <el-input v-model="profileForm.name" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="性别" prop="gender">
                <el-select v-model="profileForm.gender" placeholder="请选择性别">
                  <el-option label="男" value="MALE" />
                  <el-option label="女" value="FEMALE" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="学号">
                <el-input v-model="profileForm.studentId" disabled />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="专业">
                <el-input v-model="profileForm.major" disabled />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="毕业年份">
                <el-input v-model="profileForm.graduationYear" disabled />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="profileForm.email" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="profileForm.phone" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="当前公司" prop="currentCompany">
                <el-input v-model="profileForm.currentCompany" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="当前职位" prop="currentPosition">
                <el-input v-model="profileForm.currentPosition" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="所在行业" prop="industry">
                <el-input v-model="profileForm.industry" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="所在地" prop="location">
                <el-input v-model="profileForm.location" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="个人简介" prop="bio">
            <el-input
              v-model="profileForm.bio"
              type="textarea"
              :rows="4"
              placeholder="请输入个人简介"
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
            <el-button @click="handleCancel">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElLoading } from 'element-plus'
import request from '@/utils/request'

const userInfo = ref({})
const isEditing = ref(false)
const saving = ref(false)
const profileFormRef = ref(null)

// 获取性别文本
const getGenderText = (gender) => {
  const genderMap = {
    'MALE': '男',
    'FEMALE': '女'
  }
  return genderMap[gender] || gender
}

// 获取当前用户信息
const fetchCurrentUser = async () => {
  try {
    const currentUsername = localStorage.getItem('username')
    const token = localStorage.getItem('token')
    
    if (!currentUsername) {
      ElMessage.error('未找到用户信息，请重新登录')
      return false
    }
    
    const response = await request({
      url: '/api/users/current',
      method: 'get',
      params: {
        username: currentUsername
      },
      headers: token ? {
        'Authorization': `Bearer ${token}`,
        'X-Current-Username': currentUsername
      } : undefined
    })
    
    if (response && response.success && response.data) {
      userInfo.value = {
        id: response.data.id,
        username: response.data.username,
        name: response.data.name,
        gender: response.data.gender,
        email: response.data.email,
        phone: response.data.phone,
        role: response.data.role,
        status: response.data.status,
        studentId: response.data.studentId,
        major: response.data.major,
        graduationYear: response.data.graduationYear,
        createdAt: response.data.createdAt,
        lastLogin: response.data.lastLogin,
        
        // 校友信息
        currentCompany: response.data.currentCompany || '',
        currentPosition: response.data.currentPosition || '',
        industry: response.data.industry || '',
        location: response.data.location || '',
        bio: response.data.bio || ''
      }
      
      return true
    } else {
      ElMessage.error('获取用户信息失败')
      return false
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    if (error.response?.status === 401) {
      ElMessage.error('您的登录已过期，请重新登录')
    } else {
      ElMessage.error('获取用户信息失败：' + error.message)
    }
    return false
  }
}

// 表单验证规则
const profileRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

// 个人信息表单数据
const profileForm = reactive({
  username: '',
  name: '',
  gender: '',
  email: '',
  phone: '',
  studentId: '',
  major: '',
  graduationYear: '',
  currentCompany: '',
  currentPosition: '',
  industry: '',
  location: '',
  bio: ''
})

// 处理编辑按钮点击
const handleEdit = () => {
  // 用当前用户信息填充表单
  Object.assign(profileForm, userInfo.value)
  isEditing.value = true
}

// 处理取消按钮点击
const handleCancel = () => {
  isEditing.value = false
}

// 处理保存按钮点击
const handleSave = async () => {
  if (!profileFormRef.value) return
  
  try {
    // 表单验证
    await profileFormRef.value.validate()
    
    // 显示加载指示器
    saving.value = true
    
    // 准备提交数据
    const submitData = {
      name: profileForm.name,
      gender: profileForm.gender,
      email: profileForm.email,
      phone: profileForm.phone,
      currentCompany: profileForm.currentCompany,
      currentPosition: profileForm.currentPosition,
      industry: profileForm.industry,
      location: profileForm.location,
      bio: profileForm.bio
    }
    
    // 获取当前用户身份认证信息
    const token = localStorage.getItem('token')
    const username = localStorage.getItem('username')
    
    if (!token) {
      ElMessage.error('请先登录再进行编辑操作')
      return
    }
    
    // 发送更新请求
    const response = await request({
      url: `/api/users/${userInfo.value.id}`,
      method: 'put',
      data: {
        ...submitData,
        currentUsername: username
      },
      headers: {
        'Authorization': `Bearer ${token}`,
        'X-Current-Username': username
      }
    })
    
    if (response && response.success) {
      ElMessage.success('更新个人信息成功')
      isEditing.value = false
      // 刷新用户信息
      await fetchCurrentUser()
    } else {
      ElMessage.error('更新个人信息失败')
    }
  } catch (error) {
    console.error('更新个人信息失败:', error)
    if (error.response?.status === 401) {
      ElMessage.error('认证已过期，请重新登录')
    } else {
      ElMessage.error('更新个人信息失败：' + error.message)
    }
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  fetchCurrentUser()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-info {
  margin-top: 20px;
}

.profile-form {
  margin-top: 20px;
}
</style> 