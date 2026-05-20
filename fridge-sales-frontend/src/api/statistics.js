import request from '@/utils/request'

export function getSalesStatistics(params) {
  return request({
    url: '/statistics/sales',
    method: 'get',
    params
  })
}

export function getProductStatistics(params) {
  return request({
    url: '/statistics/product',
    method: 'get',
    params
  })
}

export function getUserStatistics(params) {
  return request({
    url: '/statistics/user',
    method: 'get',
    params
  })
}

export function getOrderStatistics(params) {
  return request({
    url: '/statistics/order',
    method: 'get',
    params
  })
}

export function getDashboardData() {
  return request({
    url: '/admin/statistics/overview',
    method: 'get'
  })
}

export function getSalesTrend(params) {
  return request({
    url: '/admin/statistics/trend',
    method: 'get',
    params
  })
}

export function getCategorySales(params) {
  return request({
    url: '/admin/statistics/category',
    method: 'get',
    params
  })
}
