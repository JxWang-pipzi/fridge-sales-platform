package com.fridge.sales.service;

import com.fridge.sales.dto.BrandDTO;
import com.fridge.sales.entity.Brand;

import java.util.List;

/**
 * 品牌服务接口
 */
public interface BrandService {

    /**
     * 查询品牌列表
     *
     * @return 品牌列表
     */
    List<Brand> list();

    /**
     * 根据ID查询品牌
     *
     * @param id 品牌ID
     * @return 品牌
     */
    Brand getById(Long id);

    /**
     * 添加品牌
     *
     * @param brandDTO 品牌DTO
     * @return 品牌
     */
    Brand add(BrandDTO brandDTO);

    /**
     * 更新品牌
     *
     * @param brandDTO 品牌DTO
     * @return 品牌
     */
    Brand update(BrandDTO brandDTO);

    /**
     * 删除品牌
     *
     * @param id 品牌ID
     */
    void delete(Long id);
}
