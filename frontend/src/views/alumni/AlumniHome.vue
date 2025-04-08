<template>
  <div class="alumni-container">
    <!-- 左侧菜单 -->
    <div class="sidebar">
      <el-menu
        :default-active="activeMenu"
        class="alumni-menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        :router="true"
      >
        <el-menu-item index="/alumni/Dashboard">
          <el-icon><Monitor /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/alumni/Profile">
          <el-icon><User /></el-icon>
          <span>个人信息</span>
        </el-menu-item>
        <el-menu-item index="/alumni/Activities">
          <el-icon><Calendar /></el-icon>
          <span>活动参与</span>
        </el-menu-item>
        <el-menu-item index="/alumni/Donations">
          <el-icon><Money /></el-icon>
          <span>我的捐赠</span>
        </el-menu-item>
        <el-menu-item index="/alumni/Feedback">
          <el-icon><Message /></el-icon>
          <span>意见反馈</span>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 右侧内容区 -->
    <div class="main-content">
      <!-- 顶部导航栏 -->
      <div class="top-bar">
        <div class="user-info">
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              {{ userInfo.name || username }} ({{ getRoleName(userInfo.role) }})
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 主要内容区 -->
      <div class="content">
        <router-view></router-view>
      </div>
    </div>

    <!-- 个人信息对话框 -->
    <el-dialog
      v-model="profileDialogVisible"
      title="个人信息"
      width="75%"
      :close-on-click-modal="true"
      class="profile-dialog"
    >
      <el-form
        ref="profileFormRef"
        :model="profileForm"
        :rules="profileRules"
        label-width="120px"
        class="profile-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
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
            <el-form-item label="学号" prop="studentId">
              <el-input v-model="profileForm.studentId" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="专业" prop="major">
              <el-input v-model="profileForm.major" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="毕业年份" prop="graduationYear">
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
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="profileDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleUpdateProfile" :loading="updating">
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElLoading } from 'element-plus'
import { Monitor, User, Calendar, Money, Message, ArrowDown } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()
const activeMenu = ref(route.path)
const profileDialogVisible = ref(false)
const profileFormRef = ref(null)
const updating = ref(false)
const username = ref('')
const userInfo = ref({})

// 获取角色名称
const getRoleName = (role) => {
  const roleMap = {
    'SUPER_ADMIN': '超级管理员',
    'ADMIN': '管理员',
    'ALUMNI': '校友'
  }
  return roleMap[role] || role
}

