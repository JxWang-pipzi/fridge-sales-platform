import request from '@/utils/request'

export function createOrder(data) {
  return request({
    url: '/order',
    method: 'post',
    data
  })
}

export function getOrderList(params) {
  return request({
    url: '/order/list',
    method: 'get',
    params
  })
}

export function getOrderDetail(id) {
  return request({
    url: `/order/${id}`,
    method: 'get'
  })
}

export function cancelOrder(id) {
  return request({
    url: `/order/cancel/${id}`,
    method: 'put'
  })
}

export function payOrder(id, data) {
  return request({
    url: `/order/pay/${id}`,
    method: 'post',
    data
  })
}

export function confirmReceive(id) {
  return request({
    url: `/order/confirm/${id}`,
    method: 'put'
  })
}

export function getAdminOrderList(params) {
  return request({
    url: '/admin/order/list',
    method: 'get',
    params
  })
}

export function getAdminOrderDetail(id) {
  return request({
    url: `/admin/order/${id}`,
    method: 'get'
  })
}

export function updateOrderStatus(id, data) {
  return request({
    url: `/admin/order/status/${id}`,
    method: 'put',
    data
  })
}

export function shipOrder(id, data) {
  return request({
    url: `/admin/order/deliver/${id}`,
    method: 'put',
    data
  })
}

export function generateInvoice(id) {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({ code: 200, message: '发票生成成功', url: `http://mock-invoice-url.com/${id}.pdf` })
    }, 1000)
  })
}

export function getOrderStats() {
  return request({
    url: '/order/stats',
    method: 'get'
  })
}

export function deleteOrder(id) {
  return request({
    url: `/order/${id}`,
    method: 'delete'
  })
}
