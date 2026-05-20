package com.fridge.sales.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fridge.sales.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类Mapper接口
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
