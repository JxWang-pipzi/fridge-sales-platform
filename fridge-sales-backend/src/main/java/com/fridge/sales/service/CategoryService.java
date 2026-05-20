package com.fridge.sales.service;

import com.fridge.sales.dto.CategoryDTO;
import com.fridge.sales.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService {

    /**
     * 添加分类
     * @param categoryDTO 分类信息
     * @return 分类实体
     */
    Category add(CategoryDTO categoryDTO);

    /**
     * 更新分类
     * @param categoryDTO 分类信息
     * @return 分类实体
     */
    Category update(CategoryDTO categoryDTO);

    /**
     * 删除分类
     * @param id 分类ID
     */
    void delete(Long id);

    /**
     * 根据ID查询分类
     * @param id 分类ID
     * @return 分类实体
     */
    Category getById(Long id);

    /**
     * 查询分类列表
     * @return 分类列表
     */
    List<Category> list();
}
