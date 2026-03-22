package com.uu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uu.dto.request.AddressCreateRequest;
import com.uu.dto.request.AddressUpdateRequest;
import com.uu.dto.response.AddressResponse;
import com.uu.entity.Address;

import java.util.List;

/**
 * 地址服务接口
 */
public interface AddressService {

    /**
     * 获取地址列表
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 地址列表
     */
    Page<AddressResponse> getAddressList(Long userId, Integer page, Integer pageSize);

    /**
     * 获取默认地址
     * @param userId 用户ID
     * @return 默认地址
     */
    AddressResponse getDefaultAddress(Long userId);

    /**
     * 新增地址
     * @param userId 用户ID
     * @param request 创建请求
     * @return 创建的地址ID
     */
    Long createAddress(Long userId, AddressCreateRequest request);

    /**
     * 更新地址
     * @param userId 用户ID
     * @param request 更新请求
     */
    void updateAddress(Long userId, AddressUpdateRequest request);

    /**
     * 删除地址（逻辑删除）
     * @param userId 用户ID
     * @param addressId 地址ID
     */
    void deleteAddress(Long userId, Long addressId);

    /**
     * 根据ID获取地址
     * @param addressId 地址ID
     * @return 地址实体
     */
    Address getById(Long addressId);

    /**
     * 验证地址所有权
     * @param userId 用户ID
     * @param addressId 地址ID
     * @return 地址实体
     */
    Address validateOwnership(Long userId, Long addressId);

    /**
     * 转换为响应DTO
     * @param address 地址实体
     * @return 响应DTO
     */
    AddressResponse toResponse(Address address);
}