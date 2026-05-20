import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

export function register(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

export function logout() {
  return request({
    url: '/user/logout',
    method: 'post'
  })
}

export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

export function updateUserInfo(data) {
  return request({
    url: '/user/info',
    method: 'put',
    data
  })
}

export function updatePassword(data) {
  return request({
    url: '/user/password',
    method: 'put',
    data
  })
}

export function getAddressList() {
  return request({
    url: '/address/list',
    method: 'get'
  })
}

export function addAddress(data) {
  return request({
    url: '/address',
    method: 'post',
    data
  })
}

export function updateAddress(data) {
  return request({
    url: '/address',
    method: 'put',
    data
  })
}

export function deleteAddress(id) {
  return request({
    url: `/address/${id}`,
    method: 'delete'
  })
}

export function setDefaultAddress(id) {
  return request({
    url: `/address/default/${id}`,
    method: 'put'
  })
}

export function sendCode(email) {
  return request({
    url: '/user/send-code',
    method: 'post',
    data: { email }
  })
}

export function forgotPassword(data) {
  return request({
    url: '/user/forgot-password',
    method: 'post',
    data
  })
}
