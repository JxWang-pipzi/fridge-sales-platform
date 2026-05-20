import request from '@/utils/request'

export function getOverview() {
  return request({
    url: '/admin/statistics/overview',
    method: 'get'
  })
}

export function getStatisticsOverview() {
  return request({
    url: '/admin/statistics/overview',
    method: 'get'
  })
}

export function getSalesTrend(type = 'day') {
  return request({
    url: '/admin/statistics/trend',
    method: 'get',
    params: { type }
  })
}

export function getCategorySales() {
  return request({
    url: '/admin/statistics/category',
    method: 'get'
  })
}

export function getHotProducts(limit = 10) {
  return request({
    url: '/admin/statistics/hot',
    method: 'get',
    params: { limit }
  })
}

export function getAdminProductList(params) {
  return request({
    url: '/product/list',
    method: 'get',
    params
  })
}

export function addProduct(data) {
  return request({
    url: '/admin/product',
    method: 'post',
    data
  })
}

export function updateProduct(data) {
  return request({
    url: '/admin/product',
    method: 'put',
    data
  })
}

export function deleteProduct(id) {
  return request({
    url: `/admin/product/${id}`,
    method: 'delete'
  })
}

export function updateProductStatus(id, status) {
  return request({
    url: '/admin/product/status',
    method: 'put',
    data: { id, status }
  })
}

export function getAdminOrderList(params) {
  return request({
    url: '/admin/order/list',
    method: 'get',
    params
  })
}

export function updateOrderStatus(id, status) {
  return request({
    url: `/admin/order/status/${id}`,
    method: 'put',
    data: { status }
  })
}

export function shipOrder(id, data) {
  return request({
    url: `/admin/order/deliver/${id}`,
    method: 'put',
    data
  })
}

export function getAdminUserList(params) {
  return request({
    url: '/admin/user/list',
    method: 'get',
    params
  })
}

export function getUserDetail(id) {
  return request({
    url: `/admin/user/${id}`,
    method: 'get'
  })
}

export function updateAdminUser(id, data) {
  return request({
    url: `/admin/user/${id}`,
    method: 'put',
    data
  })
}

export function updateUserStatus(id, status) {
  return request({
    url: '/admin/user/status',
    method: 'put',
    data: { id, status }
  })
}

export function resetUserPassword(id) {
  return request({
    url: `/admin/user/reset-password/${id}`,
    method: 'put'
  })
}
