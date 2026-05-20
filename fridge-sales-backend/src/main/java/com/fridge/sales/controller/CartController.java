package com.fridge.sales.controller;

import com.fridge.sales.common.Result;
import com.fridge.sales.dto.CartAddDTO;
import com.fridge.sales.dto.CartUpdateDTO;
import com.fridge.sales.dto.CartVO;
import com.fridge.sales.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 添加商品到购物车
     *
     * @param dto     添加购物车DTO
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping
    public Result<Void> add(@Valid @RequestBody CartAddDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.add(userId, dto);
        return Result.success();
    }

    /**
     * 查询购物车列表
     *
     * @param request HTTP请求
     * @return 购物车列表
     */
    @GetMapping("/list")
    public Result<List<CartVO>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<CartVO> cartList = cartService.list(userId);
        return Result.success(cartList);
    }

    /**
     * 更新购物车商品数量
     *
     * @param dto     更新数量DTO
     * @param request HTTP请求
     * @return 操作结果
     */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody CartUpdateDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.updateQuantity(userId, dto);
        return Result.success();
    }

    /**
     * 删除购物车商品
     *
     * @param id      购物车项ID
     * @param request HTTP请求
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.delete(userId, id);
        return Result.success();
    }

    /**
     * 清空购物车
     *
     * @param request HTTP请求
     * @return 操作结果
     */
    @DeleteMapping("/clear")
    public Result<Void> clear(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.clear(userId);
        return Result.success();
    }

    /**
     * 获取购物车商品数量
     *
     * @param request HTTP请求
     * @return 购物车商品数量
     */
    @GetMapping("/count")
    public Result<Integer> count(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Integer count = cartService.count(userId);
        return Result.success(count);
    }
}
