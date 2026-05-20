package com.fridge.sales.controller;

import com.fridge.sales.annotation.Log;
import com.fridge.sales.common.PageResult;
import com.fridge.sales.common.Result;
import com.fridge.sales.dto.OrderQueryDTO;
import com.fridge.sales.dto.OrderVO;
import com.fridge.sales.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.fridge.sales.annotation.Log.BusinessType.*;

/**
 * 管理员订单控制器
 */
@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Autowired
    private OrdersService ordersService;

    @GetMapping("/list")
    public Result<PageResult<OrderVO>> list(OrderQueryDTO dto) {
        PageResult<OrderVO> result = ordersService.adminList(dto);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<OrderVO> getById(@PathVariable Long id) {
        OrderVO order = ordersService.adminGetById(id);
        return Result.success(order);
    }

    @Log(title = "订单管理", businessType = UPDATE)
    @PutMapping("/deliver/{id}")
    public Result<Void> deliver(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String expressCompany = params.get("expressCompany");
        String expressNo = params.get("expressNo");

        // 参数校验
        if (expressCompany == null || expressCompany.trim().isEmpty()) {
            return Result.error("物流公司不能为空");
        }
        if (expressNo == null || expressNo.trim().isEmpty()) {
            return Result.error("物流单号不能为空");
        }

        ordersService.deliver(id, expressCompany, expressNo);
        return Result.success();
    }

    @Log(title = "订单管理", businessType = UPDATE)
    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Integer status = Integer.valueOf(params.get("status").toString());
        ordersService.updateStatus(id, status);
        return Result.success();
    }
}
