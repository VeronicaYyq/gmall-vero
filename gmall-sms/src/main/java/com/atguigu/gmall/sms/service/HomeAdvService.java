package com.atguigu.gmall.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmallsmsinterface.entity.HomeAdvEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 首页轮播广告
 *
 * @author Veronica
 * @email ${email}
 * @date 2019-09-22 12:31:06
 */
public interface HomeAdvService extends IService<HomeAdvEntity> {

    PageVo queryPage(QueryCondition params);
}

