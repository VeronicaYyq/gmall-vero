package com.atguigu.gmall.pms.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.ProductAttrValueDao;
import com.atguigu.gmall.pmsInterface.entity.ProductAttrValueEntity;
import com.atguigu.gmall.pms.service.ProductAttrValueService;

import java.util.List;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {
    @Autowired
    ProductAttrValueService productAttrValueService;


    @Autowired
    ProductAttrValueDao productAttrValueDao;


    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public List<ProductAttrValueEntity> querySearchAttrValue(Long spuId) {
        List<ProductAttrValueEntity> productAttrValueEntities = this.productAttrValueDao.querySearchAttrValue(spuId);
        return productAttrValueEntities;
    }


}