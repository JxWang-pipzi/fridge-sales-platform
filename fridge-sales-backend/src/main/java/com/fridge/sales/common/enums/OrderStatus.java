package com.fridge.sales.common.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    UNPAID(0, "待付款"),
    UNSHIPPED(1, "待发货"),
    UNRECEIVED(2, "待收货"),
    COMPLETED(3, "已完成"),
    CANCELLED(4, "已取消");

    private final Integer code;
    private final String desc;

    OrderStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OrderStatus getByCode(Integer code) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
