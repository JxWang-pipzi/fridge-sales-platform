package com.fridge.sales.service;

import com.fridge.sales.common.PageResult;
import com.fridge.sales.dto.ProductDTO;
import com.fridge.sales.dto.ProductQueryDTO;
import com.fridge.sales.entity.Product;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 商品服务接口
 */
public interface ProductService {

    /**
     * 添加商品
     * @param productDTO 商品信息
     * @return 商品实体
     */
    Product add(ProductDTO productDTO);

    /**
     * 更新商品
     * @param productDTO 商品信息
     * @return 商品实体
     */
    Product update(ProductDTO productDTO);

    /**
     * 删除商品
     * @param id 商品ID
     */
    void delete(Long id);

    /**
     * 根据ID查询商品
     * @param id 商品ID
     * @return 商品实体
     */
    Product getById(Long id);

    /**
     * 分页查询商品列表
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<Product> list(ProductQueryDTO queryDTO);

    /**
     * 更新商品库存
     * @param id 商品ID
     * @param stock 库存变化量（正数增加，负数减少）
     */
    void updateStock(Long id, Integer stock);

    /**
     * 获取热门商品
     * @param limit 数量限制
     * @return 热门商品列表
     */
    List<Product> getHotProducts(Integer limit);

    /**
     * 按关键词搜索商品
     * @param keyword 关键词
     * @return 商品列表
     */
    List<Product> searchByKeyword(String keyword);

    /**
     * 更新商品状态
     * @param id 商品ID
     * @param status 状态：0-下架，1-上架
     */
    void updateStatus(Long id, Integer status);

    /**
     * 获取随机推荐商品
     * @param limit 数量限制
     * @return 随机商品列表
     */
    List<Product> getRandomProducts(Integer limit);

    /**
     * Fisher-Yates洗牌算法
     * @param list 待洗牌的列表
     * @param random 随机数生成器
     */
    default void shuffle(List<?> list, Random random) {
        for (int i = list.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            // 检查 i 是否等于 j，避免不必要的交换
            if (i != j) {
                Collections.swap(list, i, j);
            }
        }
    }
}
