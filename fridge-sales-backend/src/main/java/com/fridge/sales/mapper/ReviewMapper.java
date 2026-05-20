package com.fridge.sales.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fridge.sales.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

    @Select("SELECT r.id, r.user_id, r.product_id, r.order_id, r.rating, r.content, r.images, r.status, r.create_time, r.update_time, u.username, u.avatar FROM review r LEFT JOIN sys_user u ON r.user_id = u.id WHERE r.product_id = #{productId} AND r.status = 1 ORDER BY r.create_time DESC")
    List<Map<String, Object>> selectByProductId(Long productId);

    @Select("SELECT COUNT(*) FROM review WHERE product_id = #{productId} AND status = 1")
    Integer countByProductId(Long productId);

    @Select("SELECT IFNULL(AVG(rating), 0) FROM review WHERE product_id = #{productId} AND status = 1")
    Double getAvgRating(Long productId);

    @Select("SELECT r.id, r.user_id, r.product_id, r.order_id, r.rating, r.content, r.images, r.status, r.create_time, r.update_time, u.username as username, p.name as productName FROM review r LEFT JOIN sys_user u ON r.user_id = u.id LEFT JOIN product p ON r.product_id = p.id ORDER BY r.create_time DESC")
    List<Map<String, Object>> selectAllWithUserAndProduct();
}
