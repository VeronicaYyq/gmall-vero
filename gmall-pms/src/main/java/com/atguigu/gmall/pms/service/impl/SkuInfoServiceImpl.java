package com.atguigu.gmall.pms.service.impl;

import com.atguigu.core.bean.Resp;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.SkuInfoDao;
import com.atguigu.gmall.pmsInterface.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.service.SkuInfoService;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {
    @Autowired
    SkuInfoDao skuInfoDao;
    @Autowired
    private AmqpTemplate amqpTemplate;



    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public List<SkuInfoEntity> queSkuInfoFromSpu(Long spuId) {

        List<SkuInfoEntity> skuInfoEntities = skuInfoDao.selectList(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return skuInfoEntities;
    }



    /*@Override
    public Resp<Object> updateSku(Long skuId, BigDecimal price) {

        return skuInfoEntity;
    }*/

}