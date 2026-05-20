package com.fridge.sales.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fridge.sales.dto.BrandDTO;
import com.fridge.sales.entity.Brand;
import com.fridge.sales.mapper.BrandMapper;
import com.fridge.sales.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 品牌服务实现类
 */
@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public List<Brand> list() {
        LambdaQueryWrapper<Brand> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Brand::getSort)
                .orderByDesc(Brand::getCreateTime);
        return brandMapper.selectList(wrapper);
    }

    @Override
    public Brand getById(Long id) {
        return brandMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Brand add(BrandDTO brandDTO) {
        Brand brand = new Brand();
        brand.setName(brandDTO.getName());
        brand.setLogo(brandDTO.getLogo());
        brand.setDescription(brandDTO.getDescription());
        brand.setSort(brandDTO.getSort() != null ? brandDTO.getSort() : 0);
        brand.setStatus(brandDTO.getStatus() != null ? brandDTO.getStatus() : 1);
        brand.setCreateTime(LocalDateTime.now());
        brand.setUpdateTime(LocalDateTime.now());
        brandMapper.insert(brand);
        return brand;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Brand update(BrandDTO brandDTO) {
        Brand brand = brandMapper.selectById(brandDTO.getId());
        if (brand == null) {
            throw new RuntimeException("品牌不存在");
        }
        brand.setName(brandDTO.getName());
        brand.setLogo(brandDTO.getLogo());
        brand.setDescription(brandDTO.getDescription());
        if (brandDTO.getSort() != null) {
            brand.setSort(brandDTO.getSort());
        }
        if (brandDTO.getStatus() != null) {
            brand.setStatus(brandDTO.getStatus());
        }
        brand.setUpdateTime(LocalDateTime.now());
        brandMapper.updateById(brand);
        return brand;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        brandMapper.deleteById(id);
    }
}
