import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/auth/Login.vue'
import Register from '@/views/auth/Register.vue'
import AdminHome from '@/views/admin/AdminHome.vue'
import Dashboard from '@/views/admin/Dashboard.vue'
import UserManagement from '@/views/admin/UserManagement.vue'

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

  // 如果路由需要认证
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 检查是否已登录
    if (!token) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }

    // 检查角色权限
    if (to.matched.some(record => record.meta.roles)) {
      const requiredRoles = to.matched.find(record => record.meta.roles).meta.roles
      if (!requiredRoles.includes(userRole)) {
        next({ path: '/login' })
        return
      }
    }
  }

  next()
})

export default router 