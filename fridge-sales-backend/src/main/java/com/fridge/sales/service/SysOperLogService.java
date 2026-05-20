package com.fridge.sales.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fridge.sales.entity.SysOperLog;

/**
 * 操作日志Service接口
 */
public interface SysOperLogService extends IService<SysOperLog> {

    void insertOperLog(SysOperLog operLog);
}
