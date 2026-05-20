package com.fridge.sales.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 分类销售统计视图对象
 */
@Data
public class CategorySalesVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 销售额
     */
    private BigDecimal sales;

    /**
     * 销售数量
     */
    private Integer count;
}
