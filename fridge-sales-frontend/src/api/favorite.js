import request from '@/utils/request'

export function getFavoriteList() {
  return request({
    url: '/favorite/list',
    method: 'get'
  })
}

export function addFavorite(productId) {
  return request({
    url: `/favorite/${productId}`,
    method: 'post'
  })
}

export function removeFavorite(productId) {
  return request({
    url: `/favorite/${productId}`,
    method: 'delete'
  })
}
