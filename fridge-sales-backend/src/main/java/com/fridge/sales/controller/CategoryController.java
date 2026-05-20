package com.fridge.sales.controller;

import com.fridge.sales.annotation.Log;
import com.fridge.sales.common.Result;
import com.fridge.sales.dto.CategoryDTO;
import com.fridge.sales.entity.Category;
import com.fridge.sales.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.fridge.sales.annotation.Log.BusinessType.*;

/**
 * 分类控制器
 */
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/category/list")
    public Result<List<Category>> list() {
        List<Category> categories = categoryService.list();
        return Result.success(categories);
    }

    @GetMapping("/category/{id}")
    public Result<Category> getById(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        return Result.success(category);
    }

    @Log(title = "分类管理", businessType = INSERT)
    @PostMapping("/admin/category")
    public Result<Category> add(@Valid @RequestBody CategoryDTO categoryDTO) {
        Category category = categoryService.add(categoryDTO);
        return Result.success(category);
    }

    @Log(title = "分类管理", businessType = UPDATE)
    @PutMapping("/admin/category")
    public Result<Category> update(@Valid @RequestBody CategoryDTO categoryDTO) {
        if (categoryDTO.getId() == null) {
            return Result.error("分类ID不能为空");
        }
        Category category = categoryService.update(categoryDTO);
        return Result.success(category);
    }

    @Log(title = "分类管理", businessType = DELETE)
    @DeleteMapping("/admin/category/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }
}
