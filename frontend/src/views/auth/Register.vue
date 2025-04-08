<template>
  <div class="register-container">
    <div class="system-title">
      <h1>校友信息管理系统</h1>
    </div>
    <el-card class="register-card">
      <template #header>
        <div class="card-header">
          <h2>用户注册</h2>
        </div>
      </template>
      
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="rules"
        label-width="100px"
        class="register-form"
      >
        <el-divider content-position="left">基本信息</el-divider>
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="姓名" prop="name">
          <el-input
            v-model="registerForm.name"
            placeholder="请输入姓名"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="性别" prop="gender">
          <el-select v-model="registerForm.gender" placeholder="请选择性别">
            <el-option label="男" value="MALE" />
            <el-option label="女" value="FEMALE" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>

        <el-divider content-position="left">学校信息</el-divider>
        <el-form-item label="学号" prop="studentId">
          <el-input
            v-model="registerForm.studentId"
            placeholder="请输入在校学号"
            :prefix-icon="User"
            clearable
            @keyup="handleStudentIdInput"
          />
          <div class="form-tip">根据学号自动计算毕业年份</div>
        </el-form-item>
        
        <el-form-item label="专业" prop="major">
          <el-input
            v-model="registerForm.major"
            placeholder="请输入专业"
            :prefix-icon="Briefcase"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="毕业年份" prop="graduationYear">
          <el-select
            v-model="registerForm.graduationYear"
            placeholder="请选择毕业年份"
            style="width: 100%"
            @change="handleYearChange"
          >
            <el-option
              v-for="year in graduationYears"
              :key="year"
              :label="year"
              :value="year"
            />
          </el-select>
        </el-form-item>

        <el-divider content-position="left">联系方式（选填）</el-divider>
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱"
            :prefix-icon="Message"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号"
            :prefix-icon="Phone"
            clearable
            @keyup="handlePhoneInput"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="loading" style="width: 100%">
            注册
          </el-button>
          <div class="login-link">
            已有账号？
            <el-link type="primary" @click="goToLogin">立即登录</el-link>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message, Phone, Briefcase } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  name: '',
  gender: '',
  password: '',
  confirmPassword: '',
  studentId: '',
  major: '',
  graduationYear: '',
  email: '',
  phone: ''
})

// 标记是否手动选择过毕业年份
const isYearManuallySelected = ref(false)

// 根据学号计算毕业年份
const graduationYears = computed(() => {
  const currentYear = new Date().getFullYear()
  const years = []
  for (let i = 2000; i <= currentYear + 4; i++) {
    years.push(i)
  }
  return years
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  studentId: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { pattern: /^\d+$/, message: '学号必须是数字', trigger: 'blur' }
  ],
  major: [
    { required: true, message: '请输入专业', trigger: 'blur' }
  ],
  graduationYear: [
    { required: true, message: '请选择毕业年份', trigger: 'change' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 处理学号输入
const handleStudentIdInput = (e) => {
  registerForm.studentId = e.target.value.replace(/\D/g, '')
  // 每次学号变化都更新毕业年份
  if (registerForm.studentId.length >= 4) {
    const year = parseInt(registerForm.studentId.substring(0, 4)) + 4
    if (graduationYears.value.includes(year)) {
      registerForm.graduationYear = year
    }
  } else {
    registerForm.graduationYear = ''
  }
}

// 处理手机号输入
const handlePhoneInput = (e) => {
  registerForm.phone = e.target.value.replace(/\D/g, '')
}

// 处理毕业年份选择
const handleYearChange = (value) => {
  registerForm.graduationYear = value
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    await registerFormRef.value.validate()
    loading.value = true
    
    // 准备注册数据
    const registerData = {
      username: registerForm.username,
      name: registerForm.name,
      gender: registerForm.gender,
      password: registerForm.password,
      studentId: registerForm.studentId,
      major: registerForm.major,
      graduationYear: registerForm.graduationYear,
      email: registerForm.email || null,
      phone: registerForm.phone || null,
      role: 'ALUMNI'  // 添加角色字段
    }
    
    console.log('注册数据:', registerData)
    
    const response = await request({
      url: '/api/users/register',
      method: 'post',
      data: registerData
    })
    
    console.log('注册响应:', response)
    
    if (response && !response.error) {  // 成功响应没有error字段
      ElMessage.success('注册成功')
      router.push('/login')
    } else {
      ElMessage.error(response.message || response.error || '注册失败')
    }
  } catch (error) {
    console.error('注册失败:', error)
    if (error.response) {
      const { data } = error.response
      if (data && data.message) {
        ElMessage.error(data.message)
      } else if (data && data.error) {
        ElMessage.error(data.error)
      } else {
        ElMessage.error('注册失败，请检查输入信息')
      }
    } else if (error.request) {
      ElMessage.error('无法连接到服务器，请检查网络连接')
    } else {
      ElMessage.error('注册失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: var(--background-color);
  padding: 20px;
}

.system-title {
  margin-bottom: 40px;
  text-align: center;
}

.system-title h1 {
  font-size: 36px;
  color: #409EFF;
  margin: 0;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.register-card {
  width: 600px;
}

.card-header {
  text-align: center;
}

.register-form {
  margin-top: 20px;
}

.login-link {
  text-align: center;
  margin-top: 10px;
}

:deep(.el-divider__text) {
  font-size: 16px;
  font-weight: bold;
  color: var(--text-color);
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style> 