package com.fridge.sales.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fridge.sales.common.PageResult;
import com.fridge.sales.dto.ReviewDTO;
import com.fridge.sales.entity.OrderItem;
import com.fridge.sales.entity.Orders;
import com.fridge.sales.entity.Product;
import com.fridge.sales.entity.Review;
import com.fridge.sales.mapper.OrderItemMapper;
import com.fridge.sales.mapper.OrdersMapper;
import com.fridge.sales.mapper.ProductMapper;
import com.fridge.sales.mapper.ReviewMapper;
import com.fridge.sales.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final OrdersMapper ordersMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;

    public ReviewServiceImpl(ReviewMapper reviewMapper, OrdersMapper ordersMapper, OrderItemMapper orderItemMapper, ProductMapper productMapper) {
        this.reviewMapper = reviewMapper;
        this.ordersMapper = ordersMapper;
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
    }

    @Override
    public PageResult<Map<String, Object>> list(Long productId, Integer page, Integer size) {
        Page<Map<String, Object>> pageObj = new Page<>(page, size);
        List<Map<String, Object>> records = reviewMapper.selectByProductId(productId);
        
        int total = records.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        List<Map<String, Object>> pageRecords = records.subList(start, end);
        
        return PageResult.of((long) total, (long) Math.ceil((double) total / size), (long) page, (long) size, pageRecords);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Review add(Long userId, ReviewDTO dto) {
        if (!hasPurchased(userId, dto.getProductId())) {
            throw new RuntimeException("只有购买过此商品的用户才能评价");
        }

        LambdaQueryWrapper<Review> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(Review::getUserId, userId)
                .eq(Review::getProductId, dto.getProductId());
        if (reviewMapper.selectCount(existWrapper) > 0) {
            throw new RuntimeException("您已评价过此商品");
        }

        Review review = new Review();
        review.setUserId(userId);
        review.setProductId(dto.getProductId());
        review.setOrderId(dto.getOrderId());
        review.setRating(dto.getRating());
        review.setContent(dto.getContent());
        review.setImages(dto.getImages());
        review.setStatus(1);
        review.setCreateTime(LocalDateTime.now());
        review.setUpdateTime(LocalDateTime.now());
        
        reviewMapper.insert(review);
        
        // 更新商品的平均评分
        updateProductRating(dto.getProductId());
        
        return review;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, Long id) {
        Review review = reviewMapper.selectById(id);
        if (review == null) {
            throw new RuntimeException("评价不存在");
        }
        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此评价");
        }
        Long productId = review.getProductId();
        reviewMapper.deleteById(id);
        
        // 更新商品的平均评分
        updateProductRating(productId);
    }

    /**
     * 更新商品的平均评分
     */
    private void updateProductRating(Long productId) {
        Double avgRating = reviewMapper.getAvgRating(productId);
        Integer rating = avgRating != null ? (int) Math.floor(avgRating) : 0;
        
        Product product = productMapper.selectById(productId);
        if (product != null) {
            product.setRating(rating);
            productMapper.updateById(product);
        }
    }

    @Override
    public boolean hasPurchased(Long userId, Long productId) {
        LambdaQueryWrapper<Orders> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(Orders::getUserId, userId)
                .eq(Orders::getStatus, 3);
        List<Orders> orders = ordersMapper.selectList(orderWrapper);
        
        for (Orders order : orders) {
            LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(OrderItem::getOrderId, order.getId())
                    .eq(OrderItem::getProductId, productId);
            if (orderItemMapper.selectCount(itemWrapper) > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<String, Object> getStats(Long productId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", reviewMapper.countByProductId(productId));
        stats.put("avgRating", reviewMapper.getAvgRating(productId));
        return stats;
    }

    @Override
    public PageResult<Map<String, Object>> adminList(Integer page, Integer pageSize) {
        List<Map<String, Object>> allRecords = reviewMapper.selectAllWithUserAndProduct();
        
        int total = allRecords.size();
        int start = (page - 1) * pageSize;
        
        List<Map<String, Object>> pageRecords;
        if (start >= total || total == 0) {
            pageRecords = List.of();
        } else {
            int end = Math.min(start + pageSize, total);
            pageRecords = allRecords.subList(start, end);
        }
        
        return PageResult.of((long) total, (long) Math.ceil((double) total / pageSize), (long) page, (long) pageSize, pageRecords);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Review review = reviewMapper.selectById(id);
        if (review == null) {
            throw new RuntimeException("评价不存在");
        }
        review.setStatus(status);
        review.setUpdateTime(LocalDateTime.now());
        reviewMapper.updateById(review);
        
        // 更新商品的平均评分
        updateProductRating(review.getProductId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminDelete(Long id) {
        Review review = reviewMapper.selectById(id);
        if (review != null) {
            Long productId = review.getProductId();
            reviewMapper.deleteById(id);
            // 更新商品的平均评分
            updateProductRating(productId);
        }
    }
}
