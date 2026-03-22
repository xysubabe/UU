package com.uu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uu.entity.Payment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付Mapper
 */
@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
}