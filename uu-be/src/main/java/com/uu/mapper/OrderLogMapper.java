package com.uu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uu.entity.OrderLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单日志Mapper
 */
@Mapper
public interface OrderLogMapper extends BaseMapper<OrderLog> {
}