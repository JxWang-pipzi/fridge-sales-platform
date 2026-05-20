package com.fridge.sales.service;

import com.fridge.sales.dto.FavoriteVO;

import java.util.List;

/**
 * 收藏服务接口
 */
public interface FavoriteService {

    /**
     * 添加收藏
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 是否成功
     */
    boolean add(Long userId, Long productId);

    /**
     * 取消收藏
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 是否成功
     */
    boolean remove(Long userId, Long productId);

    /**
     * 查询用户收藏列表
     *
     * @param userId 用户ID
     * @return 收藏列表
     */
    List<FavoriteVO> list(Long userId);

    /**
     * 检查是否已收藏
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 是否已收藏
     */
    boolean isFavorite(Long userId, Long productId);
}
