package com.fridge.sales.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 支付请求DTO
 */
@Data
public class PaymentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 支付方式（1-支付宝，2-微信）
     */
    @NotNull(message = "请选择支付方式")
    private Integer paymentType;
}
