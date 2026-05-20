package com.fridge.sales.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fridge.sales.common.Result;
import com.fridge.sales.entity.SysPermission;
import com.fridge.sales.entity.SysRole;
import com.fridge.sales.entity.SysRolePermission;
import com.fridge.sales.service.SysPermissionService;
import com.fridge.sales.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysPermissionService sysPermissionService;

    @GetMapping("/admin/role/list")
    public Result<List<SysRole>> getRoleList() {
        List<SysRole> roles = sysRoleService.list(
            new LambdaQueryWrapper<SysRole>().eq(SysRole::getDelFlag, 0)
        );
        return Result.success(roles);
    }

    @GetMapping("/admin/permission/list")
    public Result<List<SysPermission>> getPermissionList() {
        List<SysPermission> permissions = sysPermissionService.list();
        return Result.success(permissions);
    }

    @GetMapping("/admin/role/permission/{roleId}")
    public Result<List<Long>> getRolePermissions(@PathVariable Long roleId) {
        List<SysRolePermission> rolePermissions = sysPermissionService.getRolePermissions(roleId);
        List<Long> permissionIds = rolePermissions.stream()
            .map(SysRolePermission::getPermissionId)
            .collect(Collectors.toList());
        return Result.success(permissionIds);
    }

    @PutMapping("/admin/role/permission")
    public Result<Void> updateRolePermissions(@RequestBody java.util.Map<String, Object> params) {
        Long roleId = Long.valueOf(params.get("roleId").toString());
        @SuppressWarnings("unchecked")
        List<Integer> permissionIds = (List<Integer>) params.get("permissionIds");
        List<Long> pIds = permissionIds.stream()
            .map(Integer::longValue)
            .collect(Collectors.toList());
        sysPermissionService.updateRolePermissions(roleId, pIds);
        return Result.success();
    }
}
