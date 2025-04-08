import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/auth/Login.vue'
import Register from '@/views/auth/Register.vue'
import AdminHome from '@/views/admin/AdminHome.vue'
import Dashboard from '@/views/admin/Dashboard.vue'
import UserManagement from '@/views/admin/UserManagement.vue'
import AlumniHome from '@/views/alumni/AlumniHome.vue'
import AlumniDashboard from '@/views/alumni/Dashboard.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/admin',
    component: AdminHome,
    meta: { requiresAuth: true, roles: ['ADMIN', 'SUPER_ADMIN'] },
    children: [
      {
        path: '',
        redirect: '/admin/Dashboard'
      },
      {
        path: 'Dashboard',
        component: Dashboard
      },
      {
        path: 'AnnouncementManagement',
        component: () => import('@/views/admin/AnnouncementManagement.vue')
      },
      {
        path: 'UserManagement',
        component: UserManagement
      },
      {
        path: 'ActivityManagement',
        component: () => import('@/views/admin/ActivityManagement.vue')
      },
      {
        path: 'DonationManagement',
        component: () => import('@/views/admin/DonationManagement.vue')
      },
      {
        path: 'FeedbackManagement',
        component: () => import('@/views/admin/FeedbackManagement.vue')
      }
    ]
  },
  {
    path: '/alumni',
    component: AlumniHome,
    meta: { requiresAuth: true, roles: ['ALUMNI'] },
    children: [
      {
        path: '',
        redirect: '/alumni/Dashboard'
      },
      {
        path: 'Dashboard',
        component: AlumniDashboard
      },
      {
        path: 'Profile',
        component: () => import('@/views/alumni/Profile.vue')
      },
      {
        path: 'Activities',
        component: () => import('@/views/alumni/Activities.vue')
      },
      {
        path: 'Donations',
        component: () => import('@/views/alumni/Donations.vue')
      },
      {
        path: 'Feedback',
        component: () => import('@/views/alumni/Feedback.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userRole = localStorage.getItem('userRole')
  
  console.log('路由守卫 - token:', token)
  console.log('路由守卫 - userRole:', userRole)
  console.log('目标路由:', to.path)

  // 如果访问登录或注册页面，直接放行
  if (to.path === '/login' || to.path === '/register') {
    next()
    return
  }

  // 如果没有token，重定向到登录页
  if (!token) {
    console.log('未找到token，重定向到登录页')
    next('/login')
    return
  }

  // 如果访问管理后台，检查用户角色
  if (to.path.startsWith('/admin')) {
    console.log('访问管理后台，检查用户角色')
    if (userRole === 'SUPER_ADMIN' || userRole === 'ADMIN') {
      console.log('用户角色符合要求，允许访问')
      next()
    } else {
      console.log('用户角色不符合要求，重定向到首页')
      next('/')
    }
    return
  }

  // 如果访问校友页面，检查用户角色
  if (to.path.startsWith('/alumni')) {
    console.log('访问校友页面，检查用户角色')
    if (userRole === 'ALUMNI') {
      console.log('用户角色符合要求，允许访问')
      next()
    } else {
      console.log('用户角色不符合要求，重定向到首页')
      next('/')
    }
    return
  }

  next()
})

export default router 