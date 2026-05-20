package com.fridge.sales.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fridge.sales.dto.FavoriteVO;
import com.fridge.sales.entity.Favorite;
import com.fridge.sales.mapper.FavoriteMapper;
import com.fridge.sales.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 收藏服务实现类
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    public boolean add(Long userId, Long productId) {
        Favorite existingFavorite = favoriteMapper.selectByUserIdAndProductId(userId, productId);
        if (existingFavorite != null) {
            return false;
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favorite.setCreateTime(LocalDateTime.now());
        return favoriteMapper.insert(favorite) > 0;
    }

    @Override
    public boolean remove(Long userId, Long productId) {
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                    .eq(Favorite::getProductId, productId);
        return favoriteMapper.delete(queryWrapper) > 0;
    }

    @Override
    public List<FavoriteVO> list(Long userId) {
        return favoriteMapper.selectFavoriteWithProduct(userId);
    }

    @Override
    public boolean isFavorite(Long userId, Long productId) {
        Favorite favorite = favoriteMapper.selectByUserIdAndProductId(userId, productId);
        return favorite != null;
    }
}
