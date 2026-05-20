package com.fridge.sales.controller;

import com.fridge.sales.common.PageResult;
import com.fridge.sales.common.Result;
import com.fridge.sales.dto.ReviewDTO;
import com.fridge.sales.entity.Review;
import com.fridge.sales.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 评价控制器
 */
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * 获取商品评价列表
     */
    @GetMapping("/review/list/{productId}")
    public Result<PageResult<Map<String, Object>>> list(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(reviewService.list(productId, page, size));
    }

    /**
     * 获取商品评价统计
     */
    @GetMapping("/review/stats/{productId}")
    public Result<Map<String, Object>> getStats(@PathVariable Long productId) {
        return Result.success(reviewService.getStats(productId));
    }

    /**
     * 检查是否购买过商品
     */
    @GetMapping("/review/hasPurchased/{productId}")
    public Result<Boolean> hasPurchased(HttpServletRequest request, @PathVariable Long productId) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.success(false);
        }
        return Result.success(reviewService.hasPurchased(userId, productId));
    }

    /**
     * 添加评价
     */
    @PostMapping("/review")
    public Result<Review> add(HttpServletRequest request, @Valid @RequestBody ReviewDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        try {
            Review review = reviewService.add(userId, dto);
            return Result.success(review);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除评价
     */
    @DeleteMapping("/review/{id}")
    public Result<Void> delete(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        try {
            reviewService.delete(userId, id);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
