package com.fridge.sales.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fridge.sales.dto.SalesTrendVO;
import com.fridge.sales.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

    /**
     * 查询总销售额（已完成订单）
     *
     * @return 总销售额
     */
    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status = 3")
    BigDecimal selectTotalSales();

    /**
     * 查询总订单数（已完成订单）
     *
     * @return 总订单数
     */
    @Select("SELECT COUNT(*) FROM orders WHERE status = 3")
    Long selectTotalOrders();

    /**
     * 查询今日销售额（已完成订单）
     *
     * @param startTime 今日开始时间
     * @param endTime   今日结束时间
     * @return 今日销售额
     */
    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status = 3 AND create_time >= #{startTime} AND create_time < #{endTime}")
    BigDecimal selectTodaySales(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 查询今日订单数（已完成订单）
     *
     * @param startTime 今日开始时间
     * @param endTime   今日结束时间
     * @return 今日订单数
     */
    @Select("SELECT COUNT(*) FROM orders WHERE status = 3 AND create_time >= #{startTime} AND create_time < #{endTime}")
    Long selectTodayOrders(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 按天查询销售趋势（已完成订单）
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 销售趋势列表
     */
    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m-%d') as date, " +
            "COALESCE(SUM(total_amount), 0) as sales, " +
            "COUNT(*) as orders " +
            "FROM orders " +
            "WHERE status = 3 AND create_time >= #{startTime} AND create_time < #{endTime} " +
            "GROUP BY DATE_FORMAT(create_time, '%Y-%m-%d') " +
            "ORDER BY date ASC")
    List<SalesTrendVO> selectSalesTrendByDay(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 按周查询销售趋势（已完成订单）
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 销售趋势列表
     */
    @Select("SELECT CONCAT(YEAR(create_time), '-W', LPAD(WEEK(create_time, 1), 2, '0')) as date, " +
            "COALESCE(SUM(total_amount), 0) as sales, " +
            "COUNT(*) as orders " +
            "FROM orders " +
            "WHERE status = 3 AND create_time >= #{startTime} AND create_time < #{endTime} " +
            "GROUP BY YEAR(create_time), WEEK(create_time, 1) " +
            "ORDER BY YEAR(create_time) ASC, WEEK(create_time, 1) ASC")
    List<SalesTrendVO> selectSalesTrendByWeek(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 按月查询销售趋势（已完成订单）
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 销售趋势列表
     */
    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m') as date, " +
            "COALESCE(SUM(total_amount), 0) as sales, " +
            "COUNT(*) as orders " +
            "FROM orders " +
            "WHERE status = 3 AND create_time >= #{startTime} AND create_time < #{endTime} " +
            "GROUP BY DATE_FORMAT(create_time, '%Y-%m') " +
            "ORDER BY date ASC")
    List<SalesTrendVO> selectSalesTrendByMonth(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
