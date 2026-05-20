package com.fridge.sales.controller;

import com.fridge.sales.common.Result;
import com.fridge.sales.dto.FavoriteVO;
import com.fridge.sales.service.FavoriteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收藏控制器
 */
@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 添加收藏
     *
     * @param productId 商品ID
     * @param request   HTTP请求
     * @return 操作结果
     */
    @PostMapping("/{productId}")
    public Result<String> add(@PathVariable Long productId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        boolean success = favoriteService.add(userId, productId);
        if (success) {
            return Result.success("收藏成功");
        } else {
            return Result.error("已收藏该商品");
        }
    }

    /**
     * 取消收藏
     *
     * @param productId 商品ID
     * @param request   HTTP请求
     * @return 操作结果
     */
    @DeleteMapping("/{productId}")
    public Result<String> remove(@PathVariable Long productId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        boolean success = favoriteService.remove(userId, productId);
        if (success) {
            return Result.success("取消收藏成功");
        } else {
            return Result.error("取消收藏失败");
        }
    }

    /**
     * 查询收藏列表
     *
     * @param request HTTP请求
     * @return 收藏列表
     */
    @GetMapping("/list")
    public Result<List<FavoriteVO>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<FavoriteVO> list = favoriteService.list(userId);
        return Result.success(list);
    }

    /**
     * 检查是否已收藏
     *
     * @param productId 商品ID
     * @param request   HTTP请求
     * @return 是否已收藏
     */
    @GetMapping("/check/{productId}")
    public Result<Map<String, Boolean>> check(@PathVariable Long productId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        boolean isFavorite = favoriteService.isFavorite(userId, productId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("isFavorite", isFavorite);
        return Result.success(result);
    }
}
