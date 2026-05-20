package com.fridge.sales.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 销售趋势视图对象
 */
@Data
public class SalesTrendVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日期
     */
    private String date;

    /**
     * 销售额
     */
    private BigDecimal sales;

    /**
     * 订单数
     */
    private Long orders;
}
