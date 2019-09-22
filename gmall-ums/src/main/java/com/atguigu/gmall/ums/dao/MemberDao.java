package com.atguigu.gmall.ums.dao;

import com.atguigu.gmall.ums.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author Veronica
 * @email ${email}
 * @date 2019-09-22 12:33:45
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
