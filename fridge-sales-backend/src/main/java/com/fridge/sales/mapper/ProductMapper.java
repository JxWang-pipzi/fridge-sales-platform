package com.fridge.sales.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fridge.sales.dto.CategorySalesVO;
import com.fridge.sales.dto.HotProductVO;
import com.fridge.sales.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 商品Mapper接口
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 扣减库存（乐观锁）
     */
    @Update("UPDATE product SET stock = stock - #{quantity} " +
            "WHERE id = #{id} AND stock >= #{quantity}")
    int reduceStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 恢复库存
     */
    @Update("UPDATE product SET stock = stock + #{quantity} " +
            "WHERE id = #{id}")
    int restoreStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 增加商品销量
     */
    @Update("UPDATE product SET sales = sales + #{quantity} WHERE id = #{id}")
    int incrementSales(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 查询热门商品排行（基于已完成订单）
     *
     * @param limit 限制数量
     * @return 热门商品列表
     */
    @Select("SELECT oi.product_id, oi.product_name, oi.product_image, " +
            "SUM(oi.quantity) as sales, SUM(oi.total_amount) as amount " +
            "FROM order_item oi " +
            "INNER JOIN orders o ON oi.order_id = o.id " +
            "WHERE o.status = 3 " +
            "GROUP BY oi.product_id, oi.product_name, oi.product_image " +
            "ORDER BY sales DESC " +
            "LIMIT #{limit}")
    List<HotProductVO> selectHotProducts(@Param("limit") Integer limit);

    /**
     * 查询分类销售统计（基于已完成订单）
     *
     * @return 分类销售统计列表
     */
    @Select("SELECT c.name as category_name, " +
            "COALESCE(SUM(oi.total_amount), 0) as sales, " +
            "COALESCE(SUM(oi.quantity), 0) as count " +
            "FROM category c " +
            "LEFT JOIN product p ON c.id = p.category_id " +
            "LEFT JOIN order_item oi ON p.id = oi.product_id " +
            "LEFT JOIN orders o ON oi.order_id = o.id AND o.status = 3 " +
            "GROUP BY c.id, c.name " +
            "ORDER BY sales DESC")
    List<CategorySalesVO> selectCategorySales();

    /**
     * 随机查询商品（使用Fisher-Yates洗牌算法）
     *
     * @param limit 限制数量
     * @return 随机商品列表
     */
    @Select("SELECT * FROM product WHERE status = 1 LIMIT #{limit}")
    List<Product> selectRandomProducts(@Param("limit") Integer limit);
}
