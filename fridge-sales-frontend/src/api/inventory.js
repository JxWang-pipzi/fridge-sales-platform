import request from '@/utils/request'

export function getInventoryList(params) {
  return request({
    url: '/admin/inventory/list',
    method: 'get',
    params
  })
}

export function updateStock(id, stock) {
  return request({
    url: `/admin/inventory/${id}`,
    method: 'put',
    data: { stock }
  })
}
