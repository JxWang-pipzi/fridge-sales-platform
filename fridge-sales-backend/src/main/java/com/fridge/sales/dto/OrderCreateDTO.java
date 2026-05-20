package com.fridge.sales.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建订单DTO
 */
@Data
public class OrderCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 地址ID
     */
    @NotNull(message = "地址ID不能为空")
    private Long addressId;

    /**
     * 购物车项ID列表
     */
    @NotEmpty(message = "购物车项不能为空")
    private List<Long> cartItemIds;
}
