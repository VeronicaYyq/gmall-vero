package com.atguigu.gmall.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.wmsInterface.entity.WareSkuEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * 商品库存
 *
 * @author Veronica
 * @email ${email}
 * @date 2019-09-22 12:35:01
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageVo queryPage(QueryCondition params);

    List<WareSkuEntity> getSkuWareInfo(Long skuId);
}

