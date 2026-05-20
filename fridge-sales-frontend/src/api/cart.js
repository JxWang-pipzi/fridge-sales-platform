import request from '@/utils/request'

export function getCartList() {
  return request({
    url: '/cart/list',
    method: 'get'
  })
}

export function addToCart(data) {
  return request({
    url: '/cart',
    method: 'post',
    data
  })
}

export function updateCartItem(data) {
  return request({
    url: '/cart',
    method: 'put',
    data
  })
}

export function removeFromCart(id) {
  return request({
    url: `/cart/${id}`,
    method: 'delete'
  })
}

export function clearCart() {
  return request({
    url: '/cart/clear',
    method: 'delete'
  })
}

export function getCartCount() {
  return request({
    url: '/cart/count',
    method: 'get'
  })
}
