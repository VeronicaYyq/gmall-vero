package com.atguigu.gmall.sms.dao;

import com.atguigu.gmallsmsinterface.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author Veronica
 * @email ${email}
 * @date 2019-09-22 12:31:06
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
