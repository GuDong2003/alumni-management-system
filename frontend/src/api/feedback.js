import request from '@/utils/request'

// 获取所有反馈（分页）
export function getAllFeedbacks(page, size, headers = {}) {
  return request({
    url: '/api/feedbacks',
    method: 'get',
    params: { page, size },
    headers
  })
}

// 根据ID获取反馈
export function getFeedbackById(id, headers = {}) {
  return request({
    url: `/api/feedbacks/${id}`,
    method: 'get',
    headers
  })
}

// 根据状态获取反馈
export function getFeedbacksByStatus(status, page, size, headers = {}) {
  return request({
    url: `/api/feedbacks/status/${status}`,
    method: 'get',
    params: { page, size },
    headers
  })
}

// 根据类型获取反馈
export function getFeedbacksByType(type, headers = {}) {
  return request({
    url: `/api/feedbacks/type/${type}`,
    method: 'get',
    headers
  })
}

// 搜索反馈
export function searchFeedbacks(keyword, headers = {}) {
  return request({
    url: '/api/feedbacks/search',
    method: 'get',
    params: { keyword },
    headers
  })
}

// 更新反馈状态
export function updateFeedbackStatus(id, status, headers = {}) {
  return request({
    url: `/api/feedbacks/${id}/status`,
    method: 'put',
    params: { status },
    headers
  })
}

// 回复反馈
export function replyToFeedback(id, reply, status = 'REPLIED', headers = {}) {
  return request({
    url: `/api/feedbacks/${id}/reply`,
    method: 'put',
    params: { reply, status },
    headers
  })
}

// 删除反馈
export function deleteFeedback(id, headers = {}) {
  return request({
    url: `/api/feedbacks/${id}`,
    method: 'delete',
    headers
  })
} 