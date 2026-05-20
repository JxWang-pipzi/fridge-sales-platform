package com.fridge.sales.controller;

import com.fridge.sales.common.Result;
import com.fridge.sales.dto.AddressDTO;
import com.fridge.sales.entity.Address;
import com.fridge.sales.service.AddressService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址控制器
 */
@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 查询用户地址列表
     */
    @GetMapping("/list")
    public Result<List<Address>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Address> addresses = addressService.list(userId);
        return Result.success(addresses);
    }

    /**
     * 添加地址
     */
    @PostMapping
    public Result<Long> add(@Valid @RequestBody AddressDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Long addressId = addressService.add(userId, dto);
        return Result.success(addressId);
    }

    /**
     * 更新地址
     */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody AddressDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        addressService.update(userId, dto);
        return Result.success();
    }

    /**
     * 删除地址
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        addressService.delete(id, userId);
        return Result.success();
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/default/{id}")
    public Result<Void> setDefault(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        addressService.setDefault(id, userId);
        return Result.success();
    }
}
