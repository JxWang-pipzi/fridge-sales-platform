package com.fridge.sales.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fridge.sales.entity.SysPermission;
import com.fridge.sales.entity.SysRolePermission;

import java.util.List;

public interface SysPermissionService extends IService<SysPermission> {
    List<SysRolePermission> getRolePermissions(Long roleId);
    void updateRolePermissions(Long roleId, List<Long> permissionIds);
}
