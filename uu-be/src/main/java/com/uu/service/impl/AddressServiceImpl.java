package com.uu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uu.dto.request.AddressCreateRequest;
import com.uu.dto.request.AddressUpdateRequest;
import com.uu.dto.response.AddressResponse;
import com.uu.entity.Address;
import com.uu.enums.ErrorCodeEnum;
import com.uu.exception.BusinessException;
import com.uu.mapper.AddressMapper;
import com.uu.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 地址服务实现
 */
@Slf4j
@Service
public class AddressServiceImpl implements AddressService {

    private static final int MAX_ADDRESS_COUNT = 3;

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public Page<AddressResponse> getAddressList(Long userId, Integer page, Integer pageSize) {
        // 查询启用状态的地址
        LambdaQueryWrapper<Address> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Address::getUserId, userId)
                .eq(Address::getStatus, 1)
                .orderByAsc(Address::getIsDefault)
                .orderByDesc(Address::getCreateAt);

        // 分页查询
        Page<Address> pageResult = addressMapper.selectPage(
                new Page<>(page, pageSize),
                queryWrapper
        );

        // 转换为响应DTO
        Page<AddressResponse> responsePage = new Page<>(pageResult.getCurrent(), pageResult.getSize(), pageResult.getTotal());
        List<AddressResponse> responseList = pageResult.getRecords().stream()
                .map(this::toResponse)
                .toList();
        responsePage.setRecords(responseList);

        log.info("获取地址列表, userId={}, total={}", userId, pageResult.getTotal());
        return responsePage;
    }

    @Override
    public AddressResponse getDefaultAddress(Long userId) {
        LambdaQueryWrapper<Address> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Address::getUserId, userId)
                .eq(Address::getIsDefault, 1)
                .eq(Address::getStatus, 1)
                .last("LIMIT 1");

        Address address = addressMapper.selectOne(queryWrapper);
        if (address == null) {
            return null;
        }

        log.info("获取默认地址, userId={}, addressId={}", userId, address.getId());
        return toResponse(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAddress(Long userId, AddressCreateRequest request) {
        // 检查地址数量
        long currentCount = addressMapper.selectCount(
                new LambdaQueryWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .eq(Address::getStatus, 1)
        );

        if (currentCount >= MAX_ADDRESS_COUNT) {
            throw new BusinessException(ErrorCodeEnum.ADDRESS_LIMIT_EXCEEDED);
        }

        // 创建地址
        Address address = new Address();
        BeanUtils.copyProperties(request, address);
        address.setUserId(userId);
        address.setStatus(1);

        // 设置默认地址
        if (request.getIsDefault() != null && request.getIsDefault() == 1) {
            // 取消其他地址的默认状态
            LambdaUpdateWrapper<Address> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Address::getUserId, userId)
                    .set(Address::getIsDefault, 0);
            addressMapper.update(null, updateWrapper);
        } else {
            // 如果没有设置默认地址，且用户没有默认地址，则设置为默认地址
            long defaultCount = addressMapper.selectCount(
                    new LambdaQueryWrapper<Address>()
                            .eq(Address::getUserId, userId)
                            .eq(Address::getIsDefault, 1)
                            .eq(Address::getStatus, 1)
            );
            if (defaultCount == 0) {
                address.setIsDefault(1);
            }
        }

        addressMapper.insert(address);
        log.info("创建地址成功, userId={}, addressId={}", userId, address.getId());
        return address.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(Long userId, AddressUpdateRequest request) {
        // 验证地址所有权
        Address address = validateOwnership(userId, request.getId());

        // 设置默认地址
        if (request.getIsDefault() != null && request.getIsDefault() == 1) {
            // 取消其他地址的默认状态
            LambdaUpdateWrapper<Address> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Address::getUserId, userId)
                    .set(Address::getIsDefault, 0);
            addressMapper.update(null, updateWrapper);
        }

        // 更新地址
        BeanUtils.copyProperties(request, address);
        addressMapper.updateById(address);

        log.info("更新地址成功, userId={}, addressId={}", userId, address.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAddress(Long userId, Long addressId) {
        // 验证地址所有权
        Address address = validateOwnership(userId, addressId);

        // 逻辑删除
        address.setStatus(0);
        addressMapper.updateById(address);

        log.info("删除地址成功, userId={}, addressId={}", userId, addressId);
    }

    @Override
    public Address getById(Long addressId) {
        Address address = addressMapper.selectById(addressId);
        if (address == null) {
            throw new BusinessException(ErrorCodeEnum.ADDRESS_NOT_FOUND);
        }
        return address;
    }

    @Override
    public Address validateOwnership(Long userId, Long addressId) {
        Address address = getById(addressId);

        if (!address.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCodeEnum.FORBIDDEN);
        }

        return address;
    }

    @Override
    public AddressResponse toResponse(Address address) {
        AddressResponse response = new AddressResponse();
        response.setId(address.getId().toString());
        response.setContactName(address.getContactName());
        response.setContactPhone(maskPhone(address.getContactPhone()));
        response.setProvince(address.getProvince());
        response.setCity(address.getCity());
        response.setDistrict(address.getDistrict());
        response.setDetailAddress(address.getDetailAddress());
        response.setFullAddress(buildFullAddress(address));
        response.setIsDefault(address.getIsDefault());
        return response;
    }

    /**
     * 手机号脱敏
     */
    private String maskPhone(String phone) {
        if (!StringUtils.hasText(phone) || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 构建完整地址
     */
    private String buildFullAddress(Address address) {
        return address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress();
    }
}