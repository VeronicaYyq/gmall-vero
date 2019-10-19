package com.atguigu.gmall.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmallsmsinterface.entity.SeckillSkuRelationEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 秒杀活动商品关联
 *
 * @author Veronica
 * @email ${email}
 * @date 2019-09-22 12:31:06
 */
public interface SeckillSkuRelationService extends IService<SeckillSkuRelationEntity> {

    PageVo queryPage(QueryCondition params);
}

