package com.atguigu.gmall.sms.dao;

import com.atguigu.gmallsmsinterface.entity.MemberPriceEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品会员价格
 * 
 * @author Veronica
 * @email ${email}
 * @date 2019-09-22 12:31:06
 */
@Mapper
public interface MemberPriceDao extends BaseMapper<MemberPriceEntity> {
	
}
