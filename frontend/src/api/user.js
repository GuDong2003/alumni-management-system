import request from '@/utils/request'

// 根据ID获取用户信息
export function getUserById(id) {
  return request({
    url: `/api/users/${id}`,
    method: 'get'
  })
}

// 根据用户名获取用户信息
export function getUserByUsername(username) {
  return request({
    url: '/api/users/by-username',
    method: 'get',
    params: { username }
  })
}

// 获取当前登录用户信息
export function getCurrentUser() {
  return request({
    url: '/api/users/current',
    method: 'get'
  })
}

// 获取用户列表（分页）
export function getUserList(page, size, searchKey = '') {
  return request({
    url: '/api/users',
    method: 'get',
    params: { page, size, searchKey }
  })
} 