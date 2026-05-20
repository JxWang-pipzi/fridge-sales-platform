package com.fridge.sales.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fridge.sales.dto.FavoriteVO;
import com.fridge.sales.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 收藏Mapper接口
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {

    /**
     * 查询用户收藏列表（关联商品信息）
     *
     * @param userId 用户ID
     * @return 收藏列表
     */
    @Select("SELECT f.id, f.user_id, f.product_id, f.create_time, " +
            "p.name as product_name, p.image as product_image, p.price as product_price, " +
            "p.description as product_description, p.brand as product_brand, " +
            "p.model as product_model, p.stock as product_stock, p.status as product_status " +
            "FROM favorite f " +
            "LEFT JOIN product p ON f.product_id = p.id " +
            "WHERE f.user_id = #{userId} " +
            "ORDER BY f.create_time DESC")
    List<FavoriteVO> selectFavoriteWithProduct(@Param("userId") Long userId);

    /**
     * 根据用户ID和商品ID查询收藏
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 收藏记录
     */
    @Select("SELECT * FROM favorite WHERE user_id = #{userId} AND product_id = #{productId}")
    Favorite selectByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
}
