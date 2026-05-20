package com.fridge.sales.service;

import com.fridge.sales.dto.CategorySalesVO;
import com.fridge.sales.dto.HotProductVO;
import com.fridge.sales.dto.SalesTrendVO;
import com.fridge.sales.dto.StatisticsVO;

import java.util.List;

/**
 * 统计服务接口
 */
public interface StatisticsService {

    /**
     * 获取数据概览
     *
     * @return 统计数据概览
     */
    StatisticsVO getOverview();

    /**
     * 获取销售趋势
     *
     * @param type 类型：day-按天，week-按周，month-按月
     * @return 销售趋势列表
     */
    List<SalesTrendVO> getSalesTrend(String type);

    /**
     * 获取分类销售统计
     *
     * @return 分类销售统计列表
     */
    List<CategorySalesVO> getCategorySales();

    /**
     * 获取热门商品排行
     *
     * @param limit 限制数量，默认10
     * @return 热门商品列表
     */
    List<HotProductVO> getHotProducts(Integer limit);
}
