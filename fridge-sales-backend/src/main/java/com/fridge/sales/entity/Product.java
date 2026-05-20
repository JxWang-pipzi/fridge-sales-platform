package com.fridge.sales.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 */
@Data
@TableName("product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品名称
     */
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
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 库存
     */
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
     * 销量
     */
    private Integer sales;

    /**
     * 平均评分（1-5）
     */
    private Integer rating;

    /**
     * 状态：0-下架，1-上架
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
