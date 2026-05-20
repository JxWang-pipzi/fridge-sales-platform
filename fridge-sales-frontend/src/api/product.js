import request from '@/utils/request'

export function getProductList(params) {
  return request({
    url: '/product/list',
    method: 'get',
    params
  })
}

export function getProductDetail(id) {
  return request({
    url: `/product/${id}`,
    method: 'get'
  })
}

export function getProductCategories() {
  return request({
    url: '/product/categories',
    method: 'get'
  })
}

export function searchProducts(params) {
  return request({
    url: '/product/search',
    method: 'get',
    params
  })
}

export function createProduct(data) {
  return request({
    url: '/admin/product',
    method: 'post',
    data
  })
}

export function updateProduct(id, data) {
  return request({
    url: '/admin/product',
    method: 'put',
    data: { id, ...data }
  })
}

export function deleteProduct(id) {
  return request({
    url: `/admin/product/${id}`,
    method: 'delete'
  })
}

export function batchDeleteProduct(ids) {
  return request({
    url: '/admin/product/batch',
    method: 'delete',
    data: ids
  })
}

export function auditProductPrice(id, price) {
  // Mock API for price audit
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({ code: 200, message: '价格审核通过' })
    }, 500)
  })
}

export function updateProductStatus(id, status) {
  return request({
    url: '/admin/product/status',
    method: 'put',
    data: { id, status }
  })
}

export function uploadProductImage(id, formData) {
  return request({
    url: `/product/${id}/image`,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function getHotProducts(limit = 10) {
  return request({
    url: '/product/hot',
    method: 'get',
    params: { limit }
  })
}

export function getRandomProducts(limit = 3) {
  return request({
    url: '/product/recommend',
    method: 'get',
    params: { limit }
  })
}
