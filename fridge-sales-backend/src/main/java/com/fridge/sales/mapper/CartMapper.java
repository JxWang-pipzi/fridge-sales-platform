package com.fridge.sales.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fridge.sales.dto.CartVO;
import com.fridge.sales.entity.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 购物车Mapper接口
 */
public interface CartMapper extends BaseMapper<Cart> {

    /**
     * 查询用户购物车列表（关联商品信息）
     *
     * @param userId 用户ID
     * @return 购物车列表
     */
    List<CartVO> selectCartWithProduct(@Param("userId") Long userId);

    /**
     * 根据用户ID和商品ID查询购物车项
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 购物车项
     */
    Cart selectByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
}