// 获取当前用户信息
const fetchCurrentUser = async () => {
  try {
    console.log('开始获取用户信息...')
    const currentUsername = localStorage.getItem('username')
    const token = localStorage.getItem('token')
    
    if (!currentUsername) {
      console.log('未找到用户名')
      return
    }
    
    console.log('使用用户名获取信息:', currentUsername)
    
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
    console.log('获取到的用户信息响应:', response)
    
    if (response && response.success && response.data) {
      console.log('获取到的用户数据:', response.data)
      
      // 确保userInfo包含所有必要字段
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
      
      username.value = response.data.username
      console.log('更新后的userInfo:', userInfo.value)
      
      // 更新页面顶部显示的用户名
      updateUserDisplay()
      
      return true
    } else {
      console.log('获取用户信息失败:', response)
      if (response && response.message && response.message.includes('登录')) {
        // 如果响应中包含"登录"字样，可能是认证过期
        ElMessage.error('您的登录已过期，请重新登录')
        // 清除本地存储
        localStorage.removeItem('token')
        localStorage.removeItem('userRole')
        localStorage.removeItem('userId')
        localStorage.removeItem('username')
        // 跳转到登录页
        router.push('/login')
      } else {
        ElMessage.error('获取用户信息失败')
      }
      return false
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    if (error.response?.status === 401) {
      ElMessage.error('您的登录已过期，请重新登录')
      // 清除本地存储
      localStorage.removeItem('token')
      localStorage.removeItem('userRole')
      localStorage.removeItem('userId')
      localStorage.removeItem('username')
      // 跳转到登录页
      router.push('/login')
    } else {
      ElMessage.error('获取用户信息失败：' + error.message)
    }
    return false
  }
}

// 更新页面上显示的用户信息
const updateUserDisplay = () => {
  // 尝试更新页面顶部显示的用户名
  try {
    const dropdownLink = document.querySelector('.el-dropdown-link')
    if (dropdownLink) {
      // 从Vue响应式对象获取最新值
      const displayName = userInfo.value.name || username.value
      const roleName = getRoleName(userInfo.value.role)
      
      // 更新下拉菜单显示
      const arrowIcon = '<i class="el-icon el-icon--right"><svg viewBox="0 0 1024 1024"><path fill="currentColor" d="M831.872 340.864 512 652.672 192.128 340.864a30.592 30.592 0 0 0-42.752 0 29.12 29.12 0 0 0 0 41.6L489.664 714.24a32 32 0 0 0 44.672 0l340.288-331.712a29.12 29.12 0 0 0 0-41.728 30.592 30.592 0 0 0-42.752 0z"></path></svg></i>'
      dropdownLink.innerHTML = `${displayName} (${roleName}) ${arrowIcon}`
      
      console.log('已更新页面显示的用户名:', displayName)
    } else {
      console.warn('未找到下拉菜单元素，无法更新显示')
    }
  } catch (error) {
    console.error('更新用户显示失败:', error)
  }
}

// 处理下拉菜单命令
const handleCommand = (command) => {
  if (command === 'profile') {
    console.log('打开个人信息对话框')
    
    // 打开对话框前先获取最新用户信息
    fetchCurrentUser().then(() => {
      console.log('已获取最新用户信息，打开对话框', userInfo.value)
      profileDialogVisible.value = true
    }).catch(error => {
      console.error('获取用户信息失败，无法打开个人信息对话框:', error)
      ElMessage.error('获取用户信息失败，请稍后再试')
    })
  } else if (command === 'logout') {
    handleLogout()
  }
}

// 处理退出登录
const handleLogout = async () => {
  try {
    await request({
      url: '/api/users/logout',
      method: 'post'
    })
    // 清除本地存储
    localStorage.removeItem('token')
    localStorage.removeItem('userRole')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    // 清除用户信息
    userInfo.value = {}
    username.value = ''
    console.log('退出登录，清除本地存储')
    // 跳转到登录页
    router.push({ path: '/login' })
  } catch (error) {
    console.error('退出登录失败:', error)
    // 即使API调用失败，也清除本地存储并跳转到登录页
    localStorage.removeItem('token')
    localStorage.removeItem('userRole')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    userInfo.value = {}
    username.value = ''
    router.push({ path: '/login' })
  }
}

// 处理更新个人信息
const handleUpdateProfile = async () => {
  if (!profileFormRef.value) return
  
  try {
    console.log('开始验证表单...')
    await profileFormRef.value.validate()
    console.log('表单验证通过')
    
    // 获取当前用户身份认证信息
    const token = localStorage.getItem('token')
    const currentUsername = localStorage.getItem('username')
    
    if (!token || !currentUsername) {
      console.error('未找到身份认证信息')
      ElMessage.error('请先登录再进行编辑操作')
      router.push('/login')
      return
    }
    
    // 准备要更新的数据，确保所有字段都包含在内
    const updateData = {
      id: userInfo.value.id,
      username: userInfo.value.username,
      name: profileForm.name,
      gender: profileForm.gender,
      studentId: userInfo.value.studentId,
      major: userInfo.value.major,
      graduationYear: userInfo.value.graduationYear,
      email: profileForm.email,
      phone: profileForm.phone,
      currentCompany: profileForm.currentCompany || '',
      currentPosition: profileForm.currentPosition || '',
      industry: profileForm.industry || '',
      location: profileForm.location || '',
      bio: profileForm.bio || '',
      // 添加当前用户信息
      currentUsername: currentUsername
    }
    
    console.log('准备发送更新请求，数据:', updateData)
    
    // 显示加载指示器
    updating.value = true
    
    // 发送更新请求
    const response = await request({
      url: `/api/users/${userInfo.value.id}`,
      method: 'put',
      data: updateData,
      headers: {
        'Authorization': `Bearer ${token}`,
        'X-Current-Username': currentUsername
      }
    })
    
    console.log('更新个人信息响应:', response)
    
    if (response && response.success) {
      ElMessage.success('更新个人信息成功')
      profileDialogVisible.value = false
      // 刷新用户信息
      await fetchCurrentUser()
    } else {
      ElMessage.error(response?.message || '更新个人信息失败')
    }
  } catch (error) {
    console.error('更新个人信息失败:', error)
    if (error.response?.status === 401) {
      ElMessage.error('认证已过期，请重新登录')
      // 清除本地存储的令牌和用户信息
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      // 跳转到登录页
      router.push('/login')
    } else {
      ElMessage.error('更新个人信息失败：' + (error.message || '未知错误'))
    }
  } finally {
    updating.value = false
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

// 监听对话框显示状态，更新表单数据
watch(profileDialogVisible, (newVal) => {
  if (newVal) {
    // 对话框打开时，用当前用户信息填充表单
    Object.assign(profileForm, userInfo.value)
  }
})

onMounted(() => {
  // 先从本地存储获取用户名
  username.value = localStorage.getItem('username') || ''
  console.log('从本地存储获取的用户名：', username.value)
  
  // 然后获取最新的用户信息
  fetchCurrentUser().then(() => {
    console.log('onMounted中已成功获取最新用户信息')
  }).catch(error => {
    console.error('onMounted中获取用户信息失败：', error)
  })
})
</script>

<style scoped>
.alumni-container {
  display: flex;
  height: 100vh;
  width: 100%;
}

.sidebar {
  width: 200px;
  background-color: #304156;
  height: 100%;
  overflow-y: auto;
}

.alumni-menu {
  border-right: none;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: #f0f2f5;
}

.top-bar {
  height: 60px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 20px;
}

.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f0f2f5;
}

.user-info {
  cursor: pointer;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  color: #606266;
}

.el-dropdown-link .el-icon {
  margin-left: 5px;
}

.profile-dialog {
  max-width: 800px;
}

.profile-form {
  max-width: 600px;
  margin: 0 auto;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 