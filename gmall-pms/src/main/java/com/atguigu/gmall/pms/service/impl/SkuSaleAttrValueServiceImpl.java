package com.atguigu.gmall.pms.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.SkuSaleAttrValueDao;
import com.atguigu.gmall.pmsInterface.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.pms.service.SkuSaleAttrValueService;

import java.util.List;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {
    @Autowired
    private SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Override
    public PageVo queryPage(QueryCondition queryCondition) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(queryCondition),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public List<SkuSaleAttrValueEntity> querySaleAttrVOSBySpuId(Long spuId) {
        List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = skuSaleAttrValueDao.selectList(new QueryWrapper<SkuSaleAttrValueEntity>().eq("spu_id", spuId));

        return skuSaleAttrValueEntities;
    }


}