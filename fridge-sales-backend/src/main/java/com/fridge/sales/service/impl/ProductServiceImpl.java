package com.fridge.sales.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fridge.sales.common.PageResult;
import com.fridge.sales.dto.ProductDTO;
import com.fridge.sales.dto.ProductQueryDTO;
import com.fridge.sales.entity.Product;
import com.fridge.sales.mapper.ProductMapper;
import com.fridge.sales.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 商品服务实现类
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private static final Random RANDOM = new Random();

    public ProductServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public Product add(ProductDTO productDTO) {
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        if (product.getSales() == null) {
            product.setSales(0);
        }
        if (product.getStatus() == null) {
            product.setStatus(1);
        }
        productMapper.insert(product);
        return product;
    }

    @Override
    public Product update(ProductDTO productDTO) {
        Product existingProduct = productMapper.selectById(productDTO.getId());
        if (existingProduct == null) {
            throw new RuntimeException("商品不存在");
        }
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);
        return product;
    }

    @Override
    public void delete(Long id) {
        productMapper.deleteById(id);
    }

    @Override
    public Product getById(Long id) {
        return productMapper.selectById(id);
    }

    @Override
    public PageResult<Product> list(ProductQueryDTO queryDTO) {
        Long current = queryDTO.getCurrent();
        Long size = queryDTO.getSize();
        
        if (queryDTO.getPage() != null) {
            current = queryDTO.getPage().longValue();
        }
        if (queryDTO.getPageSize() != null) {
            size = queryDTO.getPageSize().longValue();
        }
        
        Page<Product> page = new Page<>(current, size);
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(Product::getStatus, queryDTO.getStatus());
        }
        
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Product::getName, queryDTO.getKeyword())
                    .or()
                    .like(Product::getBrand, queryDTO.getKeyword())
                    .or()
                    .like(Product::getModel, queryDTO.getKeyword())
                    .or()
                    .like(Product::getSku, queryDTO.getKeyword())
            );
        }
        
        if (queryDTO.getCategoryId() != null) {
            queryWrapper.eq(Product::getCategoryId, queryDTO.getCategoryId());
        }
        
        if (StringUtils.hasText(queryDTO.getCategory())) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Product::getName, queryDTO.getCategory())
                    .or()
                    .like(Product::getDescription, queryDTO.getCategory())
            );
        }
        
        if (queryDTO.getMinPrice() != null) {
            queryWrapper.ge(Product::getPrice, queryDTO.getMinPrice());
        }
        
        if (queryDTO.getMaxPrice() != null) {
            queryWrapper.le(Product::getPrice, queryDTO.getMaxPrice());
        }
        
        if (StringUtils.hasText(queryDTO.getBrand())) {
            queryWrapper.like(Product::getBrand, queryDTO.getBrand());
        }
        
        if (StringUtils.hasText(queryDTO.getSku())) {
            queryWrapper.like(Product::getSku, queryDTO.getSku());
        }
        
        if (StringUtils.hasText(queryDTO.getSortBy())) {
            switch (queryDTO.getSortBy()) {
                case "price-asc":
                    queryWrapper.orderByAsc(Product::getPrice);
                    break;
                case "price-desc":
                    queryWrapper.orderByDesc(Product::getPrice);
                    break;
                case "sales":
                    queryWrapper.orderByDesc(Product::getSales);
                    break;
                default:
                    queryWrapper.orderByDesc(Product::getCreateTime);
            }
        } else {
            queryWrapper.orderByDesc(Product::getCreateTime);
        }
        
        Page<Product> result = productMapper.selectPage(page, queryWrapper);
        
        return PageResult.of(
                result.getTotal(),
                result.getPages(),
                result.getCurrent(),
                result.getSize(),
                result.getRecords()
        );
    }

    @Override
    public void updateStock(Long id, Integer stock) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        LambdaUpdateWrapper<Product> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Product::getId, id)
                .set(Product::getStock, product.getStock() + stock)
                .set(Product::getUpdateTime, LocalDateTime.now());
        productMapper.update(null, updateWrapper);
    }

    @Override
    public List<Product> getHotProducts(Integer limit) {
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getStatus, 1)
                .orderByDesc(Product::getSales)
                .last("LIMIT " + limit);
        return productMapper.selectList(queryWrapper);
    }

    @Override
    public List<Product> searchByKeyword(String keyword) {
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getStatus, 1)
                .and(wrapper -> wrapper
                        .like(Product::getName, keyword)
                        .or()
                        .like(Product::getBrand, keyword)
                        .or()
                        .like(Product::getModel, keyword)
                )
                .orderByDesc(Product::getSales);
        return productMapper.selectList(queryWrapper);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        LambdaUpdateWrapper<Product> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Product::getId, id)
                .set(Product::getStatus, status)
                .set(Product::getUpdateTime, LocalDateTime.now());
        productMapper.update(null, updateWrapper);
    }

    @Override
    public List<Product> getRandomProducts(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 3;
        }
        List<Product> products = productMapper.selectRandomProducts(limit);

        // 使用Fisher-Yates洗牌算法
        shuffle(products, RANDOM);

        return products;
    }

    @Override
    public void shuffle(List<?> list, Random random) {
        for (int i = list.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            // 检查 i 是否等于 j，避免不必要的交换
            if (i != j) {
                Collections.swap(list, i, j);
            }
        }
    }
}
