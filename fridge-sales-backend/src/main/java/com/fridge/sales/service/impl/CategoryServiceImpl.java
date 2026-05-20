package com.fridge.sales.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fridge.sales.dto.CategoryDTO;
import com.fridge.sales.entity.Category;
import com.fridge.sales.mapper.CategoryMapper;
import com.fridge.sales.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类服务实现类
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public Category add(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setCreateTime(LocalDateTime.now());
        if (category.getSort() == null) {
            category.setSort(0);
        }
        categoryMapper.insert(category);
        return category;
    }

    @Override
    public Category update(CategoryDTO categoryDTO) {
        Category existingCategory = categoryMapper.selectById(categoryDTO.getId());
        if (existingCategory == null) {
            throw new RuntimeException("分类不存在");
        }
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryMapper.updateById(category);
        return category;
    }

    @Override
    public void delete(Long id) {
        categoryMapper.deleteById(id);
    }

    @Override
    public Category getById(Long id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public List<Category> list() {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        return categoryMapper.selectList(queryWrapper);
    }
}
