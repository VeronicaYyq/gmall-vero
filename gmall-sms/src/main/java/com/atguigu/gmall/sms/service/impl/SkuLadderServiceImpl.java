package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.dao.SkuBoundsDao;
import com.atguigu.gmall.sms.dao.SkuFullReductionDao;
import com.atguigu.gmallsmsinterface.entity.SkuBoundsEntity;
import com.atguigu.gmallsmsinterface.entity.SkuFullReductionEntity;
import com.atguigu.gmallsmsinterface.vo.SaleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.sms.dao.SkuLadderDao;
import com.atguigu.gmallsmsinterface.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.service.SkuLadderService;


@Service("skuLadderService")
public class SkuLadderServiceImpl extends ServiceImpl<SkuLadderDao, SkuLadderEntity> implements SkuLadderService {


    @Autowired
    private SkuLadderDao skuLadderDao;
    @Autowired
    private SkuFullReductionDao skuFullReductionDao;
    @Autowired
    private SkuBoundsDao skuBoundsDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuLadderEntity> page = this.page(
                new Query<SkuLadderEntity>().getPage(params),
                new QueryWrapper<SkuLadderEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public List<SaleVO> querySaleVObySkuId(Long skuId) {

        List<SkuLadderEntity> skuLadderEntities = skuLadderDao.selectList(new QueryWrapper<SkuLadderEntity>().eq("sku_id", skuId));
        List<SkuFullReductionEntity> skuFullReductionEntities = skuFullReductionDao.selectList(new QueryWrapper<SkuFullReductionEntity>().eq("sku_id", skuId));
        List<SkuBoundsEntity> skuBoundsEntities = skuBoundsDao.selectList(new QueryWrapper<SkuBoundsEntity>().eq("sku_id", skuId));
        List<SaleVO> saleVOS=new ArrayList<>();

        skuBoundsEntities.forEach(skuBoundsEntity -> {
            saleVOS.add(new SaleVO("积分", "赠送" + skuBoundsEntity.getBuyBounds() + "积分"));
        });

        skuLadderEntities.forEach(skuLadderEntity -> {
            saleVOS.add(new SaleVO("打折", "购买" + skuLadderEntity.getFullCount() + "件，打" + skuLadderEntity.getDiscount().divide(new BigDecimal(10)) + "折"));
        });

        skuFullReductionEntities.forEach(skuFullReductionEntity -> {
            saleVOS.add(new SaleVO("满减", "满" + skuFullReductionEntity.getFullPrice() + "减" + skuFullReductionEntity.getReducePrice()));
        });
        return saleVOS;
    }

}