package com.fridge.sales.controller;

import com.fridge.sales.annotation.Log;
import com.fridge.sales.common.Result;
import com.fridge.sales.dto.BrandDTO;
import com.fridge.sales.entity.Brand;
import com.fridge.sales.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.fridge.sales.annotation.Log.BusinessType.*;

/**
 * 品牌控制器
 */
@RestController
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping("/brand/list")
    public Result<List<Brand>> list() {
        List<Brand> brands = brandService.list();
        return Result.success(brands);
    }

    @GetMapping("/brand/{id}")
    public Result<Brand> getById(@PathVariable Long id) {
        Brand brand = brandService.getById(id);
        if (brand == null) {
            return Result.error("品牌不存在");
        }
        return Result.success(brand);
    }

    @Log(title = "品牌管理", businessType = INSERT)
    @PostMapping("/admin/brand")
    public Result<Brand> add(@Valid @RequestBody BrandDTO brandDTO) {
        Brand brand = brandService.add(brandDTO);
        return Result.success(brand);
    }

    @Log(title = "品牌管理", businessType = UPDATE)
    @PutMapping("/admin/brand")
    public Result<Brand> update(@Valid @RequestBody BrandDTO brandDTO) {
        if (brandDTO.getId() == null) {
            return Result.error("品牌ID不能为空");
        }
        Brand brand = brandService.update(brandDTO);
        return Result.success(brand);
    }

    @Log(title = "品牌管理", businessType = DELETE)
    @DeleteMapping("/admin/brand/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        brandService.delete(id);
        return Result.success();
    }
}
