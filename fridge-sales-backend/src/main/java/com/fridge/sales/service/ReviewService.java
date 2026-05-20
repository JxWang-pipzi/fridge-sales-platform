package com.fridge.sales.service;

import com.fridge.sales.common.PageResult;
import com.fridge.sales.dto.ReviewDTO;
import com.fridge.sales.entity.Review;

import java.util.List;
import java.util.Map;

public interface ReviewService {

    PageResult<Map<String, Object>> list(Long productId, Integer page, Integer size);

    Review add(Long userId, ReviewDTO dto);

    void delete(Long userId, Long id);

    boolean hasPurchased(Long userId, Long productId);

    Map<String, Object> getStats(Long productId);

    PageResult<Map<String, Object>> adminList(Integer page, Integer pageSize);

    void updateStatus(Long id, Integer status);

    void adminDelete(Long id);
}
