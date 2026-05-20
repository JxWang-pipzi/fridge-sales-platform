package com.fridge.sales.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fridge.sales.entity.SysOperLog;
import com.fridge.sales.mapper.SysOperLogMapper;
import com.fridge.sales.service.SysOperLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 操作日志Service实现类
 */
@Service
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements SysOperLogService {

    @Override
    @Async
    public void insertOperLog(SysOperLog operLog) {
        save(operLog);
    }
}
