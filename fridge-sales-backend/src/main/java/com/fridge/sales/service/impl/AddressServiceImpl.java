package com.fridge.sales.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fridge.sales.common.PageResult;
import com.fridge.sales.common.ResultCode;
import com.fridge.sales.dto.AddressDTO;
import com.fridge.sales.entity.Address;
import com.fridge.sales.entity.User;
import com.fridge.sales.mapper.AddressMapper;
import com.fridge.sales.mapper.UserMapper;
import com.fridge.sales.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 地址服务实现类
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Address> list(Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getCreateTime);
        return addressMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(Long userId, AddressDTO dto) {
        Address address = new Address();
        address.setUserId(userId);
        address.setReceiverName(dto.getReceiverName());
        address.setReceiverPhone(dto.getReceiverPhone());
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setDetailAddress(dto.getDetailAddress());
        address.setIsDefault(0);
        address.setCreateTime(LocalDateTime.now());

        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId);
        Long count = addressMapper.selectCount(wrapper);
        if (count == 0) {
            address.setIsDefault(1);
        }

        addressMapper.insert(address);

        updateUserPhone(userId, dto.getReceiverPhone());

        return address.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long userId, AddressDTO dto) {
        Address address = addressMapper.selectById(dto.getId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("地址不存在");
        }

        address.setReceiverName(dto.getReceiverName());
        address.setReceiverPhone(dto.getReceiverPhone());
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setDetailAddress(dto.getDetailAddress());

        addressMapper.updateById(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, Long userId) {
        Address address = addressMapper.selectById(id);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("地址不存在");
        }

        addressMapper.deleteById(id);

        if (address.getIsDefault() == 1) {
            LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Address::getUserId, userId)
                    .orderByDesc(Address::getCreateTime)
                    .last("LIMIT 1");
            Address latestAddress = addressMapper.selectOne(wrapper);
            if (latestAddress != null) {
                latestAddress.setIsDefault(1);
                addressMapper.updateById(latestAddress);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Long id, Long userId) {
        Address address = addressMapper.selectById(id);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("地址不存在");
        }

        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId)
                .eq(Address::getIsDefault, 1);
        Address defaultAddress = addressMapper.selectOne(wrapper);
        if (defaultAddress != null) {
            defaultAddress.setIsDefault(0);
            addressMapper.updateById(defaultAddress);
        }

        address.setIsDefault(1);
        addressMapper.updateById(address);
    }

    @Override
    public Address getById(Long id, Long userId) {
        Address address = addressMapper.selectById(id);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("地址不存在");
        }
        return address;
    }

    private void updateUserPhone(Long userId, String phone) {
        if (phone == null || phone.isEmpty()) {
            return;
        }
        User user = userMapper.selectById(userId);
        if (user != null && (user.getPhone() == null || user.getPhone().isEmpty())) {
            user.setPhone(phone);
            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);
        }
    }
}
