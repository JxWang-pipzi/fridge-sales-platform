package com.fridge.sales.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fridge.sales.common.Result;
import com.fridge.sales.entity.Product;
import com.fridge.sales.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/inventory")
public class InventoryController {

    @Autowired
    private ProductMapper productMapper;

    @GetMapping("/list")
    public Result<Page<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String model) {
        Page<Product> page = new Page<>(current, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (productName != null && !productName.isEmpty()) {
            wrapper.like(Product::getName, productName);
        }
        if (model != null && !model.isEmpty()) {
            wrapper.like(Product::getModel, model);
        }
        wrapper.orderByDesc(Product::getCreateTime);
        Page<Product> productPage = productMapper.selectPage(page, wrapper);
        
        Page<Map<String, Object>> resultPage = new Page<>();
        resultPage.setCurrent(productPage.getCurrent());
        resultPage.setSize(productPage.getSize());
        resultPage.setTotal(productPage.getTotal());
        resultPage.setPages(productPage.getPages());
        
        List<Map<String, Object>> records = productPage.getRecords().stream()
                .map(product -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", product.getId());
                    map.put("productName", product.getName());
                    map.put("model", product.getModel());
                    map.put("stock", product.getStock());
                    map.put("lowStockThreshold", 10);
                    return map;
                })
                .collect(Collectors.toList());
        resultPage.setRecords(records);
        
        return Result.success(resultPage);
    }

    @PutMapping("/{id}")
    public Result<Void> updateStock(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Integer stock = Integer.valueOf(params.get("stock").toString());
        Product product = productMapper.selectById(id);
        if (product == null) {
            return Result.error(404, "商品不存在");
        }
        product.setStock(stock);
        productMapper.updateById(product);
        return Result.success();
    }
}
