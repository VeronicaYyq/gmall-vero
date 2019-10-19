package com.atguigu.gmall.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pmsInterface.entity.SkuImagesEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * sku图片
 *
 * @author Veronica
 * @email lxf@atguigu.com
 * @date 2019-09-22 12:07:40
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageVo queryPage(QueryCondition params);

    List<SkuImagesEntity> skuImagesBySkuId(Long skuId);
}

