package com.fridge.sales.controller;

import com.fridge.sales.common.PageResult;
import com.fridge.sales.common.Result;
import com.fridge.sales.service.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员评价控制器
 */
@RestController
@RequestMapping("/admin/review")
public class AdminReviewController {

    private final ReviewService reviewService;

    public AdminReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * 管理员获取评价列表
     */
    @GetMapping("/list")
    public Result<PageResult<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(reviewService.adminList(page, pageSize));
    }

    /**
     * 管理员更新评价状态
     */
    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestBody Map<String, Integer> params) {
        Long id = params.get("id").longValue();
        Integer status = params.get("status");
        reviewService.updateStatus(id, status);
        return Result.success();
    }

    /**
     * 管理员删除评价
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        reviewService.adminDelete(id);
        return Result.success();
    }
}
