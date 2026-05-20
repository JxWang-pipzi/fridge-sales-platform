import request from '@/utils/request'

export function getRoles() {
  // Mock data if backend not ready, but try real request first
  return request({
    url: '/admin/role/list',
    method: 'get'
  })
}

export function getPermissions() {
  return request({
    url: '/admin/permission/list',
    method: 'get'
  })
}

export function getRolePermissions(roleId) {
  return request({
    url: `/admin/role/permission/${roleId}`,
    method: 'get'
  })
}

export function updateRolePermissions(roleId, permissionIds) {
  return request({
    url: '/admin/role/permission',
    method: 'put',
    data: { roleId, permissionIds }
  })
}
