import { defineStore } from 'pinia'
import request from '@/utils/request'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}')
  }),
  
  getters: {
    isLoggedIn: (state) => !!state.token,
    userRole: (state) => state.userInfo.role || 'user'
  },
  
  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },
    
    setUserInfo(userInfo) {
      console.log('设置用户信息:', userInfo)
      
      // 确保userInfo是一个对象
      if (!userInfo || typeof userInfo !== 'object') {
        console.error('无效的用户信息:', userInfo)
        userInfo = {}
      }
      
      // 检查用户ID
      if (!userInfo.id) {
        console.warn('用户信息中缺少ID字段:', userInfo)
        
        // 尝试从其他可能的字段获取ID
        if (userInfo.userId) {
          userInfo.id = userInfo.userId
          console.log('从userId字段获取ID:', userInfo.id)
        } else if (userInfo.user_id) {
          userInfo.id = userInfo.user_id
          console.log('从user_id字段获取ID:', userInfo.id)
        }
      }
      
      this.userInfo = userInfo
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
      console.log('用户信息已保存:', this.userInfo)
    },
    
    async login(credentials) {
      try {
        const response = await request({
          url: '/api/users/login',
          method: 'post',
          data: credentials
        })
        
        if (response.success) {
          this.setToken(response.data.token)
          this.setUserInfo(response.data.user)
          return true
        } else {
          throw new Error(response.message || '登录失败')
        }
      } catch (error) {
        console.error('Login failed:', error)
        throw error
      }
    },
    
    async logout() {
      try {
        await request({
          url: '/api/users/logout',
          method: 'post'
        })
      } catch (error) {
        console.error('Logout failed:', error)
      } finally {
        this.token = ''
        this.userInfo = {}
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
      }
    },
    
    async checkAuth() {
      if (!this.token) return false
      
      try {
        const response = await request({
          url: '/auth/check',
          method: 'get'
        })
        return response.code === 200
      } catch (error) {
        console.error('Auth check failed:', error)
        return false
      }
    }
  }
}) 