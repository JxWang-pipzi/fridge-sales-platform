import request from '@/utils/request'

export function getReviewList(productId, page = 1, pageSize = 10) {
  return request({
    url: `/review/list/${productId}`,
    method: 'get',
    params: { page, size: pageSize }
  })
}

export function getReviewStats(productId) {
  return request({
    url: `/review/stats/${productId}`,
    method: 'get'
  })
}

export function checkHasPurchased(productId) {
  return request({
    url: `/review/hasPurchased/${productId}`,
    method: 'get'
  })
}

export function addReview(data) {
  return request({
    url: '/review',
    method: 'post',
    data
  })
}

export function deleteReview(id) {
  return request({
    url: `/review/${id}`,
    method: 'delete'
  })
}

export function getAdminReviewList(page = 1, pageSize = 10) {
  return request({
    url: '/admin/review/list',
    method: 'get',
    params: { page, pageSize }
  })
}

export function updateReviewStatus(id, status) {
  return request({
    url: '/admin/review/status',
    method: 'put',
    data: { id, status }
  })
}

export function deleteReviewByAdmin(id) {
  return request({
    url: `/admin/review/${id}`,
    method: 'delete'
  })
}
