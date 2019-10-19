package com.atguigu.gmall.pms.service;

import com.atguigu.core.bean.Resp;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pmsInterface.entity.SkuInfoEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.math.BigDecimal;
import java.util.List;


/**
 * sku信息
 *
 * @author Veronica
 * @email lxf@atguigu.com
 * @date 2019-09-22 12:07:40
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageVo queryPage(QueryCondition params);

    List<SkuInfoEntity> queSkuInfoFromSpu(Long spuId);


}

