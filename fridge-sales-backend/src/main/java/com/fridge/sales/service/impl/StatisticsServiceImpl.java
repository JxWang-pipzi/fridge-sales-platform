package com.fridge.sales.service.impl;

import com.fridge.sales.dto.CategorySalesVO;
import com.fridge.sales.dto.HotProductVO;
import com.fridge.sales.dto.SalesTrendVO;
import com.fridge.sales.dto.StatisticsVO;
import com.fridge.sales.mapper.OrdersMapper;
import com.fridge.sales.mapper.ProductMapper;
import com.fridge.sales.mapper.UserMapper;
import com.fridge.sales.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 统计服务实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public StatisticsVO getOverview() {
        StatisticsVO vo = new StatisticsVO();

        BigDecimal totalSales = ordersMapper.selectTotalSales();
        if (totalSales == null) {
            totalSales = BigDecimal.ZERO;
        }
        vo.setTotalSales(totalSales);

        Long totalOrders = ordersMapper.selectTotalOrders();
        vo.setTotalOrders(totalOrders != null ? totalOrders : 0L);

        Long totalUsers = userMapper.selectCount(null);
        vo.setTotalUsers(totalUsers != null ? totalUsers : 0L);

        Long totalProducts = productMapper.selectCount(null);
        vo.setTotalProducts(totalProducts != null ? totalProducts : 0L);

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().plusDays(1).atStartOfDay();

        BigDecimal todaySales = ordersMapper.selectTodaySales(todayStart, todayEnd);
        if (todaySales == null) {
            todaySales = BigDecimal.ZERO;
        }
        vo.setTodaySales(todaySales);

        Long todayOrders = ordersMapper.selectTodayOrders(todayStart, todayEnd);
        vo.setTodayOrders(todayOrders != null ? todayOrders : 0L);

        Long todayUsers = userMapper.selectTodayUsers(todayStart, todayEnd);
        vo.setTodayUsers(todayUsers != null ? todayUsers : 0L);

        return vo;
    }

    @Override
    public List<SalesTrendVO> getSalesTrend(String type) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime;

        if ("week".equals(type)) {
            startTime = endTime.minusWeeks(12).with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            List<SalesTrendVO> dbData = ordersMapper.selectSalesTrendByWeek(startTime, endTime);
            return fillWeekData(dbData, startTime, endTime);
        } else if ("month".equals(type)) {
            startTime = endTime.minusMonths(12).withDayOfMonth(1);
            List<SalesTrendVO> dbData = ordersMapper.selectSalesTrendByMonth(startTime, endTime);
            return fillMonthData(dbData, startTime, endTime);
        } else {
            startTime = endTime.minusDays(30).toLocalDate().atStartOfDay();
            List<SalesTrendVO> dbData = ordersMapper.selectSalesTrendByDay(startTime, endTime);
            return fillDayData(dbData, startTime, endTime);
        }
    }

    /**
     * 填充按天的数据，确保每天都有数据点
     */
    private List<SalesTrendVO> fillDayData(List<SalesTrendVO> dbData, LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, SalesTrendVO> dataMap = new TreeMap<>();
        for (SalesTrendVO vo : dbData) {
            dataMap.put(vo.getDate(), vo);
        }

        List<SalesTrendVO> result = new ArrayList<>();
        LocalDate current = startTime.toLocalDate();
        LocalDate end = endTime.toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        while (!current.isAfter(end)) {
            String dateStr = current.format(formatter);
            SalesTrendVO vo = dataMap.get(dateStr);
            if (vo == null) {
                vo = new SalesTrendVO();
                vo.setDate(dateStr);
                vo.setSales(BigDecimal.ZERO);
                vo.setOrders(0L);
            }
            result.add(vo);
            current = current.plusDays(1);
        }

        return result;
    }

    /**
     * 填充按周的数据
     */
    private List<SalesTrendVO> fillWeekData(List<SalesTrendVO> dbData, LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, SalesTrendVO> dataMap = new TreeMap<>();
        for (SalesTrendVO vo : dbData) {
            dataMap.put(vo.getDate(), vo);
        }
        return new ArrayList<>(dataMap.values());
    }

    /**
     * 填充按月的数据
     */
    private List<SalesTrendVO> fillMonthData(List<SalesTrendVO> dbData, LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, SalesTrendVO> dataMap = new TreeMap<>();
        for (SalesTrendVO vo : dbData) {
            dataMap.put(vo.getDate(), vo);
        }
        return new ArrayList<>(dataMap.values());
    }

    @Override
    public List<CategorySalesVO> getCategorySales() {
        return productMapper.selectCategorySales();
    }

    @Override
    public List<HotProductVO> getHotProducts(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        return productMapper.selectHotProducts(limit);
    }
}
