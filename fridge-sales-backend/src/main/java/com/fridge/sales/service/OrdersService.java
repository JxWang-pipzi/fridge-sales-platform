package com.fridge.sales.service;

import com.fridge.sales.common.PageResult;
import com.fridge.sales.dto.OrderCreateDTO;
import com.fridge.sales.dto.OrderQueryDTO;
import com.fridge.sales.dto.OrderVO;
import com.fridge.sales.entity.Orders;

/**
 * 订单服务接口
 */
public interface OrdersService {

    /**
     * 创建订单
     *
     * @param userId 用户ID
     * @param dto    创建订单DTO
     * @return 订单ID
     */
    Long create(Long userId, OrderCreateDTO dto);

    /**
     * 查询用户订单列表
     *
     * @param userId 用户ID
     * @param dto    查询条件
     * @return 订单分页列表
     */
    PageResult<OrderVO> list(Long userId, OrderQueryDTO dto);

    /**
     * 查询订单详情
     *
     * @param id     订单ID
     * @param userId 用户ID
     * @return 订单详情
     */
    OrderVO getById(Long id, Long userId);

    /**
     * 取消订单
     *
     * @param id     订单ID
     * @param userId 用户ID
     */
    void cancel(Long id, Long userId);

    /**
     * 支付订单
     *
     * @param id          订单ID
     * @param userId      用户ID
     * @param paymentType 支付方式
     */
    void pay(Long id, Long userId, Integer paymentType);

    /**
     * 确认收货
     *
     * @param id     订单ID
     * @param userId 用户ID
     */
    void confirmReceive(Long id, Long userId);

    /**
     * 管理员查询所有订单
     *
     * @param dto 查询条件
     * @return 订单分页列表
     */
    PageResult<OrderVO> adminList(OrderQueryDTO dto);

    /**
     * 管理员发货
     *
     * @param id 订单ID
     * @param expressCompany 物流公司
     * @param expressNo 物流单号
     */
    void deliver(Long id, String expressCompany, String expressNo);

    /**
     * 更新订单状态
     *
     * @param id     订单ID
     * @param status 订单状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 获取用户订单统计
     *
     * @param userId 用户ID
     * @return 统计数据
     */
    java.util.Map<String, Long> getOrderStats(Long userId);

    /**
     * 管理员查询订单详情
     *
     * @param id 订单ID
     * @return 订单详情
     */
    OrderVO adminGetById(Long id);

    /**
     * 删除订单（软删除）
     *
     * @param id     订单ID
     * @param userId 用户ID
     */
    void delete(Long id, Long userId);
}
