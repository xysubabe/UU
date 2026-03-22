package com.uu.controller.address;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uu.dto.response.ApiResponse;
import com.uu.dto.response.AddressResponse;
import com.uu.dto.response.IdStringResponse;
import com.uu.dto.request.AddressCreateRequest;
import com.uu.dto.request.AddressUpdateRequest;
import com.uu.interceptor.LoginInterceptor;
import com.uu.service.AddressService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 地址控制器
 */
@Slf4j
@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 获取地址列表
     */
    @GetMapping("/list")
    public ApiResponse<Page<AddressResponse>> getAddressList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long userId = LoginInterceptor.getUserId(request);
        log.info("获取地址列表, userId={}", userId);
        Page<AddressResponse> response = addressService.getAddressList(userId, page, pageSize);
        return ApiResponse.success(response);
    }

    /**
     * 获取默认地址
     */
    @GetMapping("/default")
    public ApiResponse<AddressResponse> getDefaultAddress(HttpServletRequest request) {
        Long userId = LoginInterceptor.getUserId(request);
        log.info("获取默认地址, userId={}", userId);
        AddressResponse response = addressService.getDefaultAddress(userId);
        return ApiResponse.success(response);
    }

    /**
     * 新增地址
     */
    @PostMapping("/create")
    public ApiResponse<IdStringResponse> createAddress(
            @Valid @RequestBody AddressCreateRequest request,
            HttpServletRequest httpRequest) {
        Long userId = LoginInterceptor.getUserId(httpRequest);
        log.info("新增地址, userId={}", userId);
        Long addressId = addressService.createAddress(userId, request);
        return ApiResponse.success(IdStringResponse.of(addressId));
    }

    /**
     * 更新地址
     */
    @PutMapping("/update")
    public ApiResponse<Void> updateAddress(
            @Valid @RequestBody AddressUpdateRequest request,
            HttpServletRequest httpRequest) {
        Long userId = LoginInterceptor.getUserId(httpRequest);
        log.info("更新地址, userId={}, addressId={}", userId, request.getId());
        addressService.updateAddress(userId, request);
        return ApiResponse.success(null);
    }

    /**
     * 删除地址
     */
    @DeleteMapping("/delete/{addressId}")
    public ApiResponse<Void> deleteAddress(
            @PathVariable Long addressId,
            HttpServletRequest request) {
        Long userId = LoginInterceptor.getUserId(request);
        log.info("删除地址, userId={}, addressId={}", userId, addressId);
        addressService.deleteAddress(userId, addressId);
        return ApiResponse.success(null);
    }
}