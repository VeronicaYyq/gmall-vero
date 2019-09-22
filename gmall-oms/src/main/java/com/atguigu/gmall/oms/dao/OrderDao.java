package com.atguigu.gmall.oms.dao;

import com.atguigu.gmall.oms.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author Veronica
 * @email ${email}
 * @date 2019-09-22 12:26:29
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
