package com.fridge.sales.controller;

import com.fridge.sales.annotation.Log;
import com.fridge.sales.common.PageResult;
import com.fridge.sales.common.Result;
import com.fridge.sales.dto.ProductDTO;
import com.fridge.sales.dto.ProductQueryDTO;
import com.fridge.sales.entity.Product;
import com.fridge.sales.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fridge.sales.annotation.Log.BusinessType.*;

/**
 * 商品控制器
 */
@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/product/list")
    public Result<PageResult<Product>> list(ProductQueryDTO queryDTO) {
        PageResult<Product> result = productService.list(queryDTO);
        return Result.success(result);
    }

    @GetMapping("/product/{id}")
    public Result<Product> getById(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        return Result.success(product);
    }

    @GetMapping("/product/hot")
    public Result<List<Product>> getHotProducts(
            @RequestParam(defaultValue = "10") Integer limit) {
        List<Product> products = productService.getHotProducts(limit);
        return Result.success(products);
    }

    @GetMapping("/product/recommend")
    public Result<List<Product>> getRandomProducts(
            @RequestParam(defaultValue = "3") Integer limit) {
        List<Product> products = productService.getRandomProducts(limit);
        return Result.success(products);
    }

    @GetMapping("/product/search")
    public Result<List<Product>> search(@RequestParam String keyword) {
        List<Product> products = productService.searchByKeyword(keyword);
        return Result.success(products);
    }

    @Log(title = "商品管理", businessType = INSERT)
    @PostMapping("/admin/product")
    public Result<Product> add(@Valid @RequestBody ProductDTO productDTO) {
        Product product = productService.add(productDTO);
        return Result.success(product);
    }

    @Log(title = "商品管理", businessType = UPDATE)
    @PutMapping("/admin/product")
    public Result<Product> update(@Valid @RequestBody ProductDTO productDTO) {
        if (productDTO.getId() == null) {
            return Result.error("商品ID不能为空");
        }
        Product product = productService.update(productDTO);
        return Result.success(product);
    }

    @Log(title = "商品管理", businessType = DELETE)
    @DeleteMapping("/admin/product/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return Result.success();
    }

    @Log(title = "商品管理", businessType = UPDATE)
    @PutMapping("/admin/product/status")
    public Result<Void> updateStatus(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer status = Integer.valueOf(params.get("status").toString());
        productService.updateStatus(id, status);
        return Result.success();
    }
}
