package com.uu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uu.entity.Address;
import org.apache.ibatis.annotations.Mapper;

/**
 * 地址Mapper
 */
@Mapper
public interface AddressMapper extends BaseMapper<Address> {
}