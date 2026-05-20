package com.fridge.sales.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收藏视图对象
 */
@Data
public class FavoriteVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

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
     * 商品价格
     */
    private BigDecimal productPrice;

    /**
     * 商品描述
     */
    private String productDescription;

    /**
     * 商品品牌
     */
    private String productBrand;

    /**
     * 商品型号
     */
    private String productModel;

    /**
     * 商品库存
     */
    private Integer productStock;

    /**
     * 商品状态
     */
    private Integer productStatus;

    /**
     * 收藏时间
     */
    private LocalDateTime createTime;
}
