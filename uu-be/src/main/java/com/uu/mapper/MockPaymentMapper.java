package com.uu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uu.entity.MockPayment;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mock支付Mapper
 */
@Mapper
public interface MockPaymentMapper extends BaseMapper<MockPayment> {
}