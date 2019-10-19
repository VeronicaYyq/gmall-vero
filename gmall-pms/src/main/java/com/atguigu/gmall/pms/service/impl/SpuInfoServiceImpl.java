package com.atguigu.gmall.pms.service.impl;


import com.atguigu.gmall.pms.dao.*;
import com.atguigu.gmall.pmsInterface.entity.*;
import com.atguigu.gmall.pms.feign.GmallSmsFeign;
import com.atguigu.gmall.pms.service.ProductAttrValueService;
import com.atguigu.gmall.pms.service.SkuImagesService;
import com.atguigu.gmall.pms.service.SkuSaleAttrValueService;
import com.atguigu.gmall.pms.vo.BaseAttrVO;
import com.atguigu.gmall.pms.vo.SkuInfoVO;

import com.atguigu.gmall.pms.vo.SpuInfoVO;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.service.SpuInfoService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.atguigu.gmallsmsinterface.vo.SkuSaleVO;


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
    @Autowired
    private AmqpTemplate amqpTemplate;

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
    @GlobalTransactional
    @Override
    public void saveSpuInfoAndSku(SpuInfoVO spuInfoVO) {
        //保存spu的信息
        //首先保存spuInfoEntity的信息


        SpuInfoService proxy = (SpuInfoService) AopContext.currentProxy();

        Long spuId = proxy.saveSpuInfo(spuInfoVO);
        //但是spudescribetion还对应着spu_info_desc,所以要对应的存进去
        proxy.saveSpuDesc(spuInfoVO, spuId);

        proxy.saveProductAttrValue(spuInfoVO, spuId);

        //2.保存sku的信息

        proxy.saveSkuInfo(spuInfoVO, spuId);


            this.sendMsg("insert",spuId);


    }

        private void sendMsg(String key,Long spuId){
          //
            try {
                Map<String,Object> map=new HashMap<>();
                map.put("key",key);
                map.put("spuId",spuId);
                map.put("time",new Date());

                this.amqpTemplate.convertAndSend("GMALL-ITEM-EXCHANGE","item."+key,map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    @Transactional(propagation = Propagation.REQUIRED)
    public void saveSkuInfo(SpuInfoVO spuInfoVO, Long spuId) {
        List<SkuInfoVO> skus = spuInfoVO.getSkus();
        if(CollectionUtils.isEmpty(skus)){
           return;//如果为空的话直接返回
        }

        //2.1保存skuInfo的信息
        skus.forEach(skuInfoVO -> {
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(skuInfoVO,skuInfoEntity);
            skuInfoEntity.setBrandId(spuInfoVO.getBrandId());
            skuInfoEntity.setCatalogId(spuInfoVO.getCatalogId());
            skuInfoEntity.setSkuCode(UUID.randomUUID().toString());
            List<String> images = skuInfoVO.getImages();
          //  List<String> images = skuInfoVO.getSpuImages();
            if(!CollectionUtils.isEmpty(images)){

                skuInfoEntity.setSkuDefaultImg(StringUtils.isEmpty(skuInfoEntity.getSkuDefaultImg())? images.get(0):skuInfoEntity.getSkuDefaultImg());
            }

            skuInfoEntity.setSpuId(spuId);

            skuInfoDao.insert(skuInfoEntity);
            //2.2保存sku_images
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
            if(!CollectionUtils.isEmpty(saleAttrs)){
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

   /* @Override
    public PageVo querySpuInfoByStatus(QueryCondition queryCondition) {
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("publish_status",1);
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(queryCondition),
                queryWrapper
        );

        return new PageVo(page);

    }*/

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveProductAttrValue(SpuInfoVO spuInfoVO, Long spuId) {
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
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long saveSpuInfo(SpuInfoVO spuInfoVO) {
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfoVO,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUodateTime(spuInfoEntity.getCreateTime());
        spuInfoDao.insert(spuInfoEntity);
        return spuInfoEntity.getId();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSpuDesc(SpuInfoVO spuInfoVO, Long spuId) {

        //保存spu_info_desc的信息
        if(!CollectionUtils.isEmpty(spuInfoVO.getSpuImages())){
            SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
            spuInfoDescEntity.setSpuId(spuId);
            spuInfoDescEntity.setDecript(StringUtils.join(spuInfoVO.getSpuImages(),","));
            spuInfoDescDao.insert(spuInfoDescEntity);
        }
    }


}