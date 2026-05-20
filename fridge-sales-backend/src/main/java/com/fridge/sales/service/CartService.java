package com.fridge.sales.service;

import com.fridge.sales.dto.CartAddDTO;
import com.fridge.sales.dto.CartUpdateDTO;
import com.fridge.sales.dto.CartVO;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {

    /**
     * 添加商品到购物车
     *
     * @param userId 用户ID
     * @param dto    添加购物车DTO
     */
    void add(Long userId, CartAddDTO dto);

    /**
     * 查询用户购物车列表
     *
     * @param userId 用户ID
     * @return 购物车列表
     */
    List<CartVO> list(Long userId);

    /**
     * 更新购物车商品数量
     *
     * @param userId 用户ID
     * @param dto    更新数量DTO
     */
    void updateQuantity(Long userId, CartUpdateDTO dto);

    /**
     * 删除购物车商品
     *
     * @param userId 用户ID
     * @param id     购物车项ID
     */
    void delete(Long userId, Long id);

    /**
     * 清空购物车
     *
     * @param userId 用户ID
     */
    void clear(Long userId);

    /**
     * 检查库存是否充足
     *
     * @param productId 商品ID
     * @param quantity  数量
     * @return 是否充足
     */
    boolean checkStock(Long productId, Integer quantity);

    /**
     * 获取购物车商品数量
     *
     * @param userId 用户ID
     * @return 购物车商品数量
     */
    Integer count(Long userId);
}
