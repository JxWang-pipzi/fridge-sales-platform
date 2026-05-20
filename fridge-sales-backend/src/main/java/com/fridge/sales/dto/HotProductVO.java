package com.fridge.sales.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 热门商品视图对象
 */
@Data
public class HotProductVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片
     */
    private String productImage;

    /**
     * 销量
     */
    private Integer sales;

    /**
     * 销售额
     */
    private BigDecimal amount;
}
