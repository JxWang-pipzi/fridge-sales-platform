package com.fridge.sales.controller;

import com.fridge.sales.common.PageResult;
import com.fridge.sales.common.Result;
import com.fridge.sales.dto.OrderCreateDTO;
import com.fridge.sales.dto.OrderQueryDTO;
import com.fridge.sales.dto.OrderVO;
import com.fridge.sales.dto.PaymentDTO;
import com.fridge.sales.service.OrdersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    /**
     * 创建订单
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody OrderCreateDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Long orderId = ordersService.create(userId, dto);
        return Result.success(orderId);
    }

    /**
     * 查询用户订单列表
     */
    @GetMapping("/list")
    public Result<PageResult<OrderVO>> list(OrderQueryDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        PageResult<OrderVO> result = ordersService.list(userId, dto);
        return Result.success(result);
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/{id}")
    public Result<OrderVO> getById(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        OrderVO order = ordersService.getById(id, userId);
        return Result.success(order);
    }

    /**
     * 取消订单
     */
    @PutMapping("/cancel/{id}")
    public Result<Void> cancel(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ordersService.cancel(id, userId);
        return Result.success();
    }

    /**
     * 支付订单
     */
    @PostMapping("/pay/{id}")
    public Result<Void> pay(@PathVariable Long id, @Valid @RequestBody PaymentDTO paymentDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ordersService.pay(id, userId, paymentDTO.getPaymentType());
        return Result.success();
    }

    /**
     * 确认收货
     */
    @PutMapping("/confirm/{id}")
    public Result<Void> confirmReceive(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ordersService.confirmReceive(id, userId);
        return Result.success();
    }

    /**
     * 获取用户订单统计
     */
    @GetMapping("/stats")
    public Result<java.util.Map<String, Long>> getOrderStats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        java.util.Map<String, Long> stats = ordersService.getOrderStats(userId);
        return Result.success(stats);
    }

    /**
     * 删除订单
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ordersService.delete(id, userId);
        return Result.success();
    }
}
