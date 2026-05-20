package com.fridge.sales.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fridge.sales.common.ResultCode;
import com.fridge.sales.common.exception.BusinessException;
import com.fridge.sales.dto.CartAddDTO;
import com.fridge.sales.dto.CartUpdateDTO;
import com.fridge.sales.dto.CartVO;
import com.fridge.sales.entity.Cart;
import com.fridge.sales.entity.Product;
import com.fridge.sales.mapper.CartMapper;
import com.fridge.sales.mapper.ProductMapper;
import com.fridge.sales.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车服务实现类
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(Long userId, CartAddDTO dto) {
        Product product = productMapper.selectById(dto.getProductId());
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }

        if (!checkStock(dto.getProductId(), dto.getQuantity())) {
            throw new BusinessException(ResultCode.PRODUCT_STOCK_NOT_ENOUGH);
        }

        Cart existCart = cartMapper.selectByUserIdAndProductId(userId, dto.getProductId());
        if (existCart != null) {
            int newQuantity = existCart.getQuantity() + dto.getQuantity();
            if (!checkStock(dto.getProductId(), newQuantity)) {
                throw new BusinessException(ResultCode.PRODUCT_STOCK_NOT_ENOUGH);
            }
            existCart.setQuantity(newQuantity);
            cartMapper.updateById(existCart);
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(dto.getProductId());
            cart.setQuantity(dto.getQuantity());
            cart.setCreateTime(LocalDateTime.now());
            cartMapper.insert(cart);
        }
    }

    @Override
    public List<CartVO> list(Long userId) {
        return cartMapper.selectCartWithProduct(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQuantity(Long userId, CartUpdateDTO dto) {
        Cart cart = cartMapper.selectById(dto.getId());
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.CART_ITEM_NOT_FOUND);
        }

        if (!checkStock(cart.getProductId(), dto.getQuantity())) {
            throw new BusinessException(ResultCode.PRODUCT_STOCK_NOT_ENOUGH);
        }

        cart.setQuantity(dto.getQuantity());
        cartMapper.updateById(cart);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, Long id) {
        Cart cart = cartMapper.selectById(id);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.CART_ITEM_NOT_FOUND);
        }
        cartMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clear(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        cartMapper.delete(wrapper);
    }

    @Override
    public boolean checkStock(Long productId, Integer quantity) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return false;
        }
        return product.getStock() >= quantity;
    }

    @Override
    public Integer count(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        return Math.toIntExact(cartMapper.selectCount(wrapper));
    }
}
