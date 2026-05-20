package com.fridge.sales.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志实体类
 */
@Data
@TableName("sys_oper_log")
public class SysOperLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("title")
    private String title;

    @TableField("business_type")
    private Integer businessType;

    @TableField("method")
    private String method;

    @TableField("request_method")
    private String requestMethod;

    @TableField("operator_type")
    private Integer operatorType;

    @TableField("oper_name")
    private String operName;

    @TableField("oper_url")
    private String operUrl;

    @TableField("oper_ip")
    private String operIp;

    @TableField("oper_location")
    private String operLocation;

    @TableField("oper_param")
    private String operParam;

    @TableField("json_result")
    private String jsonResult;

    @TableField("status")
    private Integer status;

    @TableField("error_msg")
    private String errorMsg;

    @TableField("oper_time")
    private LocalDateTime operTime;

    @TableField("cost_time")
    private Long costTime;
}
