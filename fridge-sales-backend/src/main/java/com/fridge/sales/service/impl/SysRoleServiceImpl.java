package com.fridge.sales.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fridge.sales.entity.SysRole;
import com.fridge.sales.mapper.SysRoleMapper;
import com.fridge.sales.service.SysRoleService;
import org.springframework.stereotype.Service;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
}
