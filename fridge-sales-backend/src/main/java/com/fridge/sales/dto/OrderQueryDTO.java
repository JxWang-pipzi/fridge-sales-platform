package com.fridge.sales.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单查询DTO
 */
@Data
public class OrderQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单状态：0-待付款，1-待发货，2-待收货，3-已完成，4-已取消
     */
    private Integer status;

    /**
     * 当前页
     */
    private Long current = 1L;

    /**
     * 每页大小
     */
    private Long size = 10L;
}
