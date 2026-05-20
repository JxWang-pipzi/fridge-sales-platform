package com.fridge.sales.service;

import com.fridge.sales.dto.AddressDTO;
import com.fridge.sales.entity.Address;

import java.util.List;

/**
 * 地址服务接口
 */
public interface AddressService {

    /**
     * 查询用户地址列表
     *
     * @param userId 用户ID
     * @return 地址列表
     */
    List<Address> list(Long userId);

    /**
     * 添加地址
     *
     * @param userId 用户ID
     * @param dto    地址DTO
     * @return 地址ID
     */
    Long add(Long userId, AddressDTO dto);

    /**
     * 更新地址
     *
     * @param userId 用户ID
     * @param dto    地址DTO
     */
    void update(Long userId, AddressDTO dto);

    /**
     * 删除地址
     *
     * @param id     地址ID
     * @param userId 用户ID
     */
    void delete(Long id, Long userId);

    /**
     * 设置默认地址
     *
     * @param id     地址ID
     * @param userId 用户ID
     */
    void setDefault(Long id, Long userId);

    /**
     * 根据ID查询地址
     *
     * @param id     地址ID
     * @param userId 用户ID
     * @return 地址
     */
    Address getById(Long id, Long userId);
}
