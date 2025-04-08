<template>
  <div class="login-container">
    <div class="system-title">
      <h1>校友信息管理系统</h1>
    </div>
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>用户登录</h2>
        </div>
      </template>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="rules"
        label-width="80px"
        class="login-form"
        @submit.prevent
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            clearable
            @keyup.enter="focusPasswordInput"
          />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input
            ref="passwordInputRef"
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            clearable
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">
            登录
          </el-button>
          <div class="register-link">
            还没有账号？
            <el-link type="primary" @click="goToRegister">立即注册</el-link>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

// 重写request方法，拦截所有错误，确保不会显示"没有权限访问"
const safeRequest = async (options) => {
  try {
    return await request(options);
  } catch (error) {
    // 如果是登录相关的请求且失败了，不要抛出原始错误
    if (options.url.includes('/auth/login')) {
      // 创建一个自定义错误对象，移除所有权限相关信息
      const customError = new Error('密码不正确');
      customError.isPasswordError = true;
      throw customError;
    }
    // 其他请求继续抛出原始错误
    throw error;
  }
}

const router = useRouter()
const loginFormRef = ref(null)
const passwordInputRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

// 用户名输入框按回车键时，聚焦到密码框
const focusPasswordInput = () => {
  if (loginForm.username.trim()) {
    passwordInputRef.value.focus()
  }
}

const handleLogin = async () => {
  // 检查用户名和密码是否为空
  if (!loginForm.username.trim()) {
    ElMessage.warning('请输入用户名')
    return
  }
  
  if (!loginForm.password.trim()) {
    ElMessage.warning('请输入密码')
    return
  }
  
  // 验证表单
  if (loginFormRef.value) {
    const valid = await loginFormRef.value.validate().catch(() => false)
    if (!valid) {
      return
    }
  }
  
  try {
    loading.value = true
    
    // 1. 先检查用户名是否存在
    try {
      const checkResponse = await safeRequest({
        url: `/api/users/check/username?username=${encodeURIComponent(loginForm.username)}`,
        method: 'get'
      })
      
      // 该API返回true表示用户名已存在，返回false表示用户名不存在
      const userExists = !!checkResponse
      
      if (!userExists) {
        // 用户不存在，直接提示
        ElMessage.error('账号不存在')
        loading.value = false
        return
      }
      
      // 2. 用户存在，进行密码验证
      try {
        const loginResponse = await safeRequest({
          url: '/api/auth/login',
          method: 'post',
          data: {
            username: loginForm.username,
            password: loginForm.password
          }
        })
        
        console.log('登录响应:', loginResponse)
        
        // 登录成功，保存用户信息
        localStorage.setItem('token', loginResponse.token)
        localStorage.setItem('userRole', loginResponse.role)
        localStorage.setItem('userId', loginResponse.id)
        localStorage.setItem('username', loginResponse.username)
        
        ElMessage.success('登录成功')
        
        // 根据角色跳转
        if (loginResponse.role === 'SUPER_ADMIN' || loginResponse.role === 'ADMIN') {
          router.push({ path: '/admin' })
        } else if (loginResponse.role === 'ALUMNI') {
          router.push({ path: '/alumni' })
        }
      } catch (loginError) {
        // 无论什么错误，只显示"密码不正确"
        console.error('密码验证失败:', loginError)
        ElMessage.error('密码不正确')
      }
    } catch (checkError) {
      console.error('检查用户名失败:', checkError)
      // 如果检查用户名的API失败，也不显示原始错误，保持一致的用户体验
      if (loginForm.username) {
        ElMessage.error('账号不存在')
      } else {
        ElMessage.error('登录失败，请稍后重试')
      }
    }
  } catch (error) {
    console.error('登录流程失败:', error)
    // 统一显示为登录失败，不透露具体原因
    ElMessage.error('登录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: var(--background-color);
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

.login-card {
  width: 400px;
}

.card-header {
  text-align: center;
}

.login-form {
  margin-top: 20px;
}

.register-link {
  text-align: center;
  margin-top: 10px;
}
</style> 