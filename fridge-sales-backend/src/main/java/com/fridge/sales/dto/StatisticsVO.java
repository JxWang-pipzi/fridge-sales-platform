package com.fridge.sales.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 统计数据视图对象
 */
@Data
public class StatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总销售额
     */
    private BigDecimal totalSales;

    /**
     * 总订单数
     */
    private Long totalOrders;

    /**
     * 总用户数
     */
    private Long totalUsers;

    /**
     * 总商品数
     */
    private Long totalProducts;

    /**
     * 今日销售额
     */
    private BigDecimal todaySales;

    /**
     * 今日订单数
     */
    private Long todayOrders;

    /**
     * 今日新增用户
     */
    private Long todayUsers;
}
