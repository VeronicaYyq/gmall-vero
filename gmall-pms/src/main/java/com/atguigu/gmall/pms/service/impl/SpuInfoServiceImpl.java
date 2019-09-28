package com.atguigu.gmall.pms.service.impl;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import com.atguigu.gmall.pms.dao.*;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feign.GmallSmsFeign;
import com.atguigu.gmall.pms.service.ProductAttrValueService;
import com.atguigu.gmall.pms.service.SkuImagesService;
import com.atguigu.gmall.pms.service.SkuSaleAttrValueService;
import com.atguigu.gmall.pms.vo.BaseAttrVO;
import com.atguigu.gmall.pms.vo.SkuInfoVO;

import com.atguigu.gmall.pms.vo.SpuInfoVO;
import javafx.scene.image.Image;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.service.SpuInfoService;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import vo.SkuSaleVO;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    SpuInfoDao spuInfoDao;
    @Autowired
    SpuImagesDao spuImagesDao;
    @Autowired
    ProductAttrValueService productAttrValueService;
    @Autowired
    SpuInfoDescDao spuInfoDescDao;
    @Autowired
    SkuInfoDao skuInfoDao;
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    GmallSmsFeign smsFeign;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo getSpuInfo(QueryCondition queryCondition, Long catId) {

        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("catalog_id",catId);
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(queryCondition),
                queryWrapper
        );

        return new PageVo(page);
    }

    @Override
    public void saveSpuInfoAndSku(SpuInfoVO spuInfoVO) {
        //首先保存spuInfoEntity的信息
       // Long spuId = spuInfoVO.getId();
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfoVO,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUodateTime(spuInfoEntity.getCreateTime());
        this.save(spuInfoEntity);
        Long spuId = spuInfoEntity.getId();
        //但是spudescribetion还对应着spu_info_desc,所以要对应的存进去
        //保存spu_info_desc的信息
        if(!CollectionUtils.isEmpty(spuInfoVO.getSpuImages())){
            SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
            spuInfoDescEntity.setDecript(StringUtils.join(spuInfoVO.getSpuImages(),","));
            spuInfoDescEntity.setSpuId(spuId);
            spuInfoDescDao.insert(spuInfoDescEntity);
        }


        //保存product——attr—value的信息
        List<BaseAttrVO> baseAttrs = spuInfoVO.getBaseAttrs();
        if(!CollectionUtils.isEmpty(baseAttrs)){
            List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream().map(baseAttr -> {
                ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
                valueEntity.setSpuId(spuId);
                valueEntity.setAttrName(baseAttr.getAttrName());
                valueEntity.setAttrSort(1);
                valueEntity.setAttrId(baseAttr.getAttrId());
                valueEntity.setAttrValue(baseAttr.getAttrValue());
                valueEntity.setQuickShow(0);

                return valueEntity;
            }).collect(Collectors.toList());

            productAttrValueService.saveBatch(productAttrValueEntities);
        }


        //2.保存sku的信息

        List<SkuInfoVO> skus = spuInfoVO.getSkus();
        if(CollectionUtils.isEmpty(skus)){
           return;//如果为空的话直接返回
        }
        //2.1保存skuInfo的信息
        skus.forEach(skuInfoVO -> {
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(skuInfoVO,skuInfoEntity);
            skuInfoEntity.setBrandId(skuInfoVO.getBrandId());
            skuInfoEntity.setCatalogId(skuInfoVO.getCatalogId());
            skuInfoEntity.setSkuCode(UUID.randomUUID().toString());
            List<String> images = skuInfoVO.getImages();
            if(!CollectionUtils.isEmpty(images)){
                skuInfoEntity.setSkuDefaultImg(StringUtils.isEmpty(skuInfoEntity.getSkuDefaultImg())? images.get(0):skuInfoEntity.getSkuDefaultImg());

            }

            skuInfoEntity.setSpuId(spuId);

            skuInfoDao.insert(skuInfoEntity);
            //2.2保存sku_imag
            Long skuId = skuInfoEntity.getSkuId();


            if(!CollectionUtils.isEmpty(images)){
                String defaultImage=images.get(0);
                List<SkuImagesEntity> skuImagesEntities = images.stream().map(image -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(image);
                    skuImagesEntity.setImgSort(1);
                    skuImagesEntity.setDefaultImg(StringUtils.equals(defaultImage, image) ? 1 : 0);
                    return skuImagesEntity;

                }).collect(Collectors.toList());

                skuImagesService.saveBatch(skuImagesEntities);
            }

            //2.3保存sku_attr_value的信息
            List<SkuSaleAttrValueEntity> saleAttrs = skuInfoVO.getSaleAttrs();
            if(CollectionUtils.isEmpty(saleAttrs)){
                saleAttrs.forEach(saleAttr -> {

                    saleAttr.setSkuId(skuId);

                    saleAttr.setAttrSort(1);

                });
                skuSaleAttrValueService.saveBatch(saleAttrs);
            }

            //3.保存和优惠相关的信息
            SkuSaleVO skuSaleVO = new SkuSaleVO();
            BeanUtils.copyProperties(skuInfoVO,skuSaleVO);
            skuSaleVO.setSkuId(skuId);
            smsFeign.saveSkuSaleInfo(skuSaleVO);
        });

    }


}