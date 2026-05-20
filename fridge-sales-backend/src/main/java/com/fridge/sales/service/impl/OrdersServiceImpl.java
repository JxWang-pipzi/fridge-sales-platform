package com.fridge.sales.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fridge.sales.common.PageResult;
import com.fridge.sales.common.ResultCode;
import com.fridge.sales.common.enums.OrderStatus;
import com.fridge.sales.common.exception.BusinessException;
import com.fridge.sales.dto.OrderCreateDTO;
import com.fridge.sales.dto.OrderQueryDTO;
import com.fridge.sales.dto.OrderVO;
import com.fridge.sales.entity.*;
import com.fridge.sales.mapper.*;
import com.fridge.sales.service.OrdersService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(Long userId, OrderCreateDTO dto) {
        Address address = addressMapper.selectById(dto.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ADDRESS_NOT_FOUND);
        }

        LambdaQueryWrapper<Cart> cartWrapper = new LambdaQueryWrapper<>();
        cartWrapper.in(Cart::getId, dto.getCartItemIds())
                .eq(Cart::getUserId, userId);
        List<Cart> cartItems = cartMapper.selectList(cartWrapper);
        if (cartItems.isEmpty()) {
            throw new BusinessException(ResultCode.CART_ITEM_NOT_FOUND);
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // 批量查询商品并校验库存（数据库级别校验）
        List<Long> productIds = cartItems.stream()
                .map(Cart::getProductId)
                .collect(Collectors.toList());

        // 查询所有需要的商品
        List<Product> products = productMapper.selectBatchIds(productIds);
        if (products.size() != productIds.size()) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }

        // 按商品ID分组，方便后续查找
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 校验并计算订单金额
        for (Cart cartItem : cartItems) {
            Product product = productMap.get(cartItem.getProductId());
            if (product == null) {
                throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
            }

            // 检查库存（使用数据库级别的锁）
            int updated = productMapper.reduceStock(cartItem.getProductId(), cartItem.getQuantity());
            if (updated <= 0) {
                throw new BusinessException(ResultCode.PRODUCT_STOCK_NOT_ENOUGH);
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getImage());
            orderItem.setSku(product.getSku());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(cartItem.getQuantity()));
            orderItem.setTotalAmount(itemTotal);
            orderItems.add(orderItem);

            totalAmount = totalAmount.add(itemTotal);
        }

        // 创建订单
        Orders order = new Orders();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setStatus(OrderStatus.UNPAID.getCode());
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        String fullAddress = address.getProvince() + address.getCity() +
                address.getDistrict() + address.getDetailAddress();
        order.setReceiverAddress(fullAddress);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        ordersMapper.insert(order);

        // 插入订单项
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrderId(order.getId());
            orderItemMapper.insert(orderItem);
        }

        // 删除购物车
        cartMapper.delete(cartWrapper);

        return order.getId();
    }

    @Override
    public PageResult<OrderVO> list(Long userId, OrderQueryDTO dto) {
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, userId);
        if (dto.getStatus() != null) {
            wrapper.eq(Orders::getStatus, dto.getStatus());
        }
        wrapper.orderByDesc(Orders::getCreateTime);

        Page<Orders> page = new Page<>(dto.getCurrent(), dto.getSize());
        Page<Orders> orderPage = ordersMapper.selectPage(page, wrapper);

        List<OrderVO> orderVOList = orderPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(
                orderPage.getTotal(),
                orderPage.getPages(),
                orderPage.getCurrent(),
                orderPage.getSize(),
                orderVOList
        );
    }

    @Override
    public OrderVO getById(Long id, Long userId) {
        Orders order = ordersMapper.selectById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        return convertToVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long id, Long userId) {
        Orders order = ordersMapper.selectById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (!order.getStatus().equals(OrderStatus.UNPAID.getCode())) {
            throw new BusinessException("只有待付款订单可以取消");
        }

        order.setStatus(OrderStatus.CANCELLED.getCode());
        order.setUpdateTime(LocalDateTime.now());
        ordersMapper.updateById(order);

        restoreStock(order.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pay(Long id, Long userId, Integer paymentType) {
        Orders order = ordersMapper.selectById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (!order.getStatus().equals(OrderStatus.UNPAID.getCode())) {
            throw new BusinessException("只有待付款订单可以支付");
        }

        order.setStatus(OrderStatus.UNSHIPPED.getCode());
        order.setPaymentType(paymentType);
        order.setPayTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        ordersMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceive(Long id, Long userId) {
        Orders order = ordersMapper.selectById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (!order.getStatus().equals(OrderStatus.UNRECEIVED.getCode())) {
            throw new BusinessException("只有待收货订单可以确认收货");
        }

        order.setStatus(OrderStatus.COMPLETED.getCode());
        order.setReceiveTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        ordersMapper.updateById(order);

        // 确认收货后，增加商品销量
        incrementSales(order.getId());
    }

    @Override
    public PageResult<OrderVO> adminList(OrderQueryDTO dto) {
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        if (dto.getStatus() != null) {
            wrapper.eq(Orders::getStatus, dto.getStatus());
        }
        wrapper.orderByDesc(Orders::getCreateTime);

        Page<Orders> page = new Page<>(dto.getCurrent(), dto.getSize());
        Page<Orders> orderPage = ordersMapper.selectPage(page, wrapper);

        List<OrderVO> orderVOList = orderPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(
                orderPage.getTotal(),
                orderPage.getPages(),
                orderPage.getCurrent(),
                orderPage.getSize(),
                orderVOList
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deliver(Long id, String expressCompany, String expressNo) {
        Orders order = ordersMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (!order.getStatus().equals(OrderStatus.UNSHIPPED.getCode())) {
            throw new BusinessException("只有待发货订单可以发货");
        }

        order.setStatus(OrderStatus.UNRECEIVED.getCode());
        order.setExpressCompany(expressCompany);
        order.setExpressNo(expressNo);
        order.setDeliverTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        ordersMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Orders order = ordersMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }

        order.setStatus(status);
        order.setUpdateTime(LocalDateTime.now());
        ordersMapper.updateById(order);
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = RandomUtil.randomNumbers(6);
        return timestamp + random;
    }

    /**
     * 恢复库存
     */
    private void restoreStock(Long orderId) {
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> orderItems = orderItemMapper.selectList(wrapper);

        for (OrderItem orderItem : orderItems) {
            productMapper.restoreStock(orderItem.getProductId(), orderItem.getQuantity());
        }
    }

    /**
     * 增加商品销量
     */
    private void incrementSales(Long orderId) {
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> orderItems = orderItemMapper.selectList(wrapper);

        for (OrderItem orderItem : orderItems) {
            productMapper.incrementSales(orderItem.getProductId(), orderItem.getQuantity());
        }
    }

    /**
     * 转换为VO
     */
    private OrderVO convertToVO(Orders order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);

        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, order.getId());
        List<OrderItem> orderItems = orderItemMapper.selectList(wrapper);
        vo.setOrderItems(orderItems);

        return vo;
    }

    @Override
    public java.util.Map<String, Long> getOrderStats(Long userId) {
        java.util.Map<String, Long> stats = new java.util.HashMap<>();
        
        LambdaQueryWrapper<Orders> unpaidWrapper = new LambdaQueryWrapper<>();
        unpaidWrapper.eq(Orders::getUserId, userId)
                .eq(Orders::getStatus, OrderStatus.UNPAID.getCode());
        stats.put("unpaid", ordersMapper.selectCount(unpaidWrapper));

        LambdaQueryWrapper<Orders> unshippedWrapper = new LambdaQueryWrapper<>();
        unshippedWrapper.eq(Orders::getUserId, userId)
                .eq(Orders::getStatus, OrderStatus.UNSHIPPED.getCode());
        stats.put("unshipped", ordersMapper.selectCount(unshippedWrapper));

        LambdaQueryWrapper<Orders> unreceivedWrapper = new LambdaQueryWrapper<>();
        unreceivedWrapper.eq(Orders::getUserId, userId)
                .eq(Orders::getStatus, OrderStatus.UNRECEIVED.getCode());
        stats.put("unreceived", ordersMapper.selectCount(unreceivedWrapper));

        LambdaQueryWrapper<Orders> completedWrapper = new LambdaQueryWrapper<>();
        completedWrapper.eq(Orders::getUserId, userId)
                .eq(Orders::getStatus, OrderStatus.COMPLETED.getCode());
        stats.put("completed", ordersMapper.selectCount(completedWrapper));

        return stats;
    }

    @Override
    public OrderVO adminGetById(Long id) {
        Orders order = ordersMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        return convertToVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, Long userId) {
        Orders order = ordersMapper.selectById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        // 只有已取消或已完成的订单可以删除
        if (!order.getStatus().equals(OrderStatus.CANCELLED.getCode()) 
                && !order.getStatus().equals(OrderStatus.COMPLETED.getCode())) {
            throw new BusinessException("只有已取消或已完成的订单可以删除");
        }

        ordersMapper.deleteById(id);
    }
}
