package com.fridge.sales.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品查询条件DTO
 */
@Data
public class ProductQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关键词（商品名称/品牌/型号）
     */
    private String keyword;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String category;

    /**
     * 最低价格
     */
    private BigDecimal minPrice;

    /**
     * 最高价格
     */
    private BigDecimal maxPrice;

    /**
     * 品牌
     */
    private String brand;

    /**
     * SKU编码
     */
    private String sku;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 排序方式
     */
    private String sortBy;

    /**
     * 当前页码
     */
    private Long current = 1L;

    /**
     * 每页大小
     */
    private Long size = 10L;

    /**
     * 页码（兼容前端）
     */
    private Integer page;

    /**
     * 每页大小（兼容前端）
     */
    private Integer pageSize;
}
