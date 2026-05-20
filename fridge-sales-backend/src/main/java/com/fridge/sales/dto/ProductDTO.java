package com.fridge.sales.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品添加/修改请求DTO
 */
@Data
public class ProductDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID（修改时必填）
     */
    private Long id;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    private String name;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 型号
     */
    private String model;

    /**
     * SKU编码
     */
    private String sku;

    /**
     * 销售价格
     */
    @NotNull(message = "销售价格不能为空")
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 库存
     */
    @NotNull(message = "库存不能为空")
    private Integer stock;

    /**
     * 主图
     */
    private String image;

    /**
     * 商品图片列表(JSON格式)
     */
    private String images;

    /**
     * 容量（升）
     */
    private Integer capacity;

    /**
     * 能效等级
     */
    private String energyLevel;

    /**
     * 颜色
     */
    private String color;

    /**
     * 尺寸
     */
    private String size;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 状态：0-下架，1-上架
     */
    private Integer status;
}
