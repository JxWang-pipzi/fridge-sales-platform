package com.fridge.sales.controller;

import com.fridge.sales.common.Result;
import com.fridge.sales.dto.CategorySalesVO;
import com.fridge.sales.dto.HotProductVO;
import com.fridge.sales.dto.SalesTrendVO;
import com.fridge.sales.dto.StatisticsVO;
import com.fridge.sales.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 统计控制器（管理员）
 */
@RestController
@RequestMapping("/admin/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取数据概览
     *
     * @return 统计数据概览
     */
    @GetMapping("/overview")
    public Result<StatisticsVO> getOverview() {
        StatisticsVO overview = statisticsService.getOverview();
        return Result.success(overview);
    }

    /**
     * 获取销售趋势
     *
     * @param type 类型：day-按天，week-按周，month-按月
     * @return 销售趋势列表
     */
    @GetMapping("/trend")
    public Result<List<SalesTrendVO>> getSalesTrend(@RequestParam(defaultValue = "day") String type) {
        List<SalesTrendVO> trend = statisticsService.getSalesTrend(type);
        return Result.success(trend);
    }

    /**
     * 获取分类销售统计
     *
     * @return 分类销售统计列表
     */
    @GetMapping("/category")
    public Result<List<CategorySalesVO>> getCategorySales() {
        List<CategorySalesVO> categorySales = statisticsService.getCategorySales();
        return Result.success(categorySales);
    }

    /**
     * 获取热门商品排行
     *
     * @param limit 限制数量，默认10
     * @return 热门商品列表
     */
    @GetMapping("/hot")
    public Result<List<HotProductVO>> getHotProducts(@RequestParam(defaultValue = "10") Integer limit) {
        List<HotProductVO> hotProducts = statisticsService.getHotProducts(limit);
        return Result.success(hotProducts);
    }
}
