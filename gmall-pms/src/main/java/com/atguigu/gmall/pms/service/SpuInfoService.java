package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.SpuInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pmsInterface.entity.SpuInfoEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * spu信息
 *
 * @author Veronica
 * @email lxf@atguigu.com
 * @date 2019-09-22 12:07:40
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageVo queryPage(QueryCondition params);

    PageVo getSpuInfo(QueryCondition queryCondition, Long catId);

    void saveSpuInfoAndSku(SpuInfoVO spuInfo);

    Long saveSpuInfo(SpuInfoVO spuInfoVO);

    void saveSpuDesc(SpuInfoVO spuInfoVO, Long spuId);

    void saveProductAttrValue(SpuInfoVO spuInfoVO, Long spuId);

    void saveSkuInfo(SpuInfoVO spuInfoVO, Long spuId);

    //PageVo querySpuInfoByStatus(QueryCondition queryCondition);
}

