package com.atguigu.gmall.item.service;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.item.feign.GmallPmsClient;
import com.atguigu.gmall.item.feign.GmallSmsClient;
import com.atguigu.gmall.item.feign.GmallWmsClient;
import com.atguigu.gmall.item.vo.ItemVO;
import com.atguigu.gmall.pmsInterface.entity.*;
import com.atguigu.gmall.pmsInterface.vo.GroupVO;


import com.atguigu.gmallsmsinterface.vo.SaleVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.nio.ch.ThreadPool;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;


@Service
public class ItemService {
    @Autowired
    private GmallPmsClient gmallPmsClient;
    @Autowired
    private GmallSmsClient gmallSmsClient;
    @Autowired
    private GmallWmsClient gmallWmsClient;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;


    public ItemVO queryItemVO(Long skuId) throws ExecutionException, InterruptedException {
        ItemVO itemVO = new ItemVO();
        // sku信息，skuId-->sku
        CompletableFuture<SkuInfoEntity> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            Resp<SkuInfoEntity> skuInfoEntityResp = gmallPmsClient.skuInfoBySkuId(skuId);
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            if (skuInfoEntity!= null) {
                itemVO.setSkuId(skuId);
                itemVO.setSubTitle(skuInfoEntity.getSkuSubtitle());
                itemVO.setTitle(skuInfoEntity.getSkuTitle());
                itemVO.setPrice(skuInfoEntity.getPrice());
                itemVO.setWeight(skuInfoEntity.getWeight());
            }
            return skuInfoEntity;
        }, threadPoolExecutor);

        CompletableFuture<Void> productAttrValueFuture = skuInfoFuture.thenAcceptAsync((skuInfoEntity) -> {
            Resp<List<ProductAttrValueEntity>> productAttrValueResp = gmallPmsClient.querySearchAttrValue(skuInfoEntity.getSpuId());
            List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueResp.getData();
            itemVO.setBaseAttrs(productAttrValueEntities);
            //return skuInfoEntity;
        }, threadPoolExecutor);


        // sku的图片信息，根据skuId-->当前sku的图片
       CompletableFuture<Void> skuImagesFuture = CompletableFuture.runAsync(() -> {
            Resp<List<SkuImagesEntity>> skuImagesResp = gmallPmsClient.skuImagesBySkuId(skuId);
            List<SkuImagesEntity> skuImagesEntities = skuImagesResp.getData();
            itemVO.setImages(skuImagesEntities);
        },threadPoolExecutor);


        // 分类 sku.getCatId --> 根据catId查询分类
        CompletableFuture<Void> categoryFuture = skuInfoFuture.thenAcceptAsync(skuInfoEntity -> {
            Resp<CategoryEntity> categoryResp = gmallPmsClient.info(skuInfoEntity.getCatalogId());
            CategoryEntity categoryEntity = categoryResp.getData();
            itemVO.setCid3(categoryEntity.getCatId());
            itemVO.setCategoryName(categoryEntity.getName());
        }, threadPoolExecutor);


        // 品牌 sku.getBrandId --> 根据brandId查询品牌
        CompletableFuture<Void>  brandEntityFuture = skuInfoFuture.thenAcceptAsync(skuInfoEntity -> {
            Resp<BrandEntity> brandEntityResp = gmallPmsClient.infoBrand(skuInfoEntity.getBrandId());
            BrandEntity brandEntity = brandEntityResp.getData();
            itemVO.setBrandName(brandEntity.getName());
            itemVO.setBrandId(skuInfoEntity.getBrandId());
        }, threadPoolExecutor);

        //spu信息 sku.getSpuId --> 根据spuId查询Spu
        CompletableFuture<Void> spuInfoEntityFuture = skuInfoFuture.thenAcceptAsync(skuInfoEntity -> {
            Resp<SpuInfoEntity> spuInfoEntityResp = gmallPmsClient.spuInfoById(skuInfoEntity.getSpuId());
            SpuInfoEntity spuInfoEntity = spuInfoEntityResp.getData();
            itemVO.setSpuId(spuInfoEntity.getId());
            itemVO.setSpuName(spuInfoEntity.getSpuName());
       }, threadPoolExecutor);


         //促销 O  skuId-->根据skuId直接查询三张表
        CompletableFuture<Void> saleVoFuture = CompletableFuture.runAsync(() -> {
            Resp<List<SaleVO>> saleVoResp = gmallSmsClient.querySaleVObySkuId(skuId);
            List<SaleVO> saleVOS = saleVoResp.getData();
            itemVO.setSales(saleVOS);
        }, threadPoolExecutor);


        CompletableFuture<Void> roupVOSFuture = skuInfoFuture.thenAcceptAsync(skuInfoEntity -> {
            Resp<List<GroupVO>> groupVOResp = gmallPmsClient.attrGroupsBycatId(skuInfoEntity.getCatalogId(), skuInfoEntity.getSpuId(), skuId);
            List<GroupVO> groupVOS = groupVOResp.getData();
            itemVO.setGroups(groupVOS);
        }, threadPoolExecutor);

        CompletableFuture<Void> skuSaleAttrValueFuture = CompletableFuture.runAsync(() -> {
            Resp<List<SkuSaleAttrValueEntity>> saleAttrlistResp = gmallPmsClient.saleInfoBySkuId(skuId);
            List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = saleAttrlistResp.getData();
            itemVO.setSaleAttrs(skuSaleAttrValueEntities);
       }, threadPoolExecutor);


        CompletableFuture<Void> spuInfoDescEntityFuture = skuInfoFuture.thenAcceptAsync(skuInfoEntity -> {
            Resp<SpuInfoDescEntity> spuInfoDescEntityResp = gmallPmsClient.spuDescBySpuId(skuInfoEntity.getSpuId());
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescEntityResp.getData();
            itemVO.setDescription(Arrays.asList(StringUtils.split(spuInfoDescEntity.getDecript())));
        }, threadPoolExecutor);
        CompletableFuture<Void> future = CompletableFuture.allOf(skuInfoFuture, spuInfoDescEntityFuture, skuSaleAttrValueFuture, roupVOSFuture, saleVoFuture
                , spuInfoEntityFuture, brandEntityFuture, categoryFuture, skuImagesFuture, productAttrValueFuture);
        future.join();

        return itemVO;

    }
}
