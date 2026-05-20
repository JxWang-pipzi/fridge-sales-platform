import request from '@/utils/request'

export function getBrandList() {
  return request({ url: '/brand/list', method: 'get' })
}

export function getBrandById(id) {
  return request({ url: `/brand/${id}`, method: 'get' })
}

export function addBrand(data) {
  return request({ url: '/admin/brand', method: 'post', data })
}

export function updateBrand(data) {
  return request({ url: '/admin/brand', method: 'put', data })
}

export function deleteBrand(id) {
  return request({ url: `/admin/brand/${id}`, method: 'delete' })
}
